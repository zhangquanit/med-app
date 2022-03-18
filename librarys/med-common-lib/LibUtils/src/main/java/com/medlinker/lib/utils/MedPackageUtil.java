package com.medlinker.lib.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Process;
import android.text.TextUtils;


import java.util.List;

public final class MedPackageUtil {

    /**
     * 是否运行在前台
     *
     * @param context
     * @return
     */
    public boolean isRunningForeground(Context context) {
        String packageName = context.getPackageName();
        String topActivityClassName = getTopActivityName(context);
        return packageName != null && topActivityClassName != null && topActivityClassName.startsWith(packageName);
    }

    /*
     * 当前栈顶的activityname
     * */
    public static String getTopActivityName(Context context) {
        String topActivityClassName = null;
        ActivityManager activityManager = (ActivityManager) (context.getSystemService(android.content.Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
        if (runningTaskInfos != null) {
            ComponentName f = runningTaskInfos.get(0).topActivity;
            topActivityClassName = f.getClassName();
        }
        return topActivityClassName;
    }



    public static int getVersionCode() {
        return MedAppInfo.getVersionCode();
    }

    public static String getVersionName() {
        return MedAppInfo.getVersionName();
    }

    public static String getPackageName() {
        return MedAppInfo.getApplicationId();
    }

    /**
     * get currnet activity's name
     *
     * @param context
     * @return
     */
    public static String getActivityName(Context context) {
        if (context == null) {
            return "";
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (checkPermissions(context, "android.permission.GET_TASKS")) {
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            String activityName = cn.getShortClassName();
            activityName = activityName.substring(activityName.lastIndexOf(".") + 1, activityName.length());
            return activityName;
        } else {
            return "";
        }
    }

    /**
     * checkPermissions
     *
     * @param context
     * @param permission
     * @return true or false
     */
    public static boolean checkPermissions(Context context, String permission) {
        PackageManager localPackageManager = context.getPackageManager();
        return localPackageManager.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
    }

    private static String channelId;


    //因为推送服务XMPushService在AndroidManifest.xml中设置为运行在另外一个进程，这导致本Application会被实例化两次，所以我们需要让应用的主进程初始化。
    public static boolean isMainProcess(Context context) {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        if (null != processInfos) {
            String mainProcessName = context.getPackageName();
            int myPid = Process.myPid();
            for (ActivityManager.RunningAppProcessInfo info : processInfos) {
                if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
