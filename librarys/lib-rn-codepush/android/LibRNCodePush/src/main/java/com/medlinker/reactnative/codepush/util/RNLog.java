package com.medlinker.reactnative.codepush.util;

import android.util.Log;

import com.medlinker.reactnative.codepush.RNCodePush;

/**
 * @author hmy
 * @time 6/1/21 10:58
 */
public class RNLog {

    private static final String TAG = "RNCodePush";

    public static void d(String tag, String msg) {
        if (RNCodePush.isDebugMode()) {
            Log.d(tag, msg);
        }
    }

    public static void d(String msg) {
        d(TAG, msg);
    }
}
