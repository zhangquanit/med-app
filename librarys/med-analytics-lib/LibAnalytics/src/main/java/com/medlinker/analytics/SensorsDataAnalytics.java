package com.medlinker.analytics;

import android.content.Context;

import com.sensorsdata.analytics.android.sdk.SAConfigOptions;
import com.sensorsdata.analytics.android.sdk.SensorsAnalyticsAutoTrackEventType;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.sensorsdata.analytics.android.sdk.SensorsDataDynamicSuperProperties;

import org.json.JSONException;
import org.json.JSONObject;

public class SensorsDataAnalytics implements IAnalytics {
    private static final String MED = "medlinker_";
    @Override
    public void init(Context application, Configuration configuration) {
        String SA_SERVER_URL = "https://ssdata.xrxr.xyz/sa?project=" + (configuration.isDebug() ? "default" : "production");
        // 初始化配置
        SAConfigOptions saConfigOptions = new SAConfigOptions(SA_SERVER_URL);
        //采集页面浏览时长
        saConfigOptions.enableTrackPageLeave(true);
        //开启点击分析功能
        saConfigOptions.enableHeatMap(true);
        // 开启可视化全埋点
        saConfigOptions.enableVisualizedAutoTrack(true);
        // 开启全埋点
        saConfigOptions.setAutoTrackEventType(SensorsAnalyticsAutoTrackEventType.APP_CLICK |
                SensorsAnalyticsAutoTrackEventType.APP_START |
                SensorsAnalyticsAutoTrackEventType.APP_END |
                SensorsAnalyticsAutoTrackEventType.APP_VIEW_SCREEN)
                .enableJavaScriptBridge(false)
                //开启 Log
                .enableLog(configuration.isDebug());
        /**
         * 其他配置，如开启可视化全埋点
         */
        // 需要在主线程初始化神策 SDK
        SensorsDataAPI.startWithConfigOptions(application, saConfigOptions);

        // 初始化 SDK 之后，开启自动采集 Fragment 页面浏览事件
        SensorsDataAPI.sharedInstance().trackFragmentAppViewScreen();

        // 参考杏仁app忽略指定fragment
        Class<?> aClass = null;
        try {
            aClass = Class.forName("com.bumptech.glide.manager.SupportRequestManagerFragment");
        } catch (Exception e) {

        }
        if (aClass != null) {
            SensorsDataAPI.sharedInstance().ignoreAutoTrackFragment(aClass);
        }

        // 将应用名称作为事件公共属性，后续所有 track() 追踪的事件都会自动带上 "AppName" 属性
        try {
            JSONObject properties = new JSONObject();
            properties.put("platform_name", configuration.getPlatformName());
            properties.put("platform_type", "app");
            properties.put("user_type",configuration.getUserType());
            SensorsDataAPI.sharedInstance().registerSuperProperties(properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setUser(User user) {
        SensorsDataAPI.sharedInstance().login(user.getUserID());
    }

    @Override
    public void clearUser() {
        SensorsDataAPI.sharedInstance().logout();
    }

    @Override
    public void disable() {
        SensorsDataAPI.disableSDK();
    }

    @Override
    public void resume() {
        SensorsDataAPI.enableSDK();
    }

    @Override
    public void track(String eventName) {
        SensorsDataAPI.sharedInstance().track(MED + eventName);
    }

    @Override
    public void track(String eventName, JSONObject eventVariables) {
        SensorsDataAPI.sharedInstance().track(MED + eventName, eventVariables);
    }

    @Override
    public void set(String methodName, Object... values) {

    }

    @Override
    public void registerDynamicSuperProperties(final DynamicSuperProperties dynamicSuperProperties) {
        SensorsDataAPI.sharedInstance().registerDynamicSuperProperties(new SensorsDataDynamicSuperProperties() {
            @Override
            public JSONObject getDynamicSuperProperties() {
                return dynamicSuperProperties.getDynamicSuperProperties();
            }
        });
    }

    @Override
    public void trackAppInstall(String channel) {
        try {
            JSONObject properties = new JSONObject();
            //这里的 DownloadChannel 负责记录下载商店的渠道，值应传入具体应用商店包的标记。如果没有为不同商店打多渠道包，则可以忽略该属性的代码示例。
            properties.put("DownloadChannel", channel);
            // 触发激活事件
            // 如果您之前使用 trackInstallation() 触发的激活事件，需要继续保持原来的调用，无需改为 trackAppInstall()，否则会导致激活事件数据分离。
            SensorsDataAPI.sharedInstance().trackAppInstall(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
