package com.doctorwork.android.logreport.http;

import java.io.InputStream;

/**
 * @author : HuBoChao
 * @date : 2020/11/30
 * @desc : 请求返回切入接口
 */
public interface CallBackListener {

    /**
     * 请求成功
     * @param inputStream
     */
    void onSuccess(InputStream inputStream);

    /**
     * 请求失败
     */
    void onFail();
}
