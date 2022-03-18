package net.medlinker.imbusiness.entity;


import net.medlinker.base.entity.DataEntity;

/**
 * @author hmy
 * @time 2020-04-26 11:24
 */
public class VideoParamEntity extends DataEntity {

    private int callTime;
    private long firstVideoCallTime;
    private String miniProgramAppId;
    private String primitiveId;
    private int remainingTime;
    private String roomId;
    private String sdkAppId;
    private int type;
    private String userId;
    private String userSig;

    public int getCallTime() {
        return callTime;
    }

    public void setCallTime(int callTime) {
        this.callTime = callTime;
    }

    public long getFirstVideoCallTime() {
        return firstVideoCallTime;
    }

    public void setFirstVideoCallTime(long firstVideoCallTime) {
        this.firstVideoCallTime = firstVideoCallTime;
    }

    public String getMiniProgramAppId() {
        return miniProgramAppId;
    }

    public void setMiniProgramAppId(String miniProgramAppId) {
        this.miniProgramAppId = miniProgramAppId;
    }

    public String getPrimitiveId() {
        return primitiveId;
    }

    public void setPrimitiveId(String primitiveId) {
        this.primitiveId = primitiveId;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getSdkAppId() {
        return sdkAppId;
    }

    public void setSdkAppId(String sdkAppId) {
        this.sdkAppId = sdkAppId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserSig() {
        return userSig;
    }

    public void setUserSig(String userSig) {
        this.userSig = userSig;
    }
}
