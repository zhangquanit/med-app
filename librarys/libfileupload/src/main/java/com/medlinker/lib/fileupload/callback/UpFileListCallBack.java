package com.medlinker.lib.fileupload.callback;


import com.medlinker.lib.fileupload.entity.FileEntity;

import java.util.ArrayList;

public interface UpFileListCallBack {

    /**
     * 上传内容成功后实现的方法
     *
     * @param fileList 文件返回图片
     */
    void upSuccess(ArrayList<FileEntity> fileList);

    /**
     * 上传内容失败后实现的方法
     *
     * @param error 上传失败
     */
    void upFailure(String error);

}
