package com.doctorwork.android.logreport.http;

/**
 * @author : HuBoChao
 * @date : 2020/11/30
 * @desc : 请求封装类的切入接口
 */
public interface IHttpBean {

    /**
     * 设置url
     *
     * @param url
     */
    void setUrl(String url);

    /**
     * 设置参数
     *
     * @param data
     */
    void setData(byte[] data);

    /**
     * 设置请求方式
     *
     * @param method
     */
    void setMethod(String method);

    /**
     * 设置回调
     *
     * @param callBackListener
     */
    void setListener(CallBackListener callBackListener);
    CallBackListener getListener();

    /**
     * 执行请求
     */
    void execute();
}
