package com.medlinker.video.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

/**
 * @author hmy
 * @time 12/7/21 09:49
 */
@Keep
public class VideoConfigBean implements Parcelable {

    private int sdkAppId;
    private int liveAppId;
    private int liveBizId;
    private String userId;
    private String userSig;

    public int getSdkAppId() {
        return sdkAppId;
    }

    public void setSdkAppId(int sdkAppId) {
        this.sdkAppId = sdkAppId;
    }

    public int getLiveAppId() {
        return liveAppId;
    }

    public void setLiveAppId(int liveAppId) {
        this.liveAppId = liveAppId;
    }

    public int getLiveBizId() {
        return liveBizId;
    }

    public void setLiveBizId(int liveBizId) {
        this.liveBizId = liveBizId;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.sdkAppId);
        dest.writeInt(this.liveAppId);
        dest.writeInt(this.liveBizId);
        dest.writeString(this.userId);
        dest.writeString(this.userSig);
    }

    public void readFromParcel(Parcel source) {
        this.sdkAppId = source.readInt();
        this.liveAppId = source.readInt();
        this.liveBizId = source.readInt();
        this.userId = source.readString();
        this.userSig = source.readString();
    }

    public VideoConfigBean() {
    }

    protected VideoConfigBean(Parcel in) {
        this.sdkAppId = in.readInt();
        this.liveAppId = in.readInt();
        this.liveBizId = in.readInt();
        this.userId = in.readString();
        this.userSig = in.readString();
    }

    public static final Creator<VideoConfigBean> CREATOR = new Creator<VideoConfigBean>() {
        @Override
        public VideoConfigBean createFromParcel(Parcel source) {
            return new VideoConfigBean(source);
        }

        @Override
        public VideoConfigBean[] newArray(int size) {
            return new VideoConfigBean[size];
        }
    };
}
