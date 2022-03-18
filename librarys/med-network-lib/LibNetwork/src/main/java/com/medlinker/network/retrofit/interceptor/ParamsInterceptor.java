package com.medlinker.network.retrofit.interceptor;

import com.medlinker.network.retrofit.INetworkConfig;

import java.io.IOException;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author hmy
 * @time 2021/1/5 10:24
 */
class ParamsInterceptor implements Interceptor {

    private INetworkConfig mNetworkConfig;

    ParamsInterceptor(INetworkConfig networkConfig) {
        mNetworkConfig = networkConfig;
    }

    @Override

    public Response intercept(Chain chain) throws IOException {
        Request request = addDefaultParams(chain.request());
        return chain.proceed(request);
    }

    private Request addDefaultParams(Request request) {
        HttpUrl.Builder builder = request.url().newBuilder();
        if (null != mNetworkConfig.getQueryParam()) {
            for (Map.Entry<String, String> entry : mNetworkConfig.getQueryParam().entrySet()) {
                builder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }
        HttpUrl httpUrl = builder.build();
        Request.Builder requestBuilder = request.newBuilder().url(httpUrl);
        if (null != mNetworkConfig.getHeaderParam()) {
            for (Map.Entry<String, String> entry : mNetworkConfig.getHeaderParam().entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return requestBuilder.build();
    }
}
