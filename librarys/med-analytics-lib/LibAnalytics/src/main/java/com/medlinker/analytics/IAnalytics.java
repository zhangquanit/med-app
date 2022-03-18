package com.medlinker.analytics;


import android.content.Context;

import org.json.JSONObject;


public interface IAnalytics {

    /**
     * 初始化埋点 SDK，在整个应用程序全局只需要调用一次。
     * <p>
     * 只能在主进程调用，建议在 Application 继承类中调用。
     * </p>
     *
     * @param application   Application 类对象。
     * @param configuration 配置参数
     */
    void init(Context application, Configuration configuration);


    /**
     * 设置用户信息。
     *
     * @param user 用户信息必须是全量，不能更新部分信息。
     */
    void setUser(User user);

    /**
     * 清除用户信息
     */
    void clearUser();

    /**
     * 停止采集
     */
    void disable();

    /**
     * 恢复采集
     */
    void resume();


    /**
     * 发送埋点数据
     *
     * @param eventName GrowingIO定义的事件名称，比如"loginSuccess"
     */
    void track(String eventName);

    /**
     * 发送埋点数据
     *
     * @param eventName      GrowingIO定义的事件名称，比如"loginSuccess"
     * @param eventVariables 埋点数据。
     */
    void track(String eventName, JSONObject eventVariables);


    /**
     * 适配一些第三方的接口。
     *
     * @param methodName 第三方接口的方法名。
     * @param values     接口需要的参数，必须与需要调用的第三方接口参数保持一致。
     *                   <p>
     *                   如set("setActivityPageName",activity,"主页")
     */
    void set(String methodName, Object... values);

    /**
     * 设置动态公共属性实现接口
     *
     * @param dynamicSuperProperties 动态属性接口
     */
    void registerDynamicSuperProperties(DynamicSuperProperties dynamicSuperProperties);

    /**
     * 记录激活事件
     *
     * @param channel 应用下载渠道
     */
    void trackAppInstall(String channel);
}
