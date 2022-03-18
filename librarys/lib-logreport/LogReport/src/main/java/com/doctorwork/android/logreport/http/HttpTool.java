package com.doctorwork.android.logreport.http;

/**
 * @author : HuBoChao
 * @date : 2020/11/30
 * @desc : 网络请求封装
 */
public class HttpTool {

    private static HttpTool httpTool;
    private int retryCount;
    private int connectTimeout;
    private int readTimeout;
    private boolean useCaches;

    public static class Builder {
        private int retryCount;
        private int connectTimeout;
        private int readTimeout;
        private boolean useCaches;

        public Builder setRetryCount(int retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        public Builder setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder setUseCaches(boolean useCaches) {
            this.useCaches = useCaches;
            return this;
        }

        public HttpTool build() {
            if (httpTool == null) {
                httpTool = new HttpTool(this);
            }
            return httpTool;
        }
    }

    public static HttpTool getInstance() {
        if (httpTool == null) {
            throw new RuntimeException("no build");
        }
        return httpTool;
    }

    private HttpTool(Builder builder) {
        this.retryCount = builder.retryCount;
        this.connectTimeout = builder.connectTimeout;
        this.readTimeout = builder.readTimeout;
        this.useCaches = builder.useCaches;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public boolean isUseCaches() {
        return useCaches;
    }

    public <T> void sendMessage(String url, String requestData, IStringListener iStringListener) {
        HttpBean httpBean = new HttpBean();
        CallBackListener callBackListener = new StringCallBackImpl(iStringListener);
        HttpTask httpTask = new HttpTask(httpBean, url, requestData, callBackListener);
        ThreadManager.getInstance().addExe(httpTask);
    }
}
