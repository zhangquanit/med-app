package com.medlinker.debugtools.manager;

import com.didichuxing.doraemonkit.aop.OkHttpHook;
import com.medlinker.debugtools.fun.lane.DTLaneUrlInterceptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.Interceptor;

/**
 *  添加OkHttp网络请求拦截器
 */
public class DTOkHttpManager {

    private static final DTOkHttpManager sDTOkHttpManager = new DTOkHttpManager();

    private Map<String, Interceptor> mGlobalInterceptors = new HashMap<>(4);
    private Map<String, Interceptor> mGlobalNetworkInterceptors = new HashMap<>(4);

    public static DTOkHttpManager instance(){
        return sDTOkHttpManager;
    }

    public void addGlobalInterceptors(Interceptor interceptor){
        Objects.requireNonNull(interceptor);
        String key = getName(interceptor);
        if (mGlobalInterceptors.containsKey(key)){
            return;
        }
        mGlobalInterceptors.put(key,interceptor);
        OkHttpHook.globalInterceptors.add(interceptor);
    }

    public void addGlobalNetworkInterceptors(Interceptor interceptor){
        Objects.requireNonNull(interceptor);
        String key = getName(interceptor);
        if (mGlobalNetworkInterceptors.containsKey(key)){
            return;
        }
        mGlobalNetworkInterceptors.put(key,interceptor);
        OkHttpHook.globalNetworkInterceptors.add(interceptor);
    }

    public void removeGlobalInterceptors(String key){
        if (!mGlobalInterceptors.containsKey(key)){
            return;
        }
        Interceptor value = mGlobalInterceptors.remove(key);
        OkHttpHook.globalInterceptors.remove(value);
    }

    public void removeGlobalNetworkInterceptors(String key){
        if (!mGlobalNetworkInterceptors.containsKey(key)){
            return;
        }
        Interceptor value = mGlobalNetworkInterceptors.remove(key);
        OkHttpHook.globalNetworkInterceptors.remove(value);
    }

    public void clearGlobalInterceptors(){
        for (Interceptor item : mGlobalInterceptors.values()) {
            OkHttpHook.globalInterceptors.remove(item);
        }
        mGlobalInterceptors.clear();
    }

    public void clearGlobalNetworkInterceptors(){
        for (Interceptor item : mGlobalNetworkInterceptors.values()) {
            OkHttpHook.globalNetworkInterceptors.remove(item);
        }
        mGlobalNetworkInterceptors.clear();
    }

    public void clear(){
        clearGlobalInterceptors();
        clearGlobalNetworkInterceptors();
    }

    private String getName(Interceptor interceptor){
        return interceptor.getClass().getSimpleName();
    }
}
