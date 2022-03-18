package com.medlinker.video.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

import java.io.Serializable;

/**
 * @author hmy
 * @time 12/6/21 15:29
 */
@Keep
public class VideoCallIntentEntity implements Parcelable {
    private String userId;
    /**
     * 用户类型 1:医生 2:患者
     */
    private int userType;
    private int roomId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeInt(this.userType);
        dest.writeInt(this.roomId);
    }

    public void readFromParcel(Parcel source) {
        this.userId = source.readString();
        this.userType = source.readInt();
        this.roomId = source.readInt();
    }

    public VideoCallIntentEntity() {
    }

    protected VideoCallIntentEntity(Parcel in) {
        this.userId = in.readString();
        this.userType = in.readInt();
        this.roomId = in.readInt();
    }

    public static final Creator<VideoCallIntentEntity> CREATOR = new Creator<VideoCallIntentEntity>() {
        @Override
        public VideoCallIntentEntity createFromParcel(Parcel source) {
            return new VideoCallIntentEntity(source);
        }

        @Override
        public VideoCallIntentEntity[] newArray(int size) {
            return new VideoCallIntentEntity[size];
        }
    };
}
