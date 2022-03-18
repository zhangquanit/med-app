package com.medlinker.login.bean;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

@Keep
public class SlideEntity implements Parcelable {

    @SerializedName("jigsawImageBase64")
    public String coverImage;
    @SerializedName("originalImageBase64")
    public String backgroundImage;
    public String secretKey;
    public String token;
    public boolean result;


    protected SlideEntity(Parcel in) {
        coverImage = in.readString();
        backgroundImage = in.readString();
        secretKey = in.readString();
        token = in.readString();
        result = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(coverImage);
        dest.writeString(backgroundImage);
        dest.writeString(secretKey);
        dest.writeString(token);
        dest.writeByte((byte) (result ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SlideEntity> CREATOR = new Creator<SlideEntity>() {
        @Override
        public SlideEntity createFromParcel(Parcel in) {
            return new SlideEntity(in);
        }

        @Override
        public SlideEntity[] newArray(int size) {
            return new SlideEntity[size];
        }
    };

    @Override
    public String toString() {
        return "SlideEntity{" +
                "coverUrl='" + coverImage + '\'' +
                ", dataUrl='" + backgroundImage + '\'' +
                '}';
    }
}
