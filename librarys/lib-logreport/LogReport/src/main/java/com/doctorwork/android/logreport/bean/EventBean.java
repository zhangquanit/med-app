package com.doctorwork.android.logreport.bean;

import androidx.annotation.StringDef;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class EventBean {
    /**
     * 事件类型
     * "cold-start" 冷启动
     * "hot-start" 热启动
     * "http-perf" http请求时间
     */
    private String event;
    /**
     * 页面
     */
    private String page;
    /**
     * 请求的url
     */
    private String reqUrl;
    /**
     * 请求参数
     */
    private String reqParam;
    /**
     * 请求方式
     */
    private String reqMethod;
    /**
     * 响应码
     */
    private int resCode;
    /**
     * 响应大小
     */
    private String resSize;
    /**
     * http响应头返回的服务端traceId
     */
    private String resTid;
    /**
     * 耗时
     */
    private int duration;
    /**
     * 发生时间
     */
    private long createTime;

    /**
     * 性能上报类型
     */
    public static final String COLD_START = "cold-start";
    public static final String HOT_START = "hot-start";
    public static final String HTTP_PERF = "http-perf";
    public static final String CUSTOM_EVENT = "custom-event";

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.PARAMETER)
    @StringDef(value = {COLD_START, HOT_START, HTTP_PERF, CUSTOM_EVENT})
    private @interface EventType {
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(@EventType String event) {
        this.event = event;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getReqUrl() {
        return reqUrl;
    }

    public void setReqUrl(String reqUrl) {
        this.reqUrl = reqUrl;
    }

    public String getReqParam() {
        return reqParam;
    }

    public void setReqParam(String reqParam) {
        this.reqParam = reqParam;
    }

    public String getReqMethod() {
        return reqMethod;
    }

    public void setReqMethod(String reqMethod) {
        this.reqMethod = reqMethod;
    }

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    public String getResSize() {
        return resSize;
    }

    public void setResSize(String resSize) {
        this.resSize = resSize;
    }

    public String getResTid() {
        return resTid;
    }

    public void setResTid(String resTid) {
        this.resTid = resTid;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
