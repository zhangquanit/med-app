package com.medlinker.network.retrofit;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import okhttp3.EventListener;
import okhttp3.Interceptor;

/**
 * @author hmy
 * @time 2020/12/25 10:44
 */
public abstract class INetworkConfig {

    public abstract File getNetCacheDir();

    public abstract boolean isDebug();

    public abstract boolean isOnlineEnv();

    public String getPage() {
        return "";
    }

    /**
     * @param responseJson
     * @return
     */
    public abstract BusinessCodeEntity getBusinessCode(String responseJson);

    /**
     * @return 业务成功code
     */
    public int getBusinessSuccessCode() {
        return 0;
    }

    /**
     * 转换服务器错误信息
     *
     * @param code
     * @param msg
     * @return
     */
    public String transErrorMsg(int code, String msg) {
        return msg;
    }

    /**
     * 处理业务错误
     *
     * @param code
     * @param msg
     * @param response
     */
    public void onResponseError(int code, String msg, Object response) {
    }

    public abstract HashMap<String, String> getQueryParam();

    public HashMap<String, String> getHeaderParam() {
        return null;
    }

    public abstract boolean isNetworkUnconnected();

    public List<Interceptor> getInterceptor() {
        return null;
    }

    public List<Interceptor> getNetworkInterceptor() {
        return null;
    }

    public abstract void showToast(String msg);

    public EventListener getEventListener() {
        return null;
    }

    public String getErrMsgKey() {
        return "errmsg";
    }
}
