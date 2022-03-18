package com.medlinker.network.retrofit;

import com.medlinker.network.retrofit.error.ErrorUtil;
import com.medlinker.network.retrofit.interceptor.InterceptorHelper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.Cache;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.TlsVersion;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author hmy
 * @time 2020/12/24 15:47
 */
public enum RetrofitProvider {
    INSTANCE;

    private static final long CONNECT_TIMEOUT = 30L;
    private static final long READ_TIMEOUT = 30L;
    private static final long WRITE_TIMEOUT = 30L;
    private static final long MAX_CACHE_SIZE = 5 * 1024 * 1024L;

    private static INetworkConfig sConfig;

    public static void init(INetworkConfig config) {
        sConfig = config;
    }

    public INetworkConfig getNetworkConfig() {
        return sConfig;
    }

    private OkHttpClient buildDefaultClient() {
        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_0)
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA,
                        CipherSuite.TLS_RSA_WITH_3DES_EDE_CBC_SHA)
                .build();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);
        builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        builder.cache(new Cache(sConfig.getNetCacheDir(), MAX_CACHE_SIZE));
        builder.retryOnConnectionFailure(true);

        if (sConfig.getEventListener() != null) {
            builder.eventListener(sConfig.getEventListener());
        }

        if (!sConfig.isDebug() && sConfig.isOnlineEnv()) {
            builder.connectionSpecs(Arrays.asList(spec, ConnectionSpec.CLEARTEXT))
                    .protocols(Collections.singletonList(Protocol.HTTP_1_1));
        }

        builder.addInterceptor(InterceptorHelper.INSTANCE.getParamsInterceptor(sConfig));
        builder.addInterceptor(InterceptorHelper.INSTANCE.getCacheInterceptor(sConfig));
        builder.addNetworkInterceptor(InterceptorHelper.INSTANCE.getCacheInterceptor(sConfig));

        List<Interceptor> interceptors = sConfig.getInterceptor();
        for (int i = 0, size = interceptors == null ? 0 : interceptors.size(); i < size; i++) {
            builder.addInterceptor(interceptors.get(i));
        }
        interceptors = sConfig.getNetworkInterceptor();
        for (int i = 0, size = interceptors == null ? 0 : interceptors.size(); i < size; i++) {
            builder.addNetworkInterceptor(interceptors.get(i));
        }
        return builder.build();
    }

    public Retrofit buildRetrofit(String baseUrl) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(buildDefaultClient())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public <T> T build(Retrofit retrofit, Class<T> cls) {
        if (retrofit == null) {
            throw new NullPointerException("retrofit is null");
        }
        return retrofit.create(cls);
    }

    public void checkResponse(int code, String msg, Object response) {
        ErrorUtil.checkResponse(code, msg, response);
    }

    public <T> Observable<T> throwResponseError(int code, String msg, Object response) {
        return ErrorUtil.throwResponseError(code, msg, response);
    }

    public void errorHandler(Throwable e) {
        ErrorUtil.errorHandler(e);
    }

}
