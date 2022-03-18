package com.medlinker.base.startup;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 组件SDK初始化
 * <p>1、请在manifest.xml中注册 确保android:value="app.startup"</p>
 * <pre class="prettyprint">
 *  <meta-data
 *    android:name="包名.类名"
 *    android:value="app.startup" />
 * </pre>
 * <p>2、如果想关闭某个app 添加tools:node="remove" 比如：</p>
 * <pre class="prettyprint">
 *    <meta-data
 *       android:name="net.medlinker.android.MainApp"
 *       tools:node="remove"
 *       android:value="app.startup" />
 * </pre>
 *
 * @author zhangquan
 */
public final class AppInitializer {
    private Application mContext;
    private static AppInitializer mInstance;
    private Set<String> mAppClasses = new HashSet<>();
    private List<ApplicationLike> mApps = new ArrayList<>();

    private AppInitializer(Application ctx) {
        mContext = ctx;
    }

    public static AppInitializer getInstance(Application ctx) {
        if (mInstance == null) {
            synchronized (AppInitializer.class) {
                if (mInstance == null) {
                    mInstance = new AppInitializer(ctx);
                    mInstance.initializeComponents(ctx);
                }
            }
        }
        return mInstance;
    }

    private void initializeComponents(Application ctx) {
        PackageManager pm = ctx.getPackageManager();
        String packageName = ctx.getPackageName();
        try {
            ApplicationInfo ai = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            Bundle metaData = ai.metaData;
            Set<String> keySet = metaData.keySet();

            String startup = mContext.getString(R.string.med_app_startup);
            final List<ApplicationLike> appList = new ArrayList<>();
            for (String key : keySet) {
                Object value = metaData.get(key);
                if (null == value) continue;
                if (value instanceof String && startup.equals(value)) {
                    try {
                        Class<?> clazz = Class.forName(key);
                        if (ApplicationLike.class.isAssignableFrom(clazz)) {
                            Class<? extends ApplicationLike> aClass = (Class<? extends ApplicationLike>) clazz;
                            if (!mAppClasses.contains(aClass.getName())) {
                                mAppClasses.add(aClass.getName());
//                            mApps.add(aClass.newInstance());
                                appList.add(aClass.newInstance());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
            addAppByPriority(appList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addAppByPriority(ApplicationLike app) {
        mApps.add(app);
        addAppByPriority(mApps);
    }

    private void addAppByPriority(List<ApplicationLike> appList) {
        Map<Integer, List<ApplicationLike>> map = new TreeMap<>();
        for (ApplicationLike app : appList) {
            int priority = app.getPriority();
            if (!map.containsKey(priority)) {
                map.put(priority, new ArrayList());
            }
            map.get(priority).add(app);
        }
        mApps.clear();
        ListIterator<Map.Entry<Integer, List<ApplicationLike>>> iterator = new ArrayList(map.entrySet()).listIterator(map.size());
        while (iterator.hasPrevious()) {
            Map.Entry<Integer, List<ApplicationLike>> entry = iterator.previous();
            List<ApplicationLike> list = entry.getValue();
            if (!list.isEmpty()) {
                mApps.addAll(list);
            }
        }
    }

    public void onCreate() {
        for (ApplicationLike app : mApps) {
            app.onCreate(mContext);
        }
    }

    public void initSensitiveSDK() {
        for (ApplicationLike app : mApps) {
            app.initSensitiveSDK(mContext);
        }
    }

    public void initSensitiveSDKOnMainProcess() {
        for (ApplicationLike app : mApps) {
            app.initSensitiveSDKOnMainProcess(mContext);
        }
    }

    public void initAsync() {
        for (ApplicationLike app : mApps) {
            app.initAsync(mContext);
        }
    }

    public void addApp(ApplicationLike app) {
        String appName = app.getClass().getName();
        if (!mAppClasses.contains(appName)) {
            mAppClasses.add(appName);
//            mApps.add(app);
            addAppByPriority(app);
        }
    }

    public void addApp(Class<? extends ApplicationLike> aClass) {
        try {
            addApp(aClass.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeApp(ApplicationLike app) {
        String appName = app.getClass().getName();
        if (mAppClasses.contains(appName)) {
            mAppClasses.remove(appName);
            mApps.remove(app);
        }
    }

    public void removeApp(Class<? extends ApplicationLike> aClass) {
        try {
            removeApp(aClass.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T extends ApplicationLike> T getApp(Class<T> aClass) {
        String appName = aClass.getName();
        for (ApplicationLike app : mApps) {
            if (TextUtils.equals(appName, app.getClass().getName())) {
                return (T) app;
            }
        }
        return null;
    }

    public List<ApplicationLike> getApps() {
        return mApps;
    }
}
