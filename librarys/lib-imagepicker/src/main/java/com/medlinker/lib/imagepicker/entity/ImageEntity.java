package com.medlinker.lib.imagepicker.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class ImageEntity implements Parcelable {
    private int mIsSupportOriginal;
    private String mFilePath;
    private String mSmallPath;
    private String mFileSize;

    public ImageEntity(int isSupportOriginal, String path) {
        mIsSupportOriginal = isSupportOriginal;
        mFilePath = path;
    }

    public ImageEntity(String path) {
        mFilePath = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImageEntity)) return false;
        ImageEntity that = (ImageEntity) o;
        return Objects.equals(mFilePath, that.mFilePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mFilePath);
    }

    protected ImageEntity(Parcel in) {
        mIsSupportOriginal = in.readInt();
        mFilePath = in.readString();
        mSmallPath = in.readString();
        mFileSize = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mIsSupportOriginal);
        dest.writeString(mFilePath);
        dest.writeString(mSmallPath);
        dest.writeString(mFileSize);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ImageEntity> CREATOR = new Creator<ImageEntity>() {
        @Override
        public ImageEntity createFromParcel(Parcel in) {
            return new ImageEntity(in);
        }

        @Override
        public ImageEntity[] newArray(int size) {
            return new ImageEntity[size];
        }
    };

    public int getIsSupportOriginal() {
        return mIsSupportOriginal;
    }

    public void setIsSupportOriginal(int isSupportOriginal) {
        this.mIsSupportOriginal = isSupportOriginal;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        this.mFilePath = filePath;
    }

    public void setSmallPath(String path) {
        mSmallPath = path;
    }

    public String getSmallPath() {
        return mSmallPath;
    }

    public void setFileSize(String size) {
        mFileSize = size;
    }

    public String getFileSize() {
        return mFileSize;
    }
}
