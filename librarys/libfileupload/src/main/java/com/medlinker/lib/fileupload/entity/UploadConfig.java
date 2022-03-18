package com.medlinker.lib.fileupload.entity;

import android.text.TextUtils;


import java.util.ArrayList;
import java.util.List;


public class UploadConfig {

    private String uploadBucket = "";//上传的文件夹
    private int isPublic = 1;//是否公开 取值范围0, 1(1代表文件传入公共盘，所有人可以访问。0代表私人盘，需要授权访问)
    private int isWaterMark;//是否加水印 取值范围0, 1（0不加水印，1-加水印）
    private List<FileEntity> fileList;//需要上传的文件
    private String waterText = "medlinker";//水印文字
    private FileEntity fileEntity;

    public UploadConfig setFileEntity(FileEntity fileEntity) {
        this.fileEntity = fileEntity;
        return this;
    }

    public UploadConfig setUploadBucket(String uploadBucket) {
        this.uploadBucket = uploadBucket;
        return this;
    }

    public UploadConfig setIsPublic(boolean isPublic) {
        this.isPublic = isPublic ? 1 : 0;
        return this;
    }

    public UploadConfig setIsWaterMark(boolean isWaterMark) {
        this.isWaterMark = isWaterMark ? 1 : 0;
        return this;
    }

    public UploadConfig setFileList(List<FileEntity> fileList) {
        this.fileList = fileList;
        return this;
    }

    public UploadConfig setWaterText(String waterText) {
        if(TextUtils.isEmpty(waterText)){
            return this;
        }
        this.waterText = waterText;
        setIsWaterMark(true);
        return this;
    }

    public String getUploadBucket() {
        return uploadBucket;
    }

    public int isPublic() {
        return isPublic;
    }

    public int isWaterMark() {
        return isWaterMark;
    }

    public List<FileEntity> getFileList() {
        if (fileList == null) {
            fileList = new ArrayList<>();
        }
        return fileList;
    }

    public String getWaterText() {
        return waterText;
    }

    public FileEntity getFileEntity() {
        return fileEntity;
    }
}
