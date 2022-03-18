package com.doctorwork.android.logreport.okhttp;


import com.doctorwork.android.logreport.request.CallAdapterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author zhangquan
 */
public class OkHttpCallAdapterFactory implements CallAdapterFactory {
    private OkHttpClient mOkHttpClient;

    private OkHttpCallAdapterFactory() {
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5 * 1000, TimeUnit.MILLISECONDS) //链接超时
                .readTimeout(10 * 1000, TimeUnit.MILLISECONDS) //读取超时
                .writeTimeout(10 * 1000, TimeUnit.MILLISECONDS) //写入超时
                .build();
    }

    public static OkHttpCallAdapterFactory create() {
        return new OkHttpCallAdapterFactory();
    }

    @Override
    public void call(String url, String jsonData) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonData);
        Request request = new Request.Builder().url(url)
                .post(requestBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }
}
