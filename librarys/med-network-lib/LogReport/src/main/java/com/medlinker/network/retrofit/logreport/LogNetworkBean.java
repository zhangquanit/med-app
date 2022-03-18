package com.medlinker.network.retrofit.logreport;

import androidx.annotation.Keep;

import java.io.Serializable;

@Keep
public class LogNetworkBean implements Serializable {

    private String mRequestId;
    private String mUrl;
    private String mMethod;
    private String mRequestParam;

    private String mTraceId;
    private int mStatus;
    private int mSize;
    private long mCostTime;

    private long mCreateTime;

    public String getRequestId() {
        return mRequestId;
    }

    public void setRequestId(String requestId) {
        mRequestId = requestId;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getMethod() {
        return mMethod;
    }

    public void setMethod(String method) {
        mMethod = method;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public int getSize() {
        return mSize;
    }

    public void setSize(int size) {
        mSize = size;
    }

    public long getCostTime() {
        return mCostTime;
    }

    public void setCostTime(long costTime) {
        mCostTime = costTime;
    }

    public long getCreateTime() {
        return mCreateTime;
    }

    public void setCreateTime(long createTime) {
        mCreateTime = createTime;
    }

    @Override
    public String toString() {
        return "NetworkFeedBean{" +
                "mRequestId='" + mRequestId + '\'' +
                ", mUrl='" + mUrl + '\'' +
                ", mMethod='" + mMethod + '\'' +
                ", mTraceId='" + mTraceId + '\'' +
                ", mRequestParam='" + mRequestParam + '\'' +
                ", mStatus=" + mStatus +
                ", mSize=" + mSize +
                ", mCostTime=" + mCostTime +
                ", mCreateTime=" + mCreateTime +
                '}';
    }

    public String getTraceId() {
        return mTraceId;
    }

    public void setTraceId(String mTraceId) {
        this.mTraceId = mTraceId;
    }

    public String getRequestParam() {
        return mRequestParam;
    }

    public void setRequestParam(String requestParam) {
        this.mRequestParam = requestParam;
    }
}
