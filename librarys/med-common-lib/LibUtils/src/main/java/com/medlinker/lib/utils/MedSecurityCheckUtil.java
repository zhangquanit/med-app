package com.medlinker.lib.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;


import java.io.File;

/**
 * @author hmy
 * @time 2020-04-08 15:37
 */
public class MedSecurityCheckUtil {

    private static class SingletonHolder {
        private static final MedSecurityCheckUtil singleInstance = new MedSecurityCheckUtil();
    }

    private MedSecurityCheckUtil() {
    }

    public static MedSecurityCheckUtil getInstance() {
        return SingletonHolder.singleInstance;
    }


    @SuppressLint("StaticFieldLeak")
    public void checkRoot(final Context context, final String tipMsg) {
       new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void[] objects) {
                try {
                    boolean rooted = isRoot();
                    if (rooted) {
                        if ((!TextUtils.isEmpty(tipMsg))) {
                            MedToastUtil.showMessageLong(tipMsg);
                        }
                        Thread.sleep(2000);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(0);
                    }


                } catch (Throwable throwable) {

                }

                return null;
            }
        }.execute();
    }


    /**
     * 检查root权限
     *
     * @return
     */
    public boolean isRoot() {
        int secureProp = getroSecureProp();
        if (secureProp == 0)//eng/userdebug版本，自带root权限
            return true;
        else return isSUExist();//user版本，继续查su文件
    }

    private int getroSecureProp() {
        int secureProp;
        String roSecureObj = getProperty("ro.secure");
        if (roSecureObj == null) secureProp = 1;
        else {
            if ("0".equals(roSecureObj)) secureProp = 0;
            else secureProp = 1;
        }
        return secureProp;
    }


    private boolean isSUExist() {
        File file;
        String[] paths = {"/sbin/su",
                "/system/bin/su",
                "/system/xbin/su",
                "/data/local/xbin/su",
                "/data/local/bin/su",
                "/system/sd/xbin/su",
                "/system/bin/failsafe/su",
                "/data/local/su"};
        for (String path : paths) {
            file = new File(path);
            if (file.exists()) return true;
        }
        return false;
    }

    private String getProperty(String propName) {
        String value = null;
        Object roSecureObj;
        try {
            roSecureObj = Class.forName("android.os.SystemProperties")
                    .getMethod("get", String.class)
                    .invoke(null, propName);
            if (roSecureObj != null) value = (String) roSecureObj;
        } catch (Exception e) {
            value = null;
        } finally {
            return value;
        }
    }
}
