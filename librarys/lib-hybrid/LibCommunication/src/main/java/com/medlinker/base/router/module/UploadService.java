package com.medlinker.base.router.module;

import com.alibaba.android.arouter.facade.template.IProvider;

import java.util.Map;

import io.reactivex.Observable;

/**
 * 文件上传
 *
 * @author zhangquan
 */
public interface UploadService extends IProvider {
    String KEY_BUCKET = "bucket";
    String KEY_FILE_URL = "fileUrl";
    String KEY_DURATION = "duration";

    Observable<String> upload(Map<String, String> params);

}
