package com.medlinker.video.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

import net.medlinker.base.entity.DataEntity;

@Keep
public class VideoRoomEntity extends DataEntity implements Parcelable {

    private VideoCallIntentEntity intentData;

    private VideoSessionBean session;
    private VideoConfigBean config;
    private VideoUserBean user;
    private boolean hasProcessingCall;

    public void setCallIntent(VideoCallIntentEntity intentData) {
        this.intentData = intentData;
    }

    private int getCurUserType() {
        if (intentData != null) {
            return intentData.getUserType();
        }
        return 0;
    }

    public boolean hasProcessingCall() {
        return hasProcessingCall;
    }

    public boolean isFinished() {
        return session == null || session.getStatus() == 2;
    }

    public boolean isDoctor() {
        return getCurUserType() == 1;
    }

    public boolean isPatient() {
        return getCurUserType() == 2;
    }

    public String getWaitInfo() {
        return isDoctor() ? "正在等待患者接受邀请" : "正在等待医生接受邀请";
    }

    public int getSdkAppId() {
        if (config != null) {
            return config.getSdkAppId();
        }
        return 0;
    }

    public int getLiveAppId() {
        if (config != null) {
            return config.getLiveAppId();
        }
        return 0;
    }

    public int getLiveBizId() {
        if (config != null) {
            return config.getLiveBizId();
        }
        return 0;
    }

    public String getUserId() {
        if (config != null) {
            return config.getUserId();
        }
        return null;
    }

    public String getUserSig() {
        if (config != null) {
            return config.getUserSig();
        }
        return null;
    }

    public int getRoomId() {
        if (intentData != null) {
            return intentData.getRoomId();
        }
        return 0;
    }

    public String getTransNo() {
        if (session != null && session.getBusiness() != null) {
            return session.getBusiness().getBusinessId();
        }
        return null;
    }

    public String getAvatar() {
        if (user != null) {
            return user.getAvatar();
        }
        return null;
    }

    public String getUserInfo() {
        if (user != null) {
            return user.getUserInfo();
        }
        return "";
    }

    public int getRemainingTime() {
        if (session != null) {
            return session.getRemainingTime();
        }
        return 0;
    }

    public void setRemainingTime(int remainingTime) {
        if (session != null) {
            session.setRemainingTime(remainingTime);
        }
    }

    public int getCallTime() {
        if (session != null) {
            return session.getMaxCallTime();
        }
        return 0;
    }


    public String getTotalTimeStr() {
        return String.format("%s分钟", getCallTime() / 60);
    }

    public boolean isTimeOut() {
        return getRemainingTime() <= 0;
    }

    public String getRemainingTimeFormat() {
        int spendTime = getCallTime() - getRemainingTime();
        int m = spendTime / 60;
        int s = spendTime % 60;
        String mStr = m < 10 ? "0" + m : String.valueOf(m);
        String sStr = s < 10 ? "0" + s : String.valueOf(s);
        return mStr + ":" + sStr;
    }

    public boolean isWoman() {
        return false; // 头像不区分性别
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.intentData, flags);
        dest.writeParcelable(this.session, flags);
        dest.writeParcelable(this.config, flags);
        dest.writeParcelable(this.user, flags);
        dest.writeByte(this.hasProcessingCall ? (byte) 1 : (byte) 0);
    }

    public void readFromParcel(Parcel source) {
        this.intentData = source.readParcelable(VideoCallIntentEntity.class.getClassLoader());
        this.session = source.readParcelable(VideoSessionBean.class.getClassLoader());
        this.config = source.readParcelable(VideoConfigBean.class.getClassLoader());
        this.user = source.readParcelable(VideoUserBean.class.getClassLoader());
        this.hasProcessingCall = source.readByte() != 0;
    }

    public VideoRoomEntity() {
    }

    protected VideoRoomEntity(Parcel in) {
        super(in);
        this.intentData = in.readParcelable(VideoCallIntentEntity.class.getClassLoader());
        this.session = in.readParcelable(VideoSessionBean.class.getClassLoader());
        this.config = in.readParcelable(VideoConfigBean.class.getClassLoader());
        this.user = in.readParcelable(VideoUserBean.class.getClassLoader());
        this.hasProcessingCall = in.readByte() != 0;
    }

    public static final Creator<VideoRoomEntity> CREATOR = new Creator<VideoRoomEntity>() {
        @Override
        public VideoRoomEntity createFromParcel(Parcel source) {
            return new VideoRoomEntity(source);
        }

        @Override
        public VideoRoomEntity[] newArray(int size) {
            return new VideoRoomEntity[size];
        }
    };
}
