package com.doctorwork.android.logreport.http;

/**
 * @author : HuBoChao
 * @date : 2020/11/30
 * @desc : String类型的切入接口
 */
public interface IStringListener {
    /**
     * 请求成功
     *
     * @param s
     */
    void onSuccess(String s);

    /**
     * 失败
     */
    void onFail();
}
