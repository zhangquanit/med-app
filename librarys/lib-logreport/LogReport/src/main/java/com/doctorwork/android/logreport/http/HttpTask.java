package com.doctorwork.android.logreport.http;

import android.util.Log;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author : HuBoChao
 * @date : 2020/12/1
 * @desc : 请求任务
 */
public class HttpTask implements Runnable, Delayed {

    public static final String TAG = "HttpTask";

    private IHttpBean iHttpBean;
    private int retryCount;
    private long delayTime;

    public HttpTask(IHttpBean iHttpBean, String url, String requestData, CallBackListener callBackListener) {
        this.iHttpBean = iHttpBean;
        this.iHttpBean.setUrl(url);
        this.iHttpBean.setListener(callBackListener);
        this.iHttpBean.setMethod("POST");
        if (requestData != null) {
            Log.d(TAG, requestData);
            try {
                this.iHttpBean.setData(requestData.getBytes("UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int compareTo(Delayed o) {
        return 0;
    }

    public IHttpBean getiHttpBean() {
        return iHttpBean;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public long getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime + System.currentTimeMillis();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(getDelayTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        try {
            iHttpBean.execute();
        } catch (Exception e) {
            ThreadManager.getInstance().addRetry(this);
        }
    }
}
