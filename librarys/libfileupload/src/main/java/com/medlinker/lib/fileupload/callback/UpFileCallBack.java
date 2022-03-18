package com.medlinker.lib.fileupload.callback;


import com.medlinker.lib.fileupload.entity.FileEntity;

public interface UpFileCallBack {

    /**
     * 上传内容成功后实现的方法
     *
     * @param file 文件上传
     */
    void upSuccess(FileEntity file);

    /**
     * 上传内容失败后实现的方法
     */
    void upFailure(String error);

    /**
     * 上传进度实现的方法
     *
     * @param file 文件上传保存名称
     * @param percent  文件上传进度
     */
    void upProgress(FileEntity file, double percent);


}
