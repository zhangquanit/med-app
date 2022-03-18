package net.medlinker.im.helper;

import android.content.Context;
import android.os.Vibrator;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.medlinker.protocol.message.MessageOuterClass;
import com.medlinker.protocol.message.commands.FriendOperationOuterClass;
import com.medlinker.protocol.message.misc.GroupOuterClass;
import com.medlinker.protocol.message.misc.UserOuterClass;

import net.medlinker.im.MsgConstants;
import net.medlinker.im.R;
import net.medlinker.im.realm.ImGroupDbEntity;
import net.medlinker.im.realm.ImUserDbEntity;
import net.medlinker.im.realm.MsgDbEntity;
import net.medlinker.im.realm.MsgExtEntity;
import net.medlinker.im.realm.MsgSessionDbEntity;
import net.medlinker.im.router.ModuleIMManager;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description 实体类转换类，protocol实体转化为realm实体保存
 * @time 2017/2/27
 */
public class EntityConvertHelper {

    //新的朋友ID
    public static final String SESSION_ID_NEW_FRIEND = "newfriend";
    /**
     * 新绑定患者通知
     */
    public static final String SESSION_ID_NEW_PATIENT_BIND = "new_patient_bind";
    //问诊ID
    public static final String SESSION_ID_INQUIRY = "inquiryId";
    //热门群组的sessionID
    public static final String SESSION_ID_HOT_GROUPS = "hot_groups";
    //门诊ID
    public static final String SESSION_ID_CLINIC = "clinicId";

    public static final int SESSION_USER_ID_NEW_FRIEND = 2; // 特殊用户id: 新的朋友
    public static final int SESSION_USER_ID_SYSTEM = 0; // 系统用户id
    public static final int SESSION_USER_ID_DYNAMIC = 4; // 动态相关通知
    public static final int SESSION_USER_ID_ORDER = 5; // 订单相关
    public static final int SESSION_USER_ID_NOTICE = 6; //
    public static final int SESSION_USER_ID_ANGEL = 7;
    public static final int SESSION_USER_ID_WALLET = 8; // 钱包
    public static final int SESSION_USER_ID_PATIENT = 9; // 患者绑定
    public static final int SESSION_USER_ID_INQUIRY = -22;
    public static final int SESSION_USER_ID_NOTIFY = -33;
    public static final int SESSION_USER_ID_HOT_GROUPS = -44;
    public static final int SESSION_USER_ID_CLINIC = -55;

    /**
     * 通过id获取时间戳
     *
     * @param id
     * @return
     */
    private static long getTimeStamp(long id) {
        return (id >> 22) + 1288834974657L;
    }

    /**
     *
     */
    public static long timeGetId(long time) {
        return (time - 1288834974657L) << 22;
    }

    /**
     *
     */
    public static String getGroupSessionId(long groupId) {
        return groupId + "-" + "group";
    }

    public static long getGroupId(String sessionId) {
        if (null == sessionId) {
            return 0;
        }
        try {
            if (sessionId.contains("-")) {
                String[] str = sessionId.split("-");
                return Long.parseLong(str[0]);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     *
     */
    public static String getRealGroupSessionId(String groupId) {
        if (TextUtils.isEmpty(groupId)) {
            return "";
        }
        int index = groupId.indexOf("-");
        return index < 0 ? groupId : groupId.substring(0, index);
    }

    /**
     *
     */
    public static String getUserSessionId(long userId, int reference) {
        return userId + "-" + reference;
    }

    /**
     *
     */
    public static String getSessionIdByHospital(String sessionId, long hospitalId) {
//        if (hospitalId <= 0) {
//            return sessionId;
//        }
//        return sessionId + "-" + hospitalId;
        return sessionId;
    }

    /**
     * 根据医院id，生成的sessionId，
     */
    private static String getSessionMsgIdByHospital(MessageOuterClass.Message msg) {
//        long hospitalId = getHospitalId(msg);
//        if (hospitalId > 0) {
//            return getSessionMsgId(msg) + "-" + hospitalId;
//        }
        return getSessionMsgId(msg);
    }

    /**
     * 获取MsgSessionDbEntity，首先判断是否跟医院有关系
     *
     * @param sessionId
     * @param realm
     * @return
     */
    public static MsgSessionDbEntity getSessionById(String sessionId, Realm realm) {
        if (realm == null || realm.isClosed()) {
            return null;
        }
        MsgSessionDbEntity session;
        session = realm.where(MsgSessionDbEntity.class).equalTo("id", sessionId).findFirst();
        if (session == null) {
            session = realm.where(MsgSessionDbEntity.class).equalTo("sessionId", sessionId).findFirst();
        }
        return session;
    }

    /**
     * 生成sessionId
     *
     * @param msg
     * @return
     */
    private static String getSessionMsgId(MessageOuterClass.Message msg) {
        switch (msg.getToCase()) {
            case USER:
                if (msg.getDataCase() == MessageOuterClass.Message.DataCase.FRIENDOPERATION || msg.getDataCase() == MessageOuterClass.Message.DataCase.GROUPOPERATION) {
                    //如果是新的朋友消息类型，就sessionId就是session；
                    return SESSION_ID_NEW_FRIEND;
                } else {
                    if (msg.getFrom().getId() == ModuleIMManager.INSTANCE.getIMService().getCurrentUserId()) {
                        //如果是自己的消息
                        return msg.getUser().getId() + "-" + msg.getUser().getReference();
                    } else {
                        return msg.getFrom().getId() + "-" + msg.getFrom().getReference();
                    }
                }
            case GROUP:
                return getGroupSessionId(msg.getGroup().getId());
            default:
                return "";
        }
    }


    /**
     * 创建一个新的消息入库
     *
     * @param entity
     * @param isHistory
     * @param realm
     * @return
     */
    public static MsgDbEntity msg2ImDbEntity(final MessageOuterClass.Message entity, boolean isHistory, Realm realm) {
        MsgDbEntity msgDbEntity = realm.where(MsgDbEntity.class).equalTo("id", entity.getId()).findFirst();
        //删掉重复的消息（自己发的消息，同一个id消息，本地的时间戳和server返回的不一样）
        if (msgDbEntity != null) {
            msgDbEntity.deleteFromRealm();
        }
        msgDbEntity = new MsgDbEntity();
        msgDbEntity.setId(entity.getId());
        msgDbEntity.setSessionId(getSessionMsgId(entity));
        msgDbEntity.setTimestamp(getTimeStamp(entity.getId()));
        msgDbEntity.setDataType(entity.getDataCase().getNumber());
        msgDbEntity.setHospitalId(getHospitalId(entity));
        msgDbEntity.setExt(getExt(entity));
        //拿到session的状态
        MsgSessionDbEntity session = realm.where(MsgSessionDbEntity.class).equalTo("id", getSessionMsgIdByHospital(entity)).findFirst();
        if (session != null) {
            //如果消息免打扰，那么就不标记未读
            final String id = ConversationHelper.getInstance().getCurrentSessionId();
            msgDbEntity.setHasRead(id.equals(session.getSessionId()) || session.isRejectMsg() || (session.isGroup() && session.getFromGroup().isRejectMsg()) || isHistory);
        } else {
            //如果是历史那么就已读
            msgDbEntity.setHasRead(isHistory);
        }
        msgDbEntity.setFromUser(protolUser2ImUser(entity.getFrom(), realm));
        msgDbEntity.setGroup(entity.getToCase() == MessageOuterClass.Message.ToCase.GROUP);
        msgDbEntity.setSelfMsg(entity.getFrom().getId() == ModuleIMManager.INSTANCE.getIMService().getCurrentUserId());
        //如果是群聊消息，就设置发送给群，否则给用户
        if (msgDbEntity.isGroup()) {
            msgDbEntity.setToGroup(protolGroup2ImGroup(entity.getGroup(), realm));
        } else {
            msgDbEntity.setToUser(protolUser2ImUser(entity.getUser(), realm));
        }
        switch (entity.getDataCase()) {
            case TEXT:
                msgDbEntity.setTextContent(entity.getText().getContent());
                msgDbEntity.setLinks(entity.getText().getAnchorsList());
                if (msgDbEntity.isGroup()) {
                    for (UserOuterClass.User user : entity.getText().getAtUsersList()) {
                        msgDbEntity.addAtUsers(EntityConvertHelper.protolUser2ImUser(user, realm));
                    }
                }
                break;
            case JSON:
                msgDbEntity.setJsonType(entity.getJson().getType());
                msgDbEntity.setJsonString(entity.getJson().getContent());
                break;
            case VOICE:
                msgDbEntity.setVoiceDuration(entity.getVoice().getDuration());
                msgDbEntity.setVoiceUrl(entity.getVoice().getUrl());
                break;
            case CARD:
                msgDbEntity.setCardType(entity.getCard().getType());
                msgDbEntity.setCardLabel(entity.getCard().getLabel());
                msgDbEntity.setCardTitle(entity.getCard().getTitle());
                msgDbEntity.setCardSubTitle(entity.getCard().getSubtitle());
                msgDbEntity.setCardSummary(entity.getCard().getSummary());
                msgDbEntity.setCardImageUrl(entity.getCard().getImage());
                msgDbEntity.setCardTargetUrl(entity.getCard().getUrl());
                msgDbEntity.setCardDisplay(entity.getCard().getDisplay());
                msgDbEntity.setCardExtra(entity.getCard().getExtra());
                break;
            case IMAGE:
                msgDbEntity.setImagePreview(entity.getImage().getPreview());
                msgDbEntity.setImageUrl(entity.getImage().getUrl());
                msgDbEntity.setImageSize(entity.getImage().getSize());
                break;
            case LOCATION:
                msgDbEntity.setLongitude(entity.getLocation().getLongitude());
                msgDbEntity.setLatitude(entity.getLocation().getLatitude());
                break;
            case NOTICE:
                msgDbEntity.setNoticeContent(entity.getNotice().getContent());
                break;
            case BUSINESSCARD:
                msgDbEntity.setCardUser(protolUser2ImUser(entity.getBusinessCard().getUser(), realm));
                break;
            case DATA_NOT_SET:
                msgDbEntity.setCompactText(ModuleIMManager.INSTANCE.getIMService().getApplication()
                        .getString(R.string.compact_text));
                break;
            default:
        }
        return msgDbEntity;
    }

    private static MsgExtEntity getExt(MessageOuterClass.Message entity) {
        String ext = entity.getExt();
        if (TextUtils.isEmpty(ext)) {
            return null;
        }
        try {
            return new Gson().fromJson(ext, MsgExtEntity.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static long getHospitalId(MessageOuterClass.Message entity) {
//        String ext = entity.getExt();
//        if (TextUtils.isEmpty(ext)) {
//            return 0;
//        }
//        try {
//            MessageExtEntity extEntity = new Gson().fromJson(ext, MessageExtEntity.class);
//            return extEntity.getHospitalId();
//        } catch (JsonSyntaxException e) {
//            e.printStackTrace();
//            return 0;
//        }
        return 0;
    }

    /**
     * 通过历史会话的消息构建会话消息，用于版本升级或者换设备登录
     *
     * @param message
     * @param realm
     */
    public static MsgSessionDbEntity upDataHistorySession(MessageOuterClass.Message message, Realm realm) {
        MsgSessionDbEntity msgSessionDbEntity;
        msgSessionDbEntity = realm.where(MsgSessionDbEntity.class).equalTo("id", getSessionMsgIdByHospital(message)).findFirst();
        if (msgSessionDbEntity == null) {
            msgSessionDbEntity = new MsgSessionDbEntity();
            msgSessionDbEntity.setId(getSessionMsgIdByHospital(message));
            msgSessionDbEntity.setGroup(message.getToCase() == MessageOuterClass.Message.ToCase.GROUP);
            msgSessionDbEntity.setTimeStamp(getTimeStamp(message.getId()));
            msgSessionDbEntity.setContent(buildContent(message));
            switch (msgSessionDbEntity.getSessionId()) {
                case SESSION_ID_NEW_FRIEND:
                    msgSessionDbEntity.setFromUser(getNewFriendUser(realm));
                    break;
                default://普通群聊或者单聊
                    if (msgSessionDbEntity.isGroup()) {
                        msgSessionDbEntity.setFromGroup(protolGroup2ImGroup(message.getGroup(), realm));
                        msgSessionDbEntity.setSessionType(msgSessionDbEntity.getFromGroup().getBusinessType());
                    } else {
                        msgSessionDbEntity.setFromUser(protolUser2ImUser(
                                message.getFrom().getId() == ModuleIMManager.INSTANCE.getIMService().getCurrentUserId()
                                        ? message.getUser()
                                        : message.getFrom(), realm));
                        checkIsNotNormalSession(msgSessionDbEntity);
                    }
                    break;
            }
        }
        msgSessionDbEntity.setHospitalId(getHospitalId(message));
        msgSessionDbEntity.setLastMsgFromUser(protolUser2ImUser(message.getFrom(), realm));
        msgSessionDbEntity.setLastMsgType(message.getDataCase().getNumber());
        if (msgSessionDbEntity.isEmptySessionId()) {
            msgSessionDbEntity.setSessionId(getSessionMsgId(message));
        }
        //特殊标识，这里是来源于服务器生成的session
        msgSessionDbEntity.setHistorySession(true);
        return msgSessionDbEntity;
    }

    /**
     * 是否是群聊
     *
     * @param message
     * @return
     */
    public static boolean isGroup(MessageOuterClass.Message message) {
        return message.getToCase() == MessageOuterClass.Message.ToCase.GROUP;
    }

    /**
     * 更新会话表
     *
     * @param message
     * @param isOffline
     * @return
     */
    public static MsgSessionDbEntity msg2SessionEntity(MessageOuterClass.Message message, boolean isHistory, boolean isOffline, Realm realm) {
        MsgSessionDbEntity msgSessionDbEntity = realm.where(MsgSessionDbEntity.class).equalTo("id", getSessionMsgIdByHospital(message)).findFirst();
        if (msgSessionDbEntity == null) {
            msgSessionDbEntity = new MsgSessionDbEntity();
            msgSessionDbEntity.setId(getSessionMsgIdByHospital(message));
            msgSessionDbEntity.setGroup(isGroup(message));
        }
        msgSessionDbEntity.setHospitalId(getHospitalId(message));
        if (msgSessionDbEntity.isEmptySessionId()) {
            msgSessionDbEntity.setSessionId(getSessionMsgId(message));
        }
        msgSessionDbEntity.setLastMsgFromUser(protolUser2ImUser(message.getFrom(), realm));
        msgSessionDbEntity.setLastMsgType(message.getDataCase().getNumber());
        msgSessionDbEntity.setTimeStamp(getTimeStamp(message.getId()));
        msgSessionDbEntity.setContent(buildContent(message));

        //清除已处理标志位
        msgSessionDbEntity.setProcessed(false);
        // 新患者绑定通知，不建立会话列表
        String ext = message.getExt();
        // 清除标志
        msgSessionDbEntity.setHasRejectMsg(false);
        int dialogueRole = 0;
        if (!TextUtils.isEmpty(ext)) {
            try {
                JSONObject object = new JSONObject(ext);
                dialogueRole = object.has("dialogueRole") ? object.optInt("dialogueRole") : 0;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        msgSessionDbEntity.setDialogueRole(dialogueRole);
        switch (msgSessionDbEntity.getSessionId()) {
            case SESSION_ID_NEW_FRIEND:
                msgSessionDbEntity.setFromUser(getNewFriendUser(realm));
                break;
            default://普通群聊或者单聊
                if (msgSessionDbEntity.isGroup()) {
                    msgSessionDbEntity.setFromGroup(protolGroup2ImGroup(message.getGroup(), realm));
                    msgSessionDbEntity.setSessionType(msgSessionDbEntity.getFromGroup().getBusinessType());
                } else {
                    msgSessionDbEntity.setFromUser(protolUser2ImUser(message.getFrom(), realm));
                    checkIsNotNormalSession(msgSessionDbEntity);
                }
                break;
        }

//        if (msgSessionDbEntity.isSuiZhenGroupSession()) {
//            boolean state = PreferencesUtil.getBoolean(BaseApplication.getApplication(), PreferencesUtil.KEY_DISTURB_STATUS_OPEN, false);
//            msgSessionDbEntity.setRejectMsg(state);
//        }
        //如果开启了免打扰，清空未读数，并且标记一下他有拦截的消息，来显示小红点
        if (msgSessionDbEntity.isRejectMsg() || (msgSessionDbEntity.isGroup() && msgSessionDbEntity.getFromGroup().isRejectMsg())) {
            msgSessionDbEntity.setUnreadMsgCount(0);
            msgSessionDbEntity.setHasRejectMsg(true);
        } else {
            //如果处于当前会话之中，未读消息数不累加。
            final String id = ConversationHelper.getInstance().getCurrentSessionId();
            if (!isHistory && !id.equals(msgSessionDbEntity.getSessionId())) {
                if (msgSessionDbEntity.isDoctorsRole()) {
                    msgSessionDbEntity.setUnreadMsgCount(msgSessionDbEntity.getUnreadMsgCount() + 1);
                } else if (msgSessionDbEntity.getUnreadMsgRealCount() < 1) {
                    msgSessionDbEntity.setHasRejectMsg(true);
                }
            }
            if (msgSessionDbEntity.isDoctorRole()) {
                ModuleIMManager.INSTANCE.getMsgService().notifyMsg(msgSessionDbEntity);
            }
        }
        if (!isOffline && !isHistory) {
            checkVibrate(getSessionMsgId(message), realm);
        }
        checkAtMe(msgSessionDbEntity, message);
        return msgSessionDbEntity;
    }

    /**
     * 是否要at自己的消息
     *
     * @param msgSessionDbEntity
     * @param message
     */
    private static void checkAtMe(MsgSessionDbEntity msgSessionDbEntity, MessageOuterClass.Message message) {
        if (message.getDataCase() == MessageOuterClass.Message.DataCase.TEXT && msgSessionDbEntity.isGroup()) {
            for (UserOuterClass.User user : message.getText().getAtUsersList()) {
                if (user.getId() == ModuleIMManager.INSTANCE.getIMService().getCurrentUserId()
                        || user.getId() == MsgConstants.GROUP_TIPS_USER_ID) {
                    //如果有@消息，就设置为有@的消息标识为true
                    msgSessionDbEntity.setHaveAt(true);
                    break;
                }
            }
        }
    }

    /**
     * 收到消息振动
     *
     * @param sessionMsgId
     */
    private static void checkVibrate(String sessionMsgId, Realm realm) {
        final String id = ConversationHelper.getInstance().getCurrentSessionId();
        if (sessionMsgId.equals(id)) {
            return;
        }
        MsgSessionDbEntity sessionDbEntity = MsgDbManager.INSTANCE.getSessionDbDao().getSessionById(sessionMsgId, realm);
        if (sessionDbEntity == null) {
            return;
        }
        //如果消息被免打扰了就屏蔽
        if (!sessionDbEntity.isGroup() && sessionDbEntity.isRejectMsg()) {
            return;
        }
        if (sessionDbEntity.isGroup() && sessionDbEntity.getFromGroup().isRejectMsg()) {
            return;
        }
        Vibrator vibrator = (Vibrator) ModuleIMManager.INSTANCE.getIMService().getApplication().getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = new long[]{0, 180, 80, 120};
        vibrator.vibrate(pattern, -1);
//        vibrator.vibrate(200);
    }

    /**
     * 检查是否是普通会话类型，设置会话的类型，目前会话类型，0是普通会话 1 是问诊患者 2 普通患者 3 经纪人聊天 4通知类型
     *
     * @param msgSessionDbEntity
     */
    private static void checkIsNotNormalSession(MsgSessionDbEntity msgSessionDbEntity) {
    }


    /**
     * 检查是否是通知类型消息，如果是的话，就要新建通知类型消息会话
     *
     * @param message
     */
    public static void upDataFatherSession(final MessageOuterClass.Message message, Realm realm) {
        final MsgSessionDbEntity msgSessionDbEntity = MsgDbManager.INSTANCE.getSessionDbDao().getSessionById(getSessionMsgIdByHospital(message), realm);
        //refrence==9就是系统级别用户，包括动态，系统消息，订单相关等。
        if (!msgSessionDbEntity.isGroup() && msgSessionDbEntity.getFromUser().getRefrence() == MsgConstants.CHAT_USER_REFERENCE_SYSTEM) {
            //系统消息和动态相关不在主流里面
            if (msgSessionDbEntity.getFromUser().getId() == SESSION_USER_ID_SYSTEM || msgSessionDbEntity.getFromUser().getId() == SESSION_USER_ID_DYNAMIC) {
                msgSessionDbEntity.setChildSession(true);
            }
        }
    }

    /**
     * 构建会话的提示内容
     *
     * @param message
     * @return
     */
    private static String buildContent(MessageOuterClass.Message message) {
        String head = message.getToCase() == MessageOuterClass.Message.ToCase.GROUP ? message.getFrom().getName() + ":" : "";
        switch (message.getDataCase()) {
            case TEXT:
                if (message.getToCase() == MessageOuterClass.Message.ToCase.GROUP) {
                    return head + message.getText().getContent();
                }
                return message.getText().getContent();
            case VOICE:
                return head + "[语音]";
            case IMAGE:
                return head + "[图片]";
            case JSON:
                String jsonContent = ModuleIMManager.INSTANCE.getMsgService().buildJsonMsgContent(message);
                if (!TextUtils.isEmpty(jsonContent)) {
                    return head + jsonContent;
                }
                return "";
            case CARD:
                return head + "[" + message.getCard().getTitle() + "]";
            case LOCATION:
                return head + "[位置]";
            case NOTICE:
                return message.getNotice().getContent();
            case BUSINESSCARD:
                return head + "[名片]";
            case WITHDRAW:
                return getWithDrawContent(message);
            case FRIENDOPERATION:
                return "您有新的好友添加申请";
            case GROUPOPERATION:
                return "您收到新的入群申请";
            case DATA_NOT_SET:
            default:
                return ModuleIMManager.INSTANCE.getIMService().getApplication().getString(R.string.compact_text);
        }
    }

    /**
     *
     */
    public static String getWithDrawContent(MessageOuterClass.Message message) {
        String content = "";
        if (message.getFrom().getId() == ModuleIMManager.INSTANCE.getIMService().getCurrentUserId()) {
            content = "你撤回了一条消息";
        } else {
            if (message.getToCase() == MessageOuterClass.Message.ToCase.GROUP) {
                content = message.getFrom().getName() + "撤回了一条消息";
            } else {
                content = "对方撤回了一条消息";
            }
        }
        return content;
    }


    /**
     * 得到新的朋友
     *
     * @param from
     * @param friendOperation
     * @return
     */
    public static ImUserDbEntity conver2NewFriend(UserOuterClass.User from, FriendOperationOuterClass.FriendOperation friendOperation, Realm realm) {
        ImUserDbEntity imUserDbEntity = protolUser2ImUser(from, realm);
        imUserDbEntity.setNewFriend(friendOperation.getOperation() == 0);
        imUserDbEntity.setAddFriendOperation(friendOperation.getOperation());
        imUserDbEntity.setAddFriendContent(friendOperation.getContent());
        imUserDbEntity.setAddFriendReference(friendOperation.getReference());
        return imUserDbEntity;
    }

    /**
     * 构建一个新的朋友
     *
     * @return
     */
    private static ImUserDbEntity getNewFriendUser(Realm realm) {
        ImUserDbEntity newFriend = realm.where(ImUserDbEntity.class).equalTo("id", SESSION_USER_ID_NEW_FRIEND).findFirst();
        if (newFriend == null) {
            newFriend = new ImUserDbEntity();
            newFriend.setId(SESSION_USER_ID_NEW_FRIEND);
            newFriend.setRefrence(9);
            newFriend.setName("新的朋友");
        }
        return newFriend;
    }

    /**
     *
     */
    public static ImUserDbEntity getInquiryUser(Realm realm) { //-22 是问诊患者自己定义的一个用户ID
        ImUserDbEntity newFriend = realm.where(ImUserDbEntity.class).equalTo("id", SESSION_USER_ID_INQUIRY).findFirst();
        if (newFriend == null) {
            newFriend = new ImUserDbEntity();
            newFriend.setId(SESSION_USER_ID_INQUIRY);
            newFriend.setRefrence(9);
            newFriend.setName("问诊患者");
        }
        return newFriend;
    }

    /**
     *
     */
    public static ImUserDbEntity getClinicUser(Realm realm) { //-55 处方门诊自己定义一个用户ID
        ImUserDbEntity newFriend = realm.where(ImUserDbEntity.class).equalTo("id", SESSION_USER_ID_CLINIC).findFirst();
        if (newFriend == null) {
            newFriend = new ImUserDbEntity();
            newFriend.setId(SESSION_USER_ID_CLINIC);
            newFriend.setRefrence(9);
            newFriend.setName("处方门诊患者");
        }
        return newFriend;
    }

    /**
     *
     */
    public static ImUserDbEntity getHotGroupsUser(Realm realm) {
        ImUserDbEntity newFriend = realm.where(ImUserDbEntity.class).equalTo("id", SESSION_USER_ID_HOT_GROUPS).findFirst();
        if (newFriend == null) {
            newFriend = new ImUserDbEntity();
            newFriend.setId(SESSION_USER_ID_HOT_GROUPS);
            newFriend.setRefrence(9);
            newFriend.setName("热门群组");
        }
        return newFriend;
    }

    /**
     * 转换group对象
     *
     * @return
     */
    public static ImGroupDbEntity protolGroup2ImGroup(GroupOuterClass.Group to, Realm realm) {
        ImGroupDbEntity imGroupDbEntity = realm.where(ImGroupDbEntity.class).equalTo("id", to.getId()).findFirst();
        if (imGroupDbEntity == null) {
            imGroupDbEntity = new ImGroupDbEntity();
            imGroupDbEntity.setId(to.getId());
        }
        imGroupDbEntity.setSingleAvatar(to.getAvatarUrl());
        imGroupDbEntity.setOpenGroup(to.getIsPublic() == 1);
        imGroupDbEntity.setName(to.getName());
        imGroupDbEntity.setAvatarList(to.getAvatarList());
        imGroupDbEntity.setAmount(to.getAmount());
        imGroupDbEntity.setOwner(to.getOwner());
        imGroupDbEntity.setBusinessType(to.getBusinessType());
        imGroupDbEntity.setBusinessId(to.getBusinessId());
        return imGroupDbEntity;
    }

    public static int getBusinessType(GroupOuterClass.Group to) {
        return to.getBusinessType();
    }

    /**
     * 转换group对象
     *
     * @param to
     * @return
     */
    public static ImGroupDbEntity protolGroup2ImGroup(GroupOuterClass.Group to) {
        ImGroupDbEntity imGroupDbEntity = new ImGroupDbEntity();
        imGroupDbEntity.setId(to.getId());
        imGroupDbEntity.setSingleAvatar(to.getAvatarUrl());
        imGroupDbEntity.setOpenGroup(to.getIsPublic() == 1);
        imGroupDbEntity.setName(to.getName());
        imGroupDbEntity.setAvatarList(to.getAvatarList());
        imGroupDbEntity.setAmount(to.getAmount());
        imGroupDbEntity.setOwner(to.getOwner());
        imGroupDbEntity.setBusinessType(to.getBusinessType());
        imGroupDbEntity.setBusinessId(to.getBusinessId());
        return imGroupDbEntity;
    }

    /**
     * 转换user对象
     */
    public static ImUserDbEntity protolUser2ImUser(UserOuterClass.User user, Realm realm) {
        ImUserDbEntity imUserDbEntity = realm.where(ImUserDbEntity.class).equalTo("id", user.getId()).findFirst();
        if (imUserDbEntity == null) {
            imUserDbEntity = new ImUserDbEntity();
            imUserDbEntity.setId(user.getId());
        }
        if (user.getReference() != 0) {
            imUserDbEntity.setRefrence(user.getReference());
        }
        if (user.getType() != 0) {
            imUserDbEntity.setType(user.getType());
        }
        if (!TextUtils.isEmpty(user.getName())) {
            imUserDbEntity.setName(user.getName());
        }
        if (!TextUtils.isEmpty(user.getHospital())) {
            imUserDbEntity.setHospital(user.getHospital());
        }
        if (!TextUtils.isEmpty(user.getSection())) {
            imUserDbEntity.setSection(user.getSection());
        }
        if (!TextUtils.isEmpty(user.getTitle())) {
            imUserDbEntity.setTitle(user.getTitle());
        }
        if (!TextUtils.isEmpty(user.getAvatar())) {
            imUserDbEntity.setAvatar(user.getAvatar());
        }
        return imUserDbEntity;
    }

    /**
     * 转换user对象
     */
    public static ImUserDbEntity protolUser2ImUser(UserOuterClass.User user) {
        ImUserDbEntity imUserDbEntity = new ImUserDbEntity();
        imUserDbEntity.setId(user.getId());
        if (user.getReference() != 0) {
            imUserDbEntity.setRefrence(user.getReference());
        }
        if (user.getType() != 0) {
            imUserDbEntity.setType(user.getType());
        }
        if (!TextUtils.isEmpty(user.getName())) {
            imUserDbEntity.setName(user.getName());
        }
        if (!TextUtils.isEmpty(user.getHospital())) {
            imUserDbEntity.setHospital(user.getHospital());
        }
        if (!TextUtils.isEmpty(user.getSection())) {
            imUserDbEntity.setSection(user.getSection());
        }
        if (!TextUtils.isEmpty(user.getTitle())) {
            imUserDbEntity.setTitle(user.getTitle());
        }
        if (!TextUtils.isEmpty(user.getAvatar())) {
            imUserDbEntity.setAvatar(user.getAvatar());
        }
        return imUserDbEntity;
    }

//    /**
//     *
//     */
//    public static ImUserDbEntity medUser2ImUser(UserDetailEntity user, Realm realm) {
//        ImUserDbEntity imUserDbEntity = realm.where(ImUserDbEntity.class).equalTo("id", user.getId()).findFirst();
//        if (imUserDbEntity == null) {
//            imUserDbEntity = new ImUserDbEntity();
//            imUserDbEntity.setId(user.getId());
//        }
//        imUserDbEntity.setRefrence(0);
//        imUserDbEntity.setName(user.getName());
//        imUserDbEntity.setAvatar(user.getAvatar());
//        imUserDbEntity.setType(user.getType());
//        imUserDbEntity.setHospital(user.getHospital());
//        imUserDbEntity.setSection(user.getSectionName());
//        imUserDbEntity.setTitle(user.getTitleName());
//        return imUserDbEntity;
//    }

//    /**
//     *
//     */
//    public static UserLetterEntity imUser2MedUser(ImUserDbEntity userDbEntity) {
//        UserLetterEntity userDetailEntity = new UserLetterEntity();
//        userDetailEntity.setId(userDbEntity.getId());
//        userDetailEntity.setName(userDbEntity.getName());
//        userDetailEntity.setAvatar(userDbEntity.getAvatar());
//        userDetailEntity.setType(userDbEntity.getType());
//        userDetailEntity.setHospital(userDbEntity.getHospital());
//        userDetailEntity.setSectionName(userDbEntity.getSection());
//        userDetailEntity.setTitleName(userDbEntity.getTitle());
//        return userDetailEntity;
//    }

//    /**
//     *
//     */
//    public static List<UserOuterClass.User> converUsers(List<UserDetailEntity> userList) {
//        List<UserOuterClass.User> users = new ArrayList<>();
//        for (UserDetailEntity userLetterEntity : userList) {
//            users.add(UserOuterClass.User.newBuilder()
//                    .setId(userLetterEntity.getId())
//                    .setReference(0)
//                    .setName(userLetterEntity.getName())
//                    .setAvatar(userLetterEntity.getAvatar())
//                    .setTitle(userLetterEntity.getTitleName())
//                    .setSection(userLetterEntity.getSectionName())
//                    .setType(userLetterEntity.getType())
//                    .setHospital(userLetterEntity.getHospital())
//                    .build());
//        }
//        return users;
//    }

//    /**
//     *
//     */
//    public static List<UserDetailBaseEntity> imUsers2MedUsers(List<ImUserDbEntity> userList) {
//        List<UserDetailBaseEntity> users = new ArrayList<>();
//        for (ImUserDbEntity userDbEntity : userList) {
//            users.add(imUser2MedUser(userDbEntity));
//        }
//        return users;
//    }

//    /**
//     *
//     */
//    public static List<UserLetterEntity> imUsers2MedLetterUsers(List<ImUserDbEntity> userList) {
//        List<UserLetterEntity> users = new ArrayList<>();
//        for (ImUserDbEntity userDbEntity : userList) {
//            users.add(imUser2MedUser(userDbEntity));
//        }
//        return users;
//    }

//    /**
//     * 这个方法手机联系人用，FJ写的。。有两个特殊参数。
//     *
//     * @param userList
//     * @return
//     */
//    public static List<ImUserDbEntity> mobileUsers2ImUsers(List<UserLetterEntity> userList, Realm realm) {
//        List<ImUserDbEntity> users = new ArrayList<>();
//        for (UserLetterEntity userLetterEntity : userList) {
//            ImUserDbEntity imUserDbEntity = medUser2ImUser(userLetterEntity, realm);
//            imUserDbEntity.setMobileContact(true);
//            users.add(imUserDbEntity);
//        }
//        return users;
//    }

//    /**
//     * 私信分享卡片转换
//     *
//     * @param msgDbEntity
//     * @param session
//     * @param contentParams
//     */
//    public static void conver2Card(MsgDbEntity msgDbEntity, MsgSessionDbEntity session, ShareContentParams contentParams) {
//        String lable = "";
//        String transeUrl = "";
//        String summary = "";
//        switch (contentParams.getType()) {
//            case ShareType.TYPE_CASE:
//                session.setContent("病例 | " + contentParams.getTitle());
//                lable = "病例";
//                summary = contentParams.getContent();
//                JSONObject extra = new JSONObject();
//                try {
//                    extra.put("casemId", contentParams.getTargetId());
//                    transeUrl = RnRouter.createJumpUrl("CaseRecord", "CRCaseExamines", extra.toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                break;
//            case ShareType.TYPE_CARD:
//                summary = contentParams.getSubtitle();
//                session.setContent("名片 | " + contentParams.getSubtitle());
//                lable = "用户名片";
//                transeUrl = UserHomeRouter.USER_HOME + "?userId=" + contentParams.getTargetId() + "&type=" + contentParams.getUserType();
//                break;
//            case ShareType.TYPE_NEWS:
//                session.setContent("资讯 | " + contentParams.getTitle());
//                lable = contentParams.getSubtitle();
//                //这里的content是hash
//                transeUrl = contentParams.getJumpUrl();
//                break;
//            case ShareType.TYPE_VIDEO:
//                summary = contentParams.getContent();
//                session.setContent("视频 | " + contentParams.getTitle());
//                lable = "视频";
//                transeUrl = VideoDetailRouter.VIDEO_DETAILS + "?videoId=" + contentParams.getTargetId();
//                break;
//            case ShareType.TYPE_GROUP:
//                summary = contentParams.getContent();
//                session.setContent("分享了一个公开群");
//                lable = "公开群";
//                transeUrl = OpenGroupHomeRouter.MESSAGE_GROUP_HOME + "?groupId=" + contentParams.getTargetId();
//                break;
//            case ShareType.TYPE_IMAGE:
//                session.setContent("分享了一个图片");
//                lable = "图片";
//                break;
//            case ShareType.TYPE_LIVE:
//                summary = contentParams.getContent();
//                session.setContent("直播 | " + contentParams.getTitle());
//                lable = "直播";
//                transeUrl = LiveIDetailRouter.LIVE_DETAIL + "?liveId=" + contentParams.getTargetId();
//                break;
//            case ShareType.TYPE_POST:
//                summary = contentParams.getContent();
//                session.setContent("帖子 | " + contentParams.getTitle());
//                lable = "帖子";
//                transeUrl = QuestionItemRouter.QUESTION_ITEM + "?questionId=" + contentParams.getTargetId();
//                break;
//            case ShareType.TYPE_H5:
//            case ShareType.TYPE_TPL:
//                summary = contentParams.getContent();
//                session.setContent("链接 | " + contentParams.getTitle());
//                lable = "查看详情";
//                transeUrl = contentParams.getTargetUrl();
//                break;
//            case ShareType.TYPE_TEAM_SHARE:
//                summary = contentParams.getContent();
//                session.setContent("小组 | " + contentParams.getTitle());
//                lable = contentParams.getExtra();
//                transeUrl = GroupHomeRouter.GROUP_HOME + "?groupId=" + contentParams.getTargetId();
//                break;
//            case ShareType.TYPE_TIME_SHARE:
//                summary = contentParams.getContent();
//                session.setContent("时空 | " + contentParams.getTitle());
//                lable = "时空";
//                transeUrl = contentParams.getTargetUrl();
//                break;
//            case ShareType.TYPE_TOPIC_SHARE:
//                summary = contentParams.getContent();
//                session.setContent("话题 | " + contentParams.getTitle());
//                lable = "话题";
//                transeUrl = contentParams.getTargetUrl();
//                break;
//            default:
//        }
//        if (TextUtils.isEmpty(summary)) {
//            summary = "来自" + msgDbEntity.getFromUser().getName() + "的分享";
//        }
//
//        if (contentParams.getFrom() == ShareContentParams.FROM_H5 && !TextUtils.isEmpty(contentParams.getJumpUrl())) {
//            transeUrl = contentParams.getJumpUrl();
//        }
//        msgDbEntity.setCardLabel(lable);
//        msgDbEntity.setCardSummary(summary);
//        msgDbEntity.setCardTitle(contentParams.getTitle());
//        msgDbEntity.setCardImageUrl(contentParams.getImageUrl());
//        msgDbEntity.setCardTargetUrl(transeUrl);
//    }
}
