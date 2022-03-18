package com.medlinker.network.retrofit.interceptor;

import com.medlinker.network.retrofit.INetworkConfig;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author hmy
 * @time 2021/1/5 10:25
 */
class CacheInterceptor implements Interceptor {
    private static final String HEADER_CACHE_CONTROL = "Cache-Control";
    private static final String HEADER_PRAGMA = "Pragma";
    private static final String CACHE_CONTROL_ONLY_CACHE = "public, only-if-cached, max-age=2419200";
    private static final String CACHE_CONTROL_NO_CACHE = "public, max-age=1";

    private INetworkConfig mNetworkConfig;

    CacheInterceptor(INetworkConfig networkConfig) {
        mNetworkConfig = networkConfig;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (mNetworkConfig.isNetworkUnconnected()) {
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
        }
        String cacheControl;
        Response originalResponse = chain.proceed(request);
        if (mNetworkConfig.isNetworkUnconnected()) {
            cacheControl = CACHE_CONTROL_ONLY_CACHE;
        } else {
            cacheControl = CACHE_CONTROL_NO_CACHE;
        }
        return originalResponse.newBuilder()
                .header(HEADER_CACHE_CONTROL, cacheControl)
                .removeHeader(HEADER_PRAGMA)
                .build();
    }

}
