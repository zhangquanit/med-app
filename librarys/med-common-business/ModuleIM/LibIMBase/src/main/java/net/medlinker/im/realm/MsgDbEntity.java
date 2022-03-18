package net.medlinker.im.realm;

import androidx.annotation.NonNull;

import com.medlinker.protocol.message.types.AnchorOuterClass;

import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description 消息上层结构
 * @time 2017/2/27
 */
public class MsgDbEntity extends RealmObject implements Comparable<MsgDbEntity> {

    //消息状态为成功
    public static final int MSG_STATUS_SUCCEED = 0;
    //消息状态为发送中
    public static final int MSG_STATUS_SENDING = 1;
    //消息状态为失败
    public static final int MSG_STATUS_FAILURE = 2;
    //撤销间隔时间
    public static final int MSG_WITHDRAW_TIME = 2 * 60 * 1000;
    //能够重新编辑时间
    public static final int MSG_RE_EDIT_TIME = 10 * 60 * 1000;

    @PrimaryKey
    private long timestamp;
    private long withdrawTimestamp;
    private long id;
    private int dataType;//消息类型
    private int msgSendStatus;
    private boolean hasRead;
    private String sessionId;
    private boolean isSelfMsg;
    private ImUserDbEntity fromUser;//来源user
    private ImUserDbEntity toUser;//2user
    private ImGroupDbEntity toGroup;//2group
    private boolean isGroup;//目的地类型
    //操作事件相关
    private boolean isWithDraw;//是否被撤回
    //基础数据类型结构
    private String imageUrl;
    private int imageSize;
    private String imagePreview;
    private double longitude;
    private double latitude;
    private String noticeContent;
    private String textContent;
    private RealmList<ImTextLink> links;//文本的link跳转
    private RealmList<ImUserDbEntity> atUsers;//at的人员列表
    private String voiceUrl;
    private String voiceLocalPath;
    private int voiceDuration;
    private boolean hasPlayVoice;
    //卡片类型，很多种
    private ImUserDbEntity cardUser;
    //公共卡片样式
    private int cardType;
    private String cardTitle;
    private String cardLabel;
    private String cardSubTitle;
    private String cardSummary;
    private String cardImageUrl;
    private String cardTargetUrl;
    private String cardExtra;//card带的额外数据，便于扩展
    private int cardDisplay;//显示位置 0:系统消息会话; 1:toast only; 2:会话+toast
    //问诊和处方类型，直接存储json串
    private String jsonString;
    private int jsonType;//1问诊问题 2 问诊解答、 处方 10 评分
    //5.2.2
    private String compactText;//兼容文本信息
    private long hospitalId; //医院id
    private MsgExtEntity ext;

    @Override
    public String toString() {
        return "MsgDbEntity{" +
                "timestamp=" + timestamp +
                "withdrawTimestamp=" + withdrawTimestamp +
                ", id=" + id +
                ", dataType=" + dataType +
                ", msgSendStatus=" + msgSendStatus +
                ", hasRead=" + hasRead +
                ", sessionId='" + sessionId + '\'' +
                ", isSelfMsg=" + isSelfMsg +
                ", fromUser=" + fromUser +
                ", toUser=" + toUser +
                ", toGroup=" + toGroup +
                ", isGroup=" + isGroup +
                ", isWithDraw=" + isWithDraw +
                ", imageUrl='" + imageUrl + '\'' +
                ", imageSize=" + imageSize +
                ", imagePreview='" + imagePreview + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", noticeContent='" + noticeContent + '\'' +
                ", textContent='" + textContent + '\'' +
                ", links=" + links +
                ", atUsers=" + atUsers +
                ", voiceUrl='" + voiceUrl + '\'' +
                ", voiceLocalPath='" + voiceLocalPath + '\'' +
                ", voiceDuration=" + voiceDuration +
                ", hasPlayVoice=" + hasPlayVoice +
                ", cardUser=" + cardUser +
                ", cardType=" + cardType +
                ", cardTitle='" + cardTitle + '\'' +
                ", cardLabel='" + cardLabel + '\'' +
                ", cardSubTitle='" + cardSubTitle + '\'' +
                ", cardSummary='" + cardSummary + '\'' +
                ", cardImageUrl='" + cardImageUrl + '\'' +
                ", cardTargetUrl='" + cardTargetUrl + '\'' +
                ", cardExtra='" + cardExtra + '\'' +
                ", cardDisplay=" + cardDisplay +
                ", jsonString='" + jsonString + '\'' +
                ", jsonType=" + jsonType +
                ", compactText='" + compactText + '\'' +
                ", hospitalId=" + hospitalId +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getWithdrawTimestamp() {
        return withdrawTimestamp;
    }

    public void setWithdrawTimestamp(long withdrawTimestamp) {
        this.withdrawTimestamp = withdrawTimestamp;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public ImUserDbEntity getFromUser() {
        return fromUser;
    }

    public void setFromUser(ImUserDbEntity fromUser) {
        this.fromUser = fromUser;
    }

    public ImUserDbEntity getToUser() {
        return toUser;
    }

    public void setToUser(ImUserDbEntity toUser) {
        this.toUser = toUser;
    }

    public ImGroupDbEntity getToGroup() {
        return toGroup;
    }

    public void setToGroup(ImGroupDbEntity toGroup) {
        this.toGroup = toGroup;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getImageSize() {
        return imageSize;
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
    }

    public String getImagePreview() {
        return imagePreview;
    }

    public void setImagePreview(String imagePreview) {
        this.imagePreview = imagePreview;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getNoticeContent() {
        return noticeContent;
    }

    public void setNoticeContent(String noticeContent) {
        this.noticeContent = noticeContent;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public String getVoiceUrl() {
        return voiceUrl;
    }

    public void setVoiceUrl(String voiceUrl) {
        this.voiceUrl = voiceUrl;
    }

    public int getVoiceDuration() {
        return voiceDuration;
    }

    public void setVoiceDuration(int voiceDuration) {
        this.voiceDuration = voiceDuration;
    }

    public ImUserDbEntity getCardUser() {
        return cardUser;
    }

    public void setCardUser(ImUserDbEntity cardUser) {
        this.cardUser = cardUser;
    }


    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isSelfMsg() {
        return isSelfMsg;
    }

    public void setSelfMsg(boolean selfMsg) {
        isSelfMsg = selfMsg;
    }

    public int getMsgSendStatus() {
        return msgSendStatus;
    }

    public void setMsgSendStatus(int msgSendStatus) {
        this.msgSendStatus = msgSendStatus;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public boolean isWithDraw() {
        return isWithDraw;
    }

    public void setWithDraw(boolean withDraw) {
        isWithDraw = withDraw;
    }

    public String getVoiceLocalPath() {
        return voiceLocalPath;
    }

    public void setVoiceLocalPath(String voiceLocalPath) {
        this.voiceLocalPath = voiceLocalPath;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public String getCardTitle() {
        return cardTitle;
    }

    public void setCardTitle(String cardTitle) {
        this.cardTitle = cardTitle;
    }

    public String getCardSubTitle() {
        return cardSubTitle;
    }

    public void setCardSubTitle(String cardSubTitle) {
        this.cardSubTitle = cardSubTitle;
    }

    public String getCardSummary() {
        return cardSummary;
    }

    public void setCardSummary(String cardSummary) {
        this.cardSummary = cardSummary;
    }

    public String getCardImageUrl() {
        return cardImageUrl == null ? "" : cardImageUrl;
    }

    public void setCardImageUrl(String cardImageUrl) {
        this.cardImageUrl = cardImageUrl;
    }

    public String getCardTargetUrl() {
        if (cardTargetUrl == null) {
            return "";
        }
        return cardTargetUrl;
    }

    public void setCardTargetUrl(String cardTargetUrl) {
        this.cardTargetUrl = cardTargetUrl;
    }

    public String getCardLabel() {
        return cardLabel;
    }

    public void setCardLabel(String cardLabel) {
        this.cardLabel = cardLabel;
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    public boolean isHasPlayVoice() {
        return hasPlayVoice;
    }

    public void setHasPlayVoice(boolean hasPlayVoice) {
        this.hasPlayVoice = hasPlayVoice;
    }

    public int getJsonType() {
        return jsonType;
    }

    public void setJsonType(int jsonType) {
        this.jsonType = jsonType;
    }

    public boolean isHasRead() {
        return hasRead;
    }

    public void setHasRead(boolean hasRead) {
        this.hasRead = hasRead;
    }

    public int getCardDisplay() {
        return cardDisplay;
    }

    public void setCardDisplay(int cardDisplay) {
        this.cardDisplay = cardDisplay;
    }

    public String getCardExtra() {
        return cardExtra;
    }

    public void setCardExtra(String cardExtra) {
        this.cardExtra = cardExtra;
    }

    public long getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(long hospitalId) {
        this.hospitalId = hospitalId;
    }

    public RealmList<ImUserDbEntity> getAtUsers() {
        if (atUsers == null) atUsers = new RealmList<>();
        return atUsers;
    }

    public void setAtUsers(RealmList<ImUserDbEntity> atUsers) {
        this.atUsers = atUsers;
    }

    public void addAtUsers(ImUserDbEntity atUser) {
        getAtUsers().add(atUser);
    }

    public RealmList<ImTextLink> getLinks() {
        if (links == null) {
            links = new RealmList<>();
        }
        return links;
    }

    public void setLinks(RealmList<ImTextLink> links) {
        this.links = links;
    }

    public void setLinks(List<AnchorOuterClass.Anchor> anchorsList) {
        RealmList<ImTextLink> realmList = new RealmList<ImTextLink>();
        for (AnchorOuterClass.Anchor anchor : anchorsList) {
            ImTextLink link = new ImTextLink();
            link.setStartIndex(anchor.getStart());
            link.setLinkLenth(anchor.getLen());
            link.setLinkUrl(anchor.getHref());
            realmList.add(link);
        }
        setLinks(realmList);
    }

    public String getCompactText() {
        return compactText;
    }

    public void setCompactText(String compactText) {
        this.compactText = compactText;
    }

    public MsgExtEntity getExt() {
        return ext;
    }

    public void setExt(MsgExtEntity ext) {
        this.ext = ext;
    }

    @Override
    public int compareTo(@NonNull MsgDbEntity o) {
        if (getTimestamp() > o.getTimestamp()) {
            return -1;
        }
        return 1;
    }
}
