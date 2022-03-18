package com.medlinker.lib.fileupload.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class UpFileEntity implements Parcelable {
    public String desc;// 文件描述 (可选)
    public String fileName;// 原图名称
    public int size;// 文件大小(可选)
    public int width;// 文件宽度(可选)
    public int height;// 文件高度(可选)
    public int duration;// 音频时长,单位秒(可选)

    /**
     * 客户端生成的唯一key,用来和本地图片对应
     */
    @SerializedName("uniqueKey")
    private String uniqueKey;

    public UpFileEntity() {
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.desc);
        dest.writeString(this.fileName);
        dest.writeInt(this.size);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeInt(this.duration);
        dest.writeString(this.uniqueKey);
    }

    protected UpFileEntity(Parcel in) {
        this.desc = in.readString();
        this.fileName = in.readString();
        this.size = in.readInt();
        this.width = in.readInt();
        this.height = in.readInt();
        this.duration = in.readInt();
        this.uniqueKey = in.readString();
    }

    public static final Creator<UpFileEntity> CREATOR = new Creator<UpFileEntity>() {
        @Override
        public UpFileEntity createFromParcel(Parcel source) {
            return new UpFileEntity(source);
        }

        @Override
        public UpFileEntity[] newArray(int size) {
            return new UpFileEntity[size];
        }
    };
}
