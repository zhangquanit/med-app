package net.medlinker.base.common;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import net.medlinker.base.manager.ActivityStashManager;


/**
 * Created by jess on 21/02/2017 14:23
 * Contact with jess.yan.effort@gmail.com
 */
public class ActivityLifecycle implements Application.ActivityLifecycleCallbacks {

    private int mActivityCount = 0;
    private static ActivityLifecycle mCallbacks;

    public static ActivityLifecycle getInstance() {
        if (mCallbacks == null) {
            mCallbacks = new ActivityLifecycle();
        }
        return mCallbacks;
    }

    public static ActivityLifecycle setInstance(ActivityLifecycle callbacks) {
        mCallbacks = callbacks;
        return callbacks;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        ActivityStashManager.onActivityCreated(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        mActivityCount++;
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
        mActivityCount--;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        ActivityStashManager.onActivityDestroied(activity);
    }

    public int getActivityCount() {
        return mActivityCount;
    }

}
