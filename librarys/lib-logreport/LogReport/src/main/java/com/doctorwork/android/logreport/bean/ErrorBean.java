package com.doctorwork.android.logreport.bean;

/**
 * @author : HuBoChao
 * @date : 2020/12/2
 * @desc : 错误类
 */
public class ErrorBean<T, M> {
    /**
     * 错误类型
     * "net"=非200状态码的网络请求
     * "business"=请求成功但业务失败
     * "img"=图片加载失败
     * "custom"=自定义错误
     */
    private String type;
    /**
     * 发生时间
     */
    private long createTime;
    /**
     * 错误发生页面
     */
    private String page;
    /**
     * 请求的url
     */
    private String url;
    /**
     * 请求方式
     */
    private String method;
    /**
     * 请求参数
     */
    private T params;
    /**
     * 响应码
     */
    private String code;
    /**
     * 错误描述
     */
    private M msg;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public T getParams() {
        return params;
    }

    public void setParams(T params) {
        this.params = params;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public M getMsg() {
        return msg;
    }

    public void setMsg(M msg) {
        this.msg = msg;
    }
}
