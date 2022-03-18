package com.medlinker.hybridapp.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;


/**
 * Created by jess on 21/02/2017 14:23
 * Contact with jess.yan.effort@gmail.com
 */
public class ActivityLifecycle implements Application.ActivityLifecycleCallbacks {

    private int activityCount = 0;
    private OnActivityLifecycleListener mOnActivityLifecycleListener;

    private static class SingletonHolder {
        private static final ActivityLifecycle INSTANCE = new ActivityLifecycle();
    }

    private ActivityLifecycle() {
    }

    public static ActivityLifecycle getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        ActivityStashManager.onActivityCreated(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        activityCount++;
        if (mOnActivityLifecycleListener != null) {
            mOnActivityLifecycleListener.onActivityStarted(activityCount);
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        ActivityStashManager.setCurrentActivity(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (ActivityStashManager.getCurrentActivity() == activity) {
            ActivityStashManager.setCurrentActivity(null);
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        activityCount--;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        ActivityStashManager.onActivityDestroied(activity);
    }

    public int getActivityCount() {
        return activityCount;
    }

    public ActivityLifecycle setOnActivityLifecycleListener(OnActivityLifecycleListener lifecycleListener) {
        mOnActivityLifecycleListener = lifecycleListener;
        return this;
    }

    public interface OnActivityLifecycleListener {
        void onActivityStarted(int activityCount);
    }
}
