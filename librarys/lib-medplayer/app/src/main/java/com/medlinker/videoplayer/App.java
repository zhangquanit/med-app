package com.medlinker.videoplayer;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;

/**
 * @author zhangquan
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ARouter.init(this);
    }
}
