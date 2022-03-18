package com.medlinker.video.entity;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

/**
 * @author hmy
 * @time 12/7/21 10:10
 */
@Keep
public class VideoBusinessBean implements Parcelable {
    private String businessId;
    private int businessType;

    public String getBusinessId() {
        return businessId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.businessId);
        dest.writeInt(this.businessType);
    }

    public void readFromParcel(Parcel source) {
        this.businessId = source.readString();
        this.businessType = source.readInt();
    }

    public VideoBusinessBean() {
    }

    protected VideoBusinessBean(Parcel in) {
        this.businessId = in.readString();
        this.businessType = in.readInt();
    }

    public static final Parcelable.Creator<VideoBusinessBean> CREATOR = new Parcelable.Creator<VideoBusinessBean>() {
        @Override
        public VideoBusinessBean createFromParcel(Parcel source) {
            return new VideoBusinessBean(source);
        }

        @Override
        public VideoBusinessBean[] newArray(int size) {
            return new VideoBusinessBean[size];
        }
    };
}
