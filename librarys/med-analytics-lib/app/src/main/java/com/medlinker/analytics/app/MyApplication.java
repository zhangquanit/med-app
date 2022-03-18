package com.medlinker.analytics.app;

import android.app.Application;

import com.medlinker.analytics.MedAnalytics;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MedAnalytics.init(this, new com.medlinker.analytics.Configuration()
                .debug(BuildConfig.DEBUG)
                .channel("oppo应用商店")
                .platformName("医联app")
                .trackType(MedAnalytics.TRACK_SENSORS_DATA));
    }
}