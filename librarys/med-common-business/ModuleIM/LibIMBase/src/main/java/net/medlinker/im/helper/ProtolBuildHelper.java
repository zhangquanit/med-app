package net.medlinker.im.helper;

import com.medlinker.protocol.message.MessageOuterClass;
import com.medlinker.protocol.message.commands.FriendOperationOuterClass;
import com.medlinker.protocol.message.commands.GroupOperationOuterClass;
import com.medlinker.protocol.message.commands.LoginOuterClass;
import com.medlinker.protocol.message.commands.WithdrawOuterClass;
import com.medlinker.protocol.message.misc.GroupOuterClass;
import com.medlinker.protocol.message.misc.UserOuterClass;
import com.medlinker.protocol.message.types.BusinessCardOuterClass;
import com.medlinker.protocol.message.types.CardOuterClass;
import com.medlinker.protocol.message.types.ImageOuterClass;
import com.medlinker.protocol.message.types.JsonOuterClass;
import com.medlinker.protocol.message.types.NoticeOuterClass;
import com.medlinker.protocol.message.types.TextOuterClass;
import com.medlinker.protocol.message.types.VoiceOuterClass;

import net.medlinker.im.BuildConfig;
import net.medlinker.im.entity.UserInfoEntity;
import net.medlinker.im.realm.ImUserDbEntity;
import net.medlinker.im.realm.MsgDbEntity;
import net.medlinker.im.router.ModuleIMManager;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description 构建protolbuffer数据结构，用来rpc请求服务器传递数据
 * @time 2017/3/1
 */
public class ProtolBuildHelper {

    private static final int PLAT_FROM = 0;
    private static final int DEVICE = 0;

    /**
     * 构建登录的message信息
     *
     * @param tocken
     * @return
     */
    public static MessageOuterClass.Message getLoginMessage(String tocken) {
        return MessageOuterClass.Message.newBuilder()
                .setFrom(getCurentUser())
                .setUser(getSystemUser(1))
                .setLogin(
                        LoginOuterClass.Login.newBuilder()
                                .setDevice(DEVICE)
                                .setPlatform(PLAT_FROM)
                                .setToken(tocken)
                                .setSessionKey(ModuleIMManager.INSTANCE.getIMService().getOrigSession())
                                .setVersion(BuildConfig.VERSION_NAME)
                                .build()
                )
                .build();
    }

    /**
     * 发送一个json type过去
     *
     * @param msgDbEntity
     * @return
     */
    public static MessageOuterClass.Message getJsonMessage(MsgDbEntity msgDbEntity) {
        if (msgDbEntity.isGroup()) {
            return MessageOuterClass.Message.newBuilder()
                    .setFrom(getCurentUser())
                    .setGroup(GroupOuterClass.Group.newBuilder().setId(msgDbEntity.getToGroup().getId()).build())
                    .setJson(JsonOuterClass.Json.newBuilder()
                            .setContent(msgDbEntity.getJsonString())
                            .setType(msgDbEntity.getJsonType()))
                    .setExt(getExtJson(msgDbEntity))
                    .build();

        } else {
            return MessageOuterClass.Message.newBuilder()
                    .setFrom(getCurentUser())
                    .setUser(UserOuterClass.User.newBuilder()
                            .setId(msgDbEntity.getToUser().getId())
                            .setType(msgDbEntity.getToUser().getType())
                            .setReference(msgDbEntity.getToUser().getRefrence()).build())
                    .setJson(
                            JsonOuterClass.Json.newBuilder()
                                    .setContent(msgDbEntity.getJsonString())
                                    .setType(msgDbEntity.getJsonType())
                    )
                    .setExt(getExtJson(msgDbEntity))
                    .build();
        }
    }

    /**
     * 构建消息内容
     *
     * @return
     */

    public static MessageOuterClass.Message getTextMessage(MsgDbEntity msgDbEntity) {
        if (msgDbEntity.isGroup()) {
            return MessageOuterClass.Message.newBuilder()
                    .setFrom(getCurentUser())
                    .setGroup(GroupOuterClass.Group.newBuilder().setId(msgDbEntity.getToGroup().getId()).build())
                    .setText(TextOuterClass.Text.newBuilder().setContent(msgDbEntity.getTextContent())
                            .addAllAtUsers(getAllAtUsers(msgDbEntity.getAtUsers()))
                            .build())
                    .setExt(getExtJson(msgDbEntity))
                    .build();
        } else {
            return MessageOuterClass.Message.newBuilder()
                    .setFrom(getCurentUser())
                    .setUser(UserOuterClass.User.newBuilder().setId(msgDbEntity.getToUser().getId()).setType(msgDbEntity.getToUser().getType()).setReference(msgDbEntity.getToUser().getRefrence()).build())
                    .setText(TextOuterClass.Text.newBuilder().setContent(msgDbEntity.getTextContent()).build())
                    .setExt(getExtJson(msgDbEntity))
                    .build();
        }
    }

    private static Iterable<? extends UserOuterClass.User> getAllAtUsers(RealmList<ImUserDbEntity> atUsers) {
        List<UserOuterClass.User> list = new ArrayList<>();
        for (ImUserDbEntity atUser : atUsers) {
            list.add(UserOuterClass.User.newBuilder().setId(atUser.getId()).setName(atUser.getName()).build());
        }
        return list;
    }

    /**
     * 发送语音消息
     *
     * @return
     */
    public static MessageOuterClass.Message getVoiceMessage(MsgDbEntity msgDbEntity) {
        if (msgDbEntity.isGroup()) {
            return MessageOuterClass.Message.newBuilder()
                    .setFrom(getCurentUser())
                    .setGroup(GroupOuterClass.Group.newBuilder().setId(msgDbEntity.getToGroup().getId()).build())
                    .setVoice(VoiceOuterClass.Voice.newBuilder().setDuration(msgDbEntity.getVoiceDuration()).setUrl(msgDbEntity.getVoiceUrl()).build())
                    .setExt(getExtJson(msgDbEntity))
                    .build();
        } else {
            return MessageOuterClass.Message.newBuilder()
                    .setFrom(getCurentUser())
                    .setUser(UserOuterClass.User.newBuilder().setId(msgDbEntity.getToUser().getId()).setType(msgDbEntity.getToUser().getType()).setReference(msgDbEntity.getToUser().getRefrence()).build())
                    .setVoice(VoiceOuterClass.Voice.newBuilder().setUrl(msgDbEntity.getVoiceUrl()).setDuration(msgDbEntity.getVoiceDuration()).build())
                    .setExt(getExtJson(msgDbEntity))
                    .build();
        }
    }


    /**
     * 发送图片
     *
     * @return
     */

    public static MessageOuterClass.Message getImageMessage(MsgDbEntity msgDbEntity) {
        if (msgDbEntity.isGroup()) {
            return MessageOuterClass.Message.newBuilder()
                    .setFrom(getCurentUser())
                    .setGroup(GroupOuterClass.Group.newBuilder().setId(msgDbEntity.getToGroup().getId()).build())
                    .setImage(ImageOuterClass.Image.newBuilder().setUrl(msgDbEntity.getImageUrl()).build())
                    .setExt(getExtJson(msgDbEntity))
                    .build();
        } else {
            return MessageOuterClass.Message.newBuilder()
                    .setFrom(getCurentUser())
                    .setUser(UserOuterClass.User.newBuilder().setId(msgDbEntity.getToUser().getId()).setType(msgDbEntity.getToUser().getType()).setReference(msgDbEntity.getToUser().getRefrence()).build())
                    .setImage(ImageOuterClass.Image.newBuilder().setUrl(msgDbEntity.getImageUrl()).build())
                    .setExt(getExtJson(msgDbEntity))
                    .build();
        }
    }

    /**
     * businesscard、个人名片
     *
     * @return
     */

    public static MessageOuterClass.Message getBusinessCardMessage(MsgDbEntity msgDbEntity) {
        if (msgDbEntity.isGroup()) {
            return MessageOuterClass.Message.newBuilder()
                    .setFrom(getCurentUser())
                    .setGroup(GroupOuterClass.Group.newBuilder().setId(msgDbEntity.getToGroup().getId()).build())
                    .setBusinessCard(BusinessCardOuterClass.BusinessCard.newBuilder().setUser(buildUser(msgDbEntity.getCardUser())).build())
                    .setExt(getExtJson(msgDbEntity))
                    .build();
        } else {
            return MessageOuterClass.Message.newBuilder()
                    .setFrom(getCurentUser())
                    .setUser(UserOuterClass.User.newBuilder().setId(msgDbEntity.getToUser().getId()).setType(msgDbEntity.getToUser().getType()).setReference(msgDbEntity.getToUser().getRefrence()).build())
                    .setBusinessCard(BusinessCardOuterClass.BusinessCard.newBuilder().setUser(buildUser(msgDbEntity.getCardUser())).build())
                    .setExt(getExtJson(msgDbEntity))
                    .build();
        }
    }

    /**
     * 撤回消息
     *
     * @param msgId
     * @return
     */
    public static MessageOuterClass.Message getWithDrawMessage(long msgId) {
        return MessageOuterClass.Message.newBuilder()
                .setFrom(getCurentUser())
                .setUser(getSystemUser(1))
                .setWithdraw(WithdrawOuterClass.Withdraw.newBuilder().setId(msgId).build())
                .build();
    }

    /**
     * @param operation // 操作类型 0-添加 1-同意 2-拒绝 3-拉黑
     * @param userId
     * @param content
     * @param reference // 来源 0=通讯录 1=通过手机号搜索 2=通过群 3=通过二维码 4=通过名片
     * @return
     */
    public static MessageOuterClass.Message getOperationFriendMsg(int operation, long userId, String content, int reference) {
        return MessageOuterClass.Message.newBuilder()
                .setFrom(getCurentUser())
                .setUser(getSystemUser(2))
                .setFriendOperation(
                        FriendOperationOuterClass.FriendOperation.newBuilder()
                                .setOperation(operation)
                                .setOpposite(userId)
                                .setContent(content)
                                .setReference(reference)
                                .build()
                )
                .build();
    }

    /**
     * 群操作
     *
     * @param type    // 操作 0=创建群 1=邀请 2=退群(踢人)
     * @param groupId
     * @param users
     * @return
     */
    public static MessageOuterClass.Message getOperationGroupMsg(int type, long groupId, String content, List<UserOuterClass.User> users) {
        return MessageOuterClass.Message.newBuilder()
                .setFrom(getCurentUser())
                .setUser(getSystemUser(1))
                .setGroupOperation(
                        GroupOperationOuterClass.GroupOperation.newBuilder()
                                .setOperation(type)
                                .setGroup(groupId)
                                .setContent(content)
                                .addAllMembers(users)
                                .build()
                )
                .build();
    }

    /**
     * 数据库user构建pro
     *
     * @param userDbEntity
     * @return
     */
    public static UserOuterClass.User buildUser(ImUserDbEntity userDbEntity) {
        return UserOuterClass.User.newBuilder()
                .setId(userDbEntity.getId())
                .setType(userDbEntity.getType())
                .setName(userDbEntity.getName())
                .setAvatar(userDbEntity.getAvatar())
                .setReference(userDbEntity.getRefrence())
                .setHospital(userDbEntity.getHospital())
                .build();
    }

    /**
     * 构建系统user
     *
     * @param id 为系统用户时: 0=系统消息 1=业务处理 2=新的朋友
     * @return
     */
    private static UserOuterClass.User getSystemUser(int id) {
        return UserOuterClass.User.newBuilder()
                .setReference(9)//系统type
                .setId(id)
                .build();
    }

    /**
     * 构建当前user的信息
     *
     * @return
     */
    private static UserOuterClass.User getCurentUser() {
        UserInfoEntity userInfo = ModuleIMManager.INSTANCE.getMsgService().getCurrentUser();
        return UserOuterClass.User.newBuilder()
                .setId(userInfo.getId())
                .setReference(0)//医联用户的type
                .setName(userInfo.getName())
                .setAvatar(userInfo.getAvatar())
                .setType(userInfo.getType())
                .build();
    }

    public static MessageOuterClass.Message getCardMessage(MsgDbEntity msgDbEntity) {
        final CardOuterClass.Card.Builder cardBuilder = CardOuterClass.Card.newBuilder();
        cardBuilder.setType(msgDbEntity.getCardType())
                .setLabel(msgDbEntity.getCardLabel())
                .setSummary(msgDbEntity.getCardSummary())
                .setTitle(msgDbEntity.getCardTitle())
                .setImage(msgDbEntity.getCardImageUrl())
                .setUrl(msgDbEntity.getCardTargetUrl());
        if (msgDbEntity.getCardExtra() != null) {
            cardBuilder.setExtra(msgDbEntity.getCardExtra());
        }

        final MessageOuterClass.Message.Builder builder = MessageOuterClass.Message.newBuilder()
                .setFrom(getCurentUser())
                .setCard(cardBuilder.build());
        if (msgDbEntity.isGroup()) {
            builder.setGroup(GroupOuterClass.Group.newBuilder().setId(msgDbEntity.getToGroup().getId()).build());
        } else {
            builder.setUser(UserOuterClass.User.newBuilder().setId(msgDbEntity.getToUser().getId()).setType(msgDbEntity.getToUser().getType()).setReference(msgDbEntity.getToUser().getRefrence()).build());
        }
        builder.setExt(getExtJson(msgDbEntity));
        return builder.build();
    }

    public static MessageOuterClass.Message getNoticeMessage(MsgDbEntity msgDbEntity) {
        final MessageOuterClass.Message.Builder builder = MessageOuterClass.Message.newBuilder().setFrom(getCurentUser())
                .setNotice(NoticeOuterClass.Notice.newBuilder()
                        .setContent(msgDbEntity.getNoticeContent())
                        .build());
        if (msgDbEntity.isGroup()) {
            builder.setGroup(GroupOuterClass.Group.newBuilder().setId(msgDbEntity.getToGroup().getId()).build());
        } else {
            final ImUserDbEntity toUser = msgDbEntity.getToUser();
            builder.setUser(UserOuterClass.User.newBuilder().setId(toUser.getId()).setType(toUser.getType()).setReference(toUser.getRefrence()).build());
        }
        builder.setExt(getExtJson(msgDbEntity));
        return builder.build();
    }

    private static String getExtJson(MsgDbEntity msgDbEntity) {
        long hospitalId = msgDbEntity.getHospitalId();
        if (hospitalId <= 0) {
            return "";
        }
//        MsgExtEntity extEntity = new MsgExtEntity();
//        extEntity.setHospitalId(hospitalId);
//        return new Gson().toJson(extEntity);
        return null;
    }
}
