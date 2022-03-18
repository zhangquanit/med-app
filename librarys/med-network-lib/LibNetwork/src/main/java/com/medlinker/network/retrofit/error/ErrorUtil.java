package com.medlinker.network.retrofit.error;

import android.net.ParseException;
import android.text.TextUtils;

import com.google.gson.JsonParseException;
import com.medlinker.network.retrofit.INetworkConfig;
import com.medlinker.network.retrofit.RetrofitProvider;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.SSLHandshakeException;

import io.reactivex.Observable;
import retrofit2.HttpException;

/**
 * @author hmy
 * @time 2021/1/5 15:53
 */
public class ErrorUtil {

    public static void checkResponse(int code, String msg, Object response) {
        INetworkConfig networkConfig = RetrofitProvider.INSTANCE.getNetworkConfig();
        if (code != networkConfig.getBusinessSuccessCode()) {
            msg = networkConfig.transErrorMsg(code, msg);
            networkConfig.onResponseError(code, msg, response);
            throw new ApiException(code, msg);
        }
    }

    public static <T> Observable<T> throwResponseError(int code, String msg, Object response) {
        INetworkConfig networkConfig = RetrofitProvider.INSTANCE.getNetworkConfig();
        msg = networkConfig.transErrorMsg(code, msg);
        networkConfig.onResponseError(code, msg, response);
        return Observable.error(new ApiException(code, msg));
    }

    /**
     * 异常统一处理
     */
    public static void errorHandler(Throwable e) {
        INetworkConfig networkConfig = RetrofitProvider.INSTANCE.getNetworkConfig();
        if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            String errorMsg = apiException.getMsg();
            if (!TextUtils.isEmpty(errorMsg)) {
                networkConfig.showToast(errorMsg);
            }
            return;
        }
        if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            networkConfig.showToast(
                    "网络异常" + "(" + httpException.code() + ")");
        } else if (e instanceof ConnectException) {
            networkConfig.showToast("网络连接接异常");
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            networkConfig.showToast(e.getMessage());
        } else if (e instanceof ConnectTimeoutException
                || e instanceof SocketTimeoutException
                || e instanceof TimeoutException) {
            networkConfig.showToast("网络连接超时");
        } else if (e instanceof SSLHandshakeException) {
            networkConfig.showToast("SSLHandshakeException");
        } else if (e instanceof UnknownHostException
                || e instanceof SocketException) {
            networkConfig.showToast("请检查网络连接");
        } else {
            e.printStackTrace();
            if (networkConfig.isDebug()) {
                networkConfig.showToast(e.getMessage());
            } else {
                networkConfig.showToast("出错啦");
            }
        }
    }
}
