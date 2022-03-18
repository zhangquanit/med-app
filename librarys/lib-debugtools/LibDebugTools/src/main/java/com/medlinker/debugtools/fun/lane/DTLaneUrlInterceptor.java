package com.medlinker.debugtools.fun.lane;

import android.text.TextUtils;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * url拦截器
 *
 * @author zhangquan
 */
public class DTLaneUrlInterceptor implements Interceptor {

    private static final String TAG = "MedLaneUrlInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        request = laneIntercept(request);
        return chain.proceed(request);
    }

    /**
     * 泳道域名替换  由子类实现
     */

    private Request laneIntercept(Request request) {
        Request.Builder builder = request.newBuilder();
        String oldUrl = request.url().toString();
        String url = UrlReplaceHelper.urlInterceptor(oldUrl);
        if (TextUtils.equals(url, oldUrl)) {
            return request;
        }
        return builder.url(url).build();
    }
}
