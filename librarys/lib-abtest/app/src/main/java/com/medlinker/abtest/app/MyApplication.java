package com.medlinker.abtest.app;

import android.app.Application;

import com.medlinker.abtest.ABTest;
import com.medlinker.analytics.BuildConfig;
import com.medlinker.analytics.MedAnalytics;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MedAnalytics.init(this, new com.medlinker.analytics.Configuration()
                .debug(BuildConfig.DEBUG)
                .channel("oppo应用商店")
                .trackType(MedAnalytics.TRACK_BI));
        ABTest.init(this,"564449826209533966",false);
        //ABTest.setVerifyCode("78457499__mcwY3vXVI5U0EqHQkOqnmGAdXksMf6a7Ez9oeSeg9yj0Tiuryr4VGlWZs4Dbaz1P");
    }
}