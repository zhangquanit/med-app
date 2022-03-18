package com.medlinker.debugtools.config;

import java.util.List;

public class DTNetworkCaptureConfig {

    private List<String> mNotCaptureDomains;

    public DTNetworkCaptureConfig setNotCaptureDomains(List<String> domains){
        mNotCaptureDomains = domains;
        return this;
    }

    public boolean isNotCapture(String domain){
        if (null == mNotCaptureDomains){
            return false;
        }
        return mNotCaptureDomains.contains(domain);
    }
}
