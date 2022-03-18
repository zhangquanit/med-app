package com.medlinker.analytics;


import android.content.Context;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MedAnalytics {

    //public static final int TRACK_GROWING_IO = 0x01;
    public static final int TRACK_BI = 0x02;
    //public static final int TRACK_AB_TEST = 0x04;
    public static final int TRACK_SENSORS_DATA= 0x08;
    public static final int ALL =  TRACK_BI |TRACK_SENSORS_DATA;

    static Configuration configuration;
    static List<IAnalytics> medAnalytics = new ArrayList<>();

    /**
     * 初始化埋点 SDK，在整个应用程序全局只需要调用一次。
     * <p>
     * 只能在主进程调用，建议在 Application 继承类中调用。
     * </p>
     *
     * @param application   Application 类对象。
     * @param config 配置参数
     */
    public static void init(Context application, Configuration config) {
        configuration = config;
        initSDK(application);
    }


    private static void  initSDK(Context application){
        medAnalytics.clear();
        if ((configuration.getTrackType() & TRACK_BI) != 0) {
            medAnalytics.add(new BIAnalytics());
        }
        if ((configuration.getTrackType() & TRACK_SENSORS_DATA) != 0) {
            medAnalytics.add(new SensorsDataAnalytics());
        }
        for (IAnalytics medAnalytics : medAnalytics) {
            medAnalytics.init(application, configuration);
        }
    }

    /**
     * 发送埋点数据
     *
     * @param eventName GrowingIO定义的事件名称，比如"loginSuccess"
     */
    public static void track(String eventName) {
        for (IAnalytics medAnalytics : medAnalytics) {
            medAnalytics.track(eventName);
        }
    }

    /**
     * 发送埋点数据
     *
     * @param eventName      GrowingIO定义的事件名称，比如"loginSuccess"
     * @param eventVariables 埋点数据。
     */
    public static void track(String eventName, JSONObject eventVariables) {
        for (IAnalytics medAnalytics : medAnalytics) {
            medAnalytics.track(eventName, eventVariables);
        }
    }

    /**
     * 设置用户信息。
     *
     * @param user 用户信息必须是全量，不能更新部分信息。
     */
    public static void setUser(User user) {
        for (IAnalytics medAnalytics : medAnalytics) {
            medAnalytics.setUser(user);
        }
    }

    /**
     * 清除用户信息。
     */
    public static void clearUser() {
        for (IAnalytics medAnalytics : medAnalytics) {
            medAnalytics.clearUser();
        }
    }

    /**
     * 停止采集。
     */
    public static void disable() {
        for (IAnalytics medAnalytics : medAnalytics) {
            medAnalytics.disable();
        }
    }

    /**
     * 恢复采集
     */
    public static void resume() {
        for (IAnalytics medAnalytics : medAnalytics) {
            medAnalytics.resume();
        }
    }

    /**
     * 适配一些第三方的接口。
     *
     * @param methodName 第三方接口的方法名。
     * @param values     接口需要的参数，必须与需要调用的第三方接口参数保持一致。
     * 如set("setActivityPageName",activity,"主页")
     */
    public static void set(String methodName, Object... values) {
        for (IAnalytics medAnalytics : medAnalytics) {
            medAnalytics.set(methodName, values);
        }
    }

    /**
     * 设置动态公共属性实现接口
     *
     * @param dynamicSuperProperties 动态属性接口
     */
    public static void  registerDynamicSuperProperties(DynamicSuperProperties dynamicSuperProperties){
        for (IAnalytics medAnalytics : medAnalytics) {
            medAnalytics.registerDynamicSuperProperties(dynamicSuperProperties);
        }
    }

    /**
     * 记录激活事件
     *
     */
    public static void trackAppInstall() {
        for (IAnalytics medAnalytics : medAnalytics) {
            medAnalytics.trackAppInstall(configuration.getChannel());
        }
    }
}
