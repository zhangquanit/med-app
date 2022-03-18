package com.doctorwork.android.logreport.bean;

import androidx.annotation.Keep;

import java.util.List;

/**
 * @author : HuBoChao
 * @date : 2020/12/2
 * @desc : 完整结构
 */
@Keep
public class BaseBean {
    private String appId;
    private String uuid;
    /**
     * 渠道
     */
    private String channel;
    /**
     * 手机号
     */
    private String p;
    /**
     * 机型
     */
    private String model;
    /**
     * 品牌
     */
    private String brand;
    /**
     * 系统
     */
    private String system;
    /**
     * 系统版本
     */
    private String systemV;
    /**
     * App版本
     */
    private String appV;
    /**
     * 启动时间，每次冷启动更新
     */
    private long appStartTime;
    private List<ErrorBean> errors;
    private List<CustomBean> customs;
    private List<EventBean> events;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getSystemV() {
        return systemV;
    }

    public void setSystemV(String systemV) {
        this.systemV = systemV;
    }

    public String getAppV() {
        return appV;
    }

    public void setAppV(String appV) {
        this.appV = appV;
    }

    public long getAppStartTime() {
        return appStartTime;
    }

    public void setAppStartTime(long appStartTime) {
        this.appStartTime = appStartTime;
    }

    public List<ErrorBean> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorBean> errors) {
        this.errors = errors;
    }

    public List<CustomBean> getCustoms() {
        return customs;
    }

    public void setCustoms(List<CustomBean> customs) {
        this.customs = customs;
    }

    public List<EventBean> getEvents() {
        return events;
    }

    public void setEvents(List<EventBean> events) {
        this.events = events;
    }
}
