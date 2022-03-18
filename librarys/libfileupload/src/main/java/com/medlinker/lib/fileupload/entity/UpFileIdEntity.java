package com.medlinker.lib.fileupload.entity;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;


import net.medlinker.base.entity.DataEntity;

import java.util.ArrayList;


public class UpFileIdEntity extends DataEntity {


    @SerializedName("success")
    private ArrayList<FileEntity> success;//返回成功id集合
    @SerializedName("failed")
    private ArrayList<FileEntity> failed;//返回失败id集合




    public UpFileIdEntity() {
    }

    protected UpFileIdEntity(Parcel in) {
        super(in);
        success = in.createTypedArrayList(FileEntity.CREATOR);
        failed = in.createTypedArrayList(FileEntity.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeTypedList(success);
        dest.writeTypedList(failed);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UpFileIdEntity> CREATOR = new Creator<UpFileIdEntity>() {
        @Override
        public UpFileIdEntity createFromParcel(Parcel in) {
            return new UpFileIdEntity(in);
        }

        @Override
        public UpFileIdEntity[] newArray(int size) {
            return new UpFileIdEntity[size];
        }
    };

    public ArrayList<FileEntity> getSuccess() {
        return success;
    }

    public void setSuccess(ArrayList<FileEntity> success) {
        this.success = success;
    }

    public ArrayList<FileEntity> getFailed() {
        return failed;
    }

    public void setFailed(ArrayList<FileEntity> failed) {
        this.failed = failed;
    }
}
