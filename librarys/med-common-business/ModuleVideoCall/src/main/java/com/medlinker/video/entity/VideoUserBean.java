package com.medlinker.video.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

/**
 * @author hmy
 * @time 12/7/21 10:01
 */
@Keep
public class VideoUserBean implements Parcelable {

    private String avatar;
    private String userInfo;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.avatar);
        dest.writeString(this.userInfo);
    }

    public void readFromParcel(Parcel source) {
        this.avatar = source.readString();
        this.userInfo = source.readString();
    }

    public VideoUserBean() {
    }

    protected VideoUserBean(Parcel in) {
        this.avatar = in.readString();
        this.userInfo = in.readString();
    }

    public static final Parcelable.Creator<VideoUserBean> CREATOR = new Parcelable.Creator<VideoUserBean>() {
        @Override
        public VideoUserBean createFromParcel(Parcel source) {
            return new VideoUserBean(source);
        }

        @Override
        public VideoUserBean[] newArray(int size) {
            return new VideoUserBean[size];
        }
    };
}
