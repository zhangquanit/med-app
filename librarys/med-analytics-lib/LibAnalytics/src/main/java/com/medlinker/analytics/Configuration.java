package com.medlinker.analytics;



public class Configuration {
    /**
     * x-header: {
     * token: //用户token
     * userId //用户id
     * channel //来源
     * appCode //应用编号 (补应用关联后台)
     * platform //平台,iOS,Android, h5
     * bizCode //业务编码
     * extra:{phone:oaxxsf,idCard:51022312,mid:11}
     * }
     */
    private String appCode;
    private String channel;
    private int trackType;
    private boolean isDebug;
    private String platformName;
    private String userType;

    public Configuration() {
    }

    public Configuration appCode(String appCode) {
        this.appCode = appCode;
        return this;
    }

    public Configuration channel(String channel) {
        this.channel = channel;
        return this;
    }

    public Configuration debug(boolean debug) {
        this.isDebug = debug;
        return this;
    }


    public boolean isDebug() {
        return isDebug;
    }

    public Configuration trackType(int trackType) {
        this.trackType = trackType;
        return this;
    }

    public int getTrackType() {
        return trackType;
    }

    public String getAppCode() {
        return appCode;
    }

    public String getChannel() {
        return channel;
    }

    public String getPlatformName() {
        return platformName;
    }

    public Configuration platformName(String platformName) {
        this.platformName = platformName;
        return this;
    }

    public String getUserType() {
        return userType;
    }

    public Configuration userType(String userType) {
        this.userType = userType;
        return this;
    }
}
