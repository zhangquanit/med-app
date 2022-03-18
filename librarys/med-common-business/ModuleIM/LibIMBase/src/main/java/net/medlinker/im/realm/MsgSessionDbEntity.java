package net.medlinker.im.realm;


import android.text.TextUtils;

import com.medlinker.protocol.message.MessageOuterClass;

import net.medlinker.im.helper.EntityConvertHelper;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description
 * @time 2017/2/22
 */
public class MsgSessionDbEntity extends RealmObject {

    @PrimaryKey
    private String id;//fromId+"-"+fromType 或 fromId+"-"+fromType+"-"+hospitalId
    private String sessionId;//真的id，fromId+"-"+fromType
    private ImUserDbEntity fromUser;//发送者use信息，包含头像，名字等。
    private ImGroupDbEntity fromGroup;//来自群聊的信息
    private boolean isGroup;//是user还是群聊
    private String content;
    private long timeStamp;
    private int unreadMsgCount;//未读消息数目
    private boolean isStickChat;//是否置顶
    private boolean isRejectMsg;//是否消息免打扰
    private boolean hasRejectMsg;//是否有被打扰的消息
    private String parentSessId;//父session的id
    private boolean isChildSession;//是否是子session
    private boolean hasChildSession;//是否有子session
    //如果是求助患者，通知类型等二级界面包含子session的会话
    private int sessionType = 0;//会话类型，0是普通会话 1 是问诊患者 2 普通患者 3 小组群 4随诊群 5多学科会诊 6开药门诊
    private boolean isHistorySession = false;//是否来源于历史消息记录列表的数据，用来判断拉取历史消息
    private int start;//历史会话消息记录的偏移量，当ishistorySession=true有效
    private String draft;//草稿
    private String draftAtUsers;//@草稿的user数据
    private boolean isHaveAt;//有人at自己的消息
    private boolean isHotGroups = false;
    private String ecasemUrl;//患者病历地址，只有为随诊会话的时候才会有，businesstype==4
    private boolean isTimeValid = true;//是否已经进入免打扰时间
    private ImUserDbEntity lastMsgFromUser;//最后发送者use信息，包含头像，名字等。
    private int lastMsgType; // 最后一条消息的type
    private long hospitalId; //医院id
    private boolean isProcessed; // 医生是否已经处理会话
    private int dialogueRole;// 角色：0 - 医生 1 - 智能小秘书 2 - 客服，客服助手 , 营养师，营养师助手

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public String getSessionId() {
        return TextUtils.isEmpty(sessionId) ? id : sessionId;
    }

    public void setSessionId(String id) {
        this.sessionId = id;
        setHotGroups(id.equals(EntityConvertHelper.SESSION_ID_HOT_GROUPS));
    }

    public boolean isEmptySessionId() {
        return TextUtils.isEmpty(sessionId);
    }

    public ImUserDbEntity getFromUser() {
        return fromUser;
    }

    public void setFromUser(ImUserDbEntity fromUser) {
        this.fromUser = fromUser;
    }

    public ImUserDbEntity getLastMsgFromUser() {
        return lastMsgFromUser;
    }

    public void setLastMsgFromUser(ImUserDbEntity lastMsgFromUser) {
        this.lastMsgFromUser = lastMsgFromUser;
    }

    public boolean isHasChildSession() {
        return hasChildSession;
    }

    public void setHasChildSession(boolean hasChildSession) {
        this.hasChildSession = hasChildSession;
    }

    public ImGroupDbEntity getFromGroup() {
        return fromGroup;
    }

    public void setFromGroup(ImGroupDbEntity fromGroup) {
        this.fromGroup = fromGroup;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getUnreadMsgCount() {
        return unreadMsgCount > 99 ? 99 : unreadMsgCount;
    }

    public int getUnreadMsgRealCount() {
        return unreadMsgCount;
    }

    public void setUnreadMsgCount(int unreadMsgCount) {
        this.unreadMsgCount = unreadMsgCount;
        this.hasRejectMsg = false;
    }

    public String getUnreadMsgCountStr() {
        return unreadMsgCount > 99 ? "99+" : String.valueOf(unreadMsgCount);
    }

    public boolean isStickChat() {
        return isStickChat;
    }

    public void setStickChat(boolean stickChat) {
        isStickChat = stickChat;
    }

    public boolean isRejectMsg() {
        return isRejectMsg;
    }

    public void setRejectMsg(boolean rejectMsg) {
        isRejectMsg = rejectMsg;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public String getDraft() {
        return draft;
    }

    public void setDraft(String draft) {
        this.draft = draft;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    private boolean isInquiryChildSession() {
        return isChildSession && EntityConvertHelper.SESSION_ID_INQUIRY.equals(parentSessId);
    }

    private boolean isClinicChildSession() {
        return isChildSession && EntityConvertHelper.SESSION_ID_CLINIC.equals(parentSessId);
    }

    public void setChildSession(boolean childSession) {
        isChildSession = childSession;
    }

    public int getSessionType() {
        return sessionType;
    }

    public void setSessionType(int sessionType) {
        this.sessionType = sessionType;
    }

    public boolean isHasRejectMsg() {
        return hasRejectMsg;
    }

    public void setHasRejectMsg(boolean hasRejectMsg) {
        this.hasRejectMsg = hasRejectMsg;
    }

    public boolean isHistorySession() {
        return isHistorySession;
    }

    public void setHistorySession(boolean historySession) {
        isHistorySession = historySession;
    }

    public boolean isHaveAt() {
        return isHaveAt;
    }

    public void setHaveAt(boolean haveAt) {
        isHaveAt = haveAt;
    }

    public String getParentSessId() {
        return parentSessId;
    }

    public String getDraftAtUsers() {
        return draftAtUsers;
    }

    public boolean isHotGroups() {
        return isHotGroups;
    }

    public void setHotGroups(boolean hotGroups) {
        isHotGroups = hotGroups;
    }

    public void setDraftAtUsers(String draftAtUsers) {
        this.draftAtUsers = draftAtUsers;
    }

    public void setParentSessId(String parentSessId) {
        this.parentSessId = parentSessId;
        setChildSession(true);
    }

    public void multiUnread() {
        if (unreadMsgCount > 0) {
            unreadMsgCount = unreadMsgCount - 1;
        }
    }

    public boolean isTimeValid() {
        return isTimeValid;
    }

    public void setTimeValid(boolean timeValid) {
        isTimeValid = timeValid;
    }

    public String getEcasemUrl() {
        return ecasemUrl;
    }

    public void setEcasemUrl(String ecasemUrl) {
        this.ecasemUrl = ecasemUrl;
    }

    public long getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(long hospitalId) {
        this.hospitalId = hospitalId;
    }

    public boolean isChildSession() {
        return isChildSession;
    }

    public int getLastMsgType() {
        return lastMsgType;
    }

    public void setLastMsgType(int lastMsgType) {
        this.lastMsgType = lastMsgType;
    }

    public boolean isNoticeMsg() {
        return lastMsgType == MessageOuterClass.Message.DataCase.NOTICE.getNumber();
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    public void setProcessed(boolean processed) {
        isProcessed = processed;
    }

    public boolean isDoctorRole() {
        return 0 == dialogueRole;
    }

    public void setDialogueRole(int dialogueRole) {
        this.dialogueRole = dialogueRole;
    }

    public boolean isDoctorsRole() {
        return this.dialogueRole == 0;
    }

    @Override
    public String toString() {
        return "MsgSessionDbEntity{" +
                "id='" + id + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", fromUser=" + fromUser +
                ", lastMsgFromUser=" + lastMsgFromUser +
                ", fromGroup=" + fromGroup +
                ", isGroup=" + isGroup +
                ", content='" + content + '\'' +
                ", timeStamp=" + timeStamp +
                ", unreadMsgCount=" + unreadMsgCount +
                ", isStickChat=" + isStickChat +
                ", isRejectMsg=" + isRejectMsg +
                ", hasRejectMsg=" + hasRejectMsg +
                ", parentSessId='" + parentSessId + '\'' +
                ", isChildSession=" + isChildSession +
                ", hasChildSession=" + hasChildSession +
                ", sessionType=" + sessionType +
                ", isHistorySession=" + isHistorySession +
                ", start=" + start +
                ", draft='" + draft + '\'' +
                ", draftAtUsers='" + draftAtUsers + '\'' +
                ", isHaveAt=" + isHaveAt +
                ", isHotGroups=" + isHotGroups +
                ", ecasemUrl='" + ecasemUrl + '\'' +
                ", isTimeValid=" + isTimeValid +
                ", lastMsgType=" + lastMsgType +
                ", hospitalId=" + hospitalId +
                ", isProcessed=" + isProcessed +
                ", dialogueRole=" + dialogueRole +
                '}';
    }
}
