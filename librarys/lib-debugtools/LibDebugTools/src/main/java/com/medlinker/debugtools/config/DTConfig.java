package com.medlinker.debugtools.config;

/**
 * @author: pengdaosong
 * @CreateTime: 2021/3/8 4:18 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class DTConfig {

    private static final DTConfig sDTConfig = new DTConfig();

    private volatile DTLaneConfig mLaneConfig;
    private volatile DTNetworkCaptureConfig mNetworkCaptureConfig;
    private volatile DTRouterConfig mRouterConfig;
    private volatile DTPluginExtConfig mPluginExtConfig;

    public static DTConfig instance(){
        return sDTConfig;
    }

    public DTConfig initLaneConfig(DTLaneConfig laneConfig){
        mLaneConfig = laneConfig;
        return this;
    }

    public DTLaneConfig getLaneConfig(){
        if (null == mLaneConfig){
            mLaneConfig = new DTLaneConfig();
        }
        return mLaneConfig;
    }

    public DTConfig initNetworkCaptureConfig(DTNetworkCaptureConfig networkCaptureConfig){
        mNetworkCaptureConfig = networkCaptureConfig;
        return this;
    }

    public DTNetworkCaptureConfig getNetworkCaptureConfig(){
        if (null == mNetworkCaptureConfig){
            mNetworkCaptureConfig = new DTNetworkCaptureConfig();
        }
        return mNetworkCaptureConfig;
    }

    public DTConfig initRouterConfig(DTRouterConfig routerConfig){
        mRouterConfig = routerConfig;
        return this;
    }

    public DTRouterConfig getRouterConfig(){
        if (null == mRouterConfig){
            mRouterConfig = new DTRouterConfig();
        }
        return mRouterConfig;
    }

    public DTPluginExtConfig getPluginExtConfig(){
        if (null == mPluginExtConfig){
            mPluginExtConfig = new DTPluginExtConfig();
            mPluginExtConfig.initPluginExtConfig();
        }
        return mPluginExtConfig;
    }
}
