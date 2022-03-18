package com.medlinker.player.util;

import android.content.Context;
import android.os.Build;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class MedPlayerWakeLocker {
    private static WakeLock mWakeLock;

    public static void acquire(Context context) {
        try {
            if (mWakeLock == null) {
                PowerManager powerManager = (PowerManager) (context.getSystemService(Context.POWER_SERVICE));
                int level = PowerManager.SCREEN_BRIGHT_WAKE_LOCK;
                int flag = PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE;
                mWakeLock = powerManager.newWakeLock(level | flag, context.getPackageName());
            }
            if (!mWakeLock.isHeld()) {
                mWakeLock.acquire();
            }
        } catch (Exception e) {

        }
    }

    public static void release() {
        try {
            if (mWakeLock != null && mWakeLock.isHeld()) {
                mWakeLock.release();
                mWakeLock = null;
            }
        } catch (Exception e) {

        }
    }

    public static boolean isScreenOn(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return pm.isInteractive();
        }
        return pm.isScreenOn();
    }
}
