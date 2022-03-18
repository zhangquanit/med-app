package com.medlinker.network.retrofit.interceptor;

import com.medlinker.network.retrofit.INetworkConfig;

import okhttp3.Interceptor;

/**
 * @author hmy
 * @time 2021/1/5 10:29
 */
public enum InterceptorHelper {
    INSTANCE;

    private Interceptor mParamsInterceptor;
    private Interceptor mCacheInterceptor;

    public Interceptor getParamsInterceptor(INetworkConfig config) {
        if (null == mParamsInterceptor) {
            mParamsInterceptor = new ParamsInterceptor(config);
        }
        return mParamsInterceptor;
    }

    public Interceptor getCacheInterceptor(INetworkConfig config) {
        if (null == mCacheInterceptor) {
            mCacheInterceptor = new CacheInterceptor(config);
        }
        return mCacheInterceptor;
    }

}
