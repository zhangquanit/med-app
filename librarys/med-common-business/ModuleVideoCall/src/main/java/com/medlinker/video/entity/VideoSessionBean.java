package com.medlinker.video.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

/**
 * @author hmy
 * @time 12/7/21 09:41
 */
@Keep
public class VideoSessionBean implements Parcelable {

    private String sessionId;
    private int duration; //总通话时长
    private int remainingTime; //剩余时长
    private int maxCallTime; //最大通话时长
    //    private int firstCallTime; //首次发起视频时间
//    private int firstStartTime; //首次接通时间
    private VideoBusinessBean business;
    private int status; //1:正常  2：已结束

    public int getStatus() {
        return status;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public int getMaxCallTime() {
        return maxCallTime;
    }

    public void setMaxCallTime(int maxCallTime) {
        this.maxCallTime = maxCallTime;
    }

    public VideoBusinessBean getBusiness() {
        return business;
    }

    public void setBusiness(VideoBusinessBean business) {
        this.business = business;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sessionId);
        dest.writeInt(this.duration);
        dest.writeInt(this.remainingTime);
        dest.writeInt(this.maxCallTime);
        dest.writeParcelable(this.business, flags);
        dest.writeInt(this.status);
    }

    public void readFromParcel(Parcel source) {
        this.sessionId = source.readString();
        this.duration = source.readInt();
        this.remainingTime = source.readInt();
        this.maxCallTime = source.readInt();
        this.business = source.readParcelable(VideoBusinessBean.class.getClassLoader());
        this.status = source.readInt();
    }

    public VideoSessionBean() {
    }

    protected VideoSessionBean(Parcel in) {
        this.sessionId = in.readString();
        this.duration = in.readInt();
        this.remainingTime = in.readInt();
        this.maxCallTime = in.readInt();
        this.business = in.readParcelable(VideoBusinessBean.class.getClassLoader());
        this.status = in.readInt();
    }

    public static final Creator<VideoSessionBean> CREATOR = new Creator<VideoSessionBean>() {
        @Override
        public VideoSessionBean createFromParcel(Parcel source) {
            return new VideoSessionBean(source);
        }

        @Override
        public VideoSessionBean[] newArray(int size) {
            return new VideoSessionBean[size];
        }
    };
}
