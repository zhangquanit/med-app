package net.medlinker.imbusiness.util;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import net.medlinker.im.MsgConstants;
import net.medlinker.im.helper.EntityConvertHelper;
import net.medlinker.im.helper.MsgDbManager;
import net.medlinker.im.realm.ImGroupDbEntity;
import net.medlinker.im.realm.ImUserDbEntity;
import net.medlinker.im.realm.MsgSessionDbEntity;
import net.medlinker.imbusiness.entity.ImUserInfoEntity;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description IM业务的跳转工具类
 * @time 2017/3/16
 */
public class ImForwardUtil {

    /**
     * 处理跳转协议到单聊
     *
     * @param context
     * @param dataUri
     * @return
     */
    public static String transferPrivateChat(Context context, Uri dataUri) {
        String id = dataUri.getQueryParameter("userId");
        String avatar = dataUri.getQueryParameter("avatar");
        String name = dataUri.getQueryParameter("name");
        String type = dataUri.getQueryParameter("type");
        String reference = dataUri.getQueryParameter("reference");
        String phone = dataUri.getQueryParameter("phoneNum");
        //额外医生参数,经纪人跳转需要
        String cuserId = dataUri.getQueryParameter("cuserId");
        String cavatar = dataUri.getQueryParameter("cavatar");
        String cname = dataUri.getQueryParameter("cname");
        String csectionName = dataUri.getQueryParameter("csectionName");
        String ctitleName = dataUri.getQueryParameter("ctitleName");
        String chospital = dataUri.getQueryParameter("chospital");
        String ctype = dataUri.getQueryParameter("ctype");
        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(type) || TextUtils.isEmpty(name)) {
            return null;//如果会话的信息不全，则返回null
        }
        //构建一个目标用户类
        ImUserInfoEntity user = new ImUserInfoEntity();
        user.setUserId(Long.parseLong(id));
        user.setAvatar(avatar);
        user.setName(name);
        user.setType(Integer.parseInt(type));
        int ref = 0;
        try {
            ref = Integer.parseInt(reference);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        buildPrivateSession(user, ref);
        return EntityConvertHelper.getUserSessionId(Long.parseLong(id), ref);
    }

    /**
     * 群聊intent转化
     *
     * @param context
     * @param dataUri
     * @return
     */
    public static String transferGroupChat(Context context, Uri dataUri) {
        final String groupId = dataUri.getQueryParameter("groupId");
        final String type = dataUri.getQueryParameter("type");//群聊类型，
        final String name = dataUri.getQueryParameter("name") == null ? "群聊" : dataUri.getQueryParameter("name");
        final String avatar = dataUri.getQueryParameter("avatar") == null ? "" : dataUri.getQueryParameter("avatar");
        final String groupMemberCount = dataUri.getQueryParameter("membersNum");
        final String businessId = dataUri.getQueryParameter("businessId");
        if (TextUtils.isEmpty(groupId)) {
            return null;
        }
        long longGroupId = Long.parseLong(groupId);
        int intType = TextUtils.isEmpty(type) ? MsgConstants.SESSION_TYPE_COMMON : Integer.parseInt(type);
        List<String> avatars = new ArrayList<>(1);
        avatars.add(avatar);
        buildGroupAndSession(longGroupId, name, avatars, intType, businessId, groupMemberCount);

        return EntityConvertHelper.getGroupSessionId(longGroupId);
    }

    public static void buildPrivateSession(final ImUserInfoEntity user, final int refrence) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    boolean isSuiZhen = user.getType() == MsgConstants.CHAT_USER_TYPE_PATIENT;
                    String sessId = EntityConvertHelper.getUserSessionId(user.getUserId(), refrence);
                    MsgSessionDbEntity msgSessionDbEntity = MsgDbManager.INSTANCE.getSessionDbDao().getSessionById(sessId, realm);
                    if (msgSessionDbEntity == null) {
                        msgSessionDbEntity = new MsgSessionDbEntity();
                        msgSessionDbEntity.setId(sessId);
                        msgSessionDbEntity.setSessionId(sessId);
                        ImUserDbEntity imUserDbEntity = new ImUserDbEntity();
                        imUserDbEntity.setId(user.getUserId());
                        msgSessionDbEntity.setFromUser(imUserDbEntity);
//                        if (user.isImAdmin()) {
//                            buildAdminMsg(realm, imUserDbEntity, msgSessionDbEntity);
//                        }
                    }
                    //更新一些附属的信息状态
                    if (refrence == 1) {
//                        msgSessionDbEntity.setSessionType(MsgConstants.SESSION_TYPE_TEAM);
//                        if (!TextUtils.isEmpty(user.getUserPhoneNo())) { //新增经纪人联系电话
//                            msgSessionDbEntity.getFromUser()
//                                    .setBrokerPhoneNum(user.getUserPhoneNo());
//                        }
                    }
//                    if (isSuiZhen) {
//                        msgSessionDbEntity.setSessionType(MsgConstants.SESSION_TYPE_NORMAL_PATIENT);
//                    }

                    msgSessionDbEntity.setGroup(false);
                    msgSessionDbEntity.getFromUser().setName(user.getName());
                    msgSessionDbEntity.getFromUser().setType(user.getType());
                    msgSessionDbEntity.getFromUser().setAvatar(user.getAvatar());
                    msgSessionDbEntity.getFromUser().setRefrence(refrence);
                    msgSessionDbEntity.setTimeStamp(System.currentTimeMillis());
                    realm.copyToRealmOrUpdate(msgSessionDbEntity);
                }
            });
        }
    }

    public static void buildGroupAndSession(final long id, final String name, final List<String> avatars,
                                            final int type, final String businessId, final String count) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    //先拿到数据创建一个群的信息
                    ImGroupDbEntity groupDbEntity = realm.where(ImGroupDbEntity.class)
                            .equalTo("id", id).findFirst();
                    if (groupDbEntity == null) {
                        groupDbEntity = new ImGroupDbEntity();
                        groupDbEntity.setId(id);
                        groupDbEntity.setName(name);
                        groupDbEntity.setAvatarList(avatars);
                        groupDbEntity.setBusinessType(type);
                        if (!TextUtils.isEmpty(businessId)) {
                            groupDbEntity.setBusinessId(Long.parseLong(businessId));
                        }
                        if (!TextUtils.isEmpty(count)) {
                            groupDbEntity.setAmount(Integer.parseInt(count));
                        }
                        realm.copyToRealmOrUpdate(groupDbEntity);
                    }
                    //构建会话信息
                    String sessId = EntityConvertHelper.getGroupSessionId(id);
                    MsgSessionDbEntity msgSessionDbEntity = MsgDbManager.INSTANCE.getSessionDbDao().getSessionById(sessId, realm);
                    if (msgSessionDbEntity == null) { //如果为空，会话基本属性新建
                        msgSessionDbEntity = new MsgSessionDbEntity();
                        msgSessionDbEntity.setId(sessId);
                        msgSessionDbEntity.setSessionId(sessId);
                        msgSessionDbEntity.setSessionType(type);
                    }
//                    updateSessionByHospital(msgSessionDbEntity, sessId);
                    msgSessionDbEntity.setGroup(true);
                    msgSessionDbEntity.setFromGroup(groupDbEntity);
                    msgSessionDbEntity.setTimeStamp(System.currentTimeMillis());
                    realm.copyToRealmOrUpdate(msgSessionDbEntity);
                }
            });
        }
    }

}