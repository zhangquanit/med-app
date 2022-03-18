package com.medlinker.base.utils;

public class AppUtil {
    private static boolean sIsLiteApp = false;

    private AppUtil() {
        //do nothing
    }

    public static void setIsLiteApp(boolean isLiteApp) {
        sIsLiteApp = isLiteApp;
    }

    public static boolean isLiteApp() {
        return sIsLiteApp;
    }
}
