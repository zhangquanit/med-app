package com.medlinker.debugtools.config;

import com.medlinker.debugtools.base.CallBack;

public class DTRouterConfig {
    private CallBack<String> mCallBack;

    public DTRouterConfig routerConfig(CallBack<String> callBack){
        mCallBack = callBack;
        return this;
    }

    public CallBack<String> getCallBack(){
        return mCallBack;
    }
}
