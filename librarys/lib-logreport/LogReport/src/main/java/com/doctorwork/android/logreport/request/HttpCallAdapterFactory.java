package com.doctorwork.android.logreport.request;

import com.doctorwork.android.logreport.http.HttpTool;
import com.doctorwork.android.logreport.http.IStringListener;

/**
 * HttpUrlConnection执行网络请求
 *
 * @author zhangquan
 */
public class HttpCallAdapterFactory implements CallAdapterFactory {
    private HttpCallAdapterFactory() {
        //初始化网络请求框架
        new HttpTool.Builder().setRetryCount(3)
                .setConnectTimeout(6000)
                .setReadTimeout(3000)
                .setUseCaches(false)
                .build();
    }

    public static CallAdapterFactory create() {
        return new HttpCallAdapterFactory();
    }

    @Override
    public void call(String url, String jsonData) {
        HttpTool.getInstance().sendMessage(url, jsonData, new IStringListener() {
            @Override
            public void onSuccess(String s) {

            }

            @Override
            public void onFail() {

            }
        });
    }
}
