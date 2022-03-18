package com.doctorwork.android.logreport.http;

import android.os.Handler;
import android.os.Looper;

import java.io.InputStream;

/**
 * @author : HuBoChao
 * @date : 2020/11/30
 * @desc : String类型的回调实现
 */
public class StringCallBackImpl implements CallBackListener {

    private IStringListener iStringListener;
    private Handler handler = new Handler(Looper.getMainLooper());

    public StringCallBackImpl(IStringListener iStringListener) {
        this.iStringListener = iStringListener;
    }

    @Override
    public void onSuccess(InputStream inputStream) {
        final String string = Utils.getContent(inputStream);
        handler.post(new Runnable() {
            @Override
            public void run() {
                iStringListener.onSuccess(string);
            }
        });
    }

    @Override
    public void onFail() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                iStringListener.onFail();
            }
        });
    }
}
