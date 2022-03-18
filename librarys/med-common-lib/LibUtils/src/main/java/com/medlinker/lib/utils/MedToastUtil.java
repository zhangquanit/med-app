package com.medlinker.lib.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;


/**
 * Author: KindyFung.
 * CreateTime:  2015/10/27 18:31
 * Email：fangjing@medlinker.com.
 * Description: Toast工具类
 */
public class MedToastUtil {
    public static boolean isReadToastShow = false;

    private static Handler handler = new Handler(Looper.getMainLooper());
    private static Toast toast = null;

    /**
     * Toast发送消息，默认Toast.LENGTH_SHORT
     */
    public static void showMessage(final String msg) {
        showMessage(null, msg, Toast.LENGTH_SHORT);
    }

    /**
     * Toast发送消息，默认Toast.LENGTH_SHORT
     */
    public static void showMessage(final Context act, final String msg) {
        showMessage(act, msg, Toast.LENGTH_SHORT);
    }

    /**
     * Toast发送消息，默认Toast.LENGTH_LONG
     */
    public static void showMessageLong(final String msg) {
        showMessage(null, msg, Toast.LENGTH_LONG);
    }

    /**
     * Toast发送消息，默认Toast.LENGTH_LONG
     */
    public static void showMessageLong(final Context act, final String msg) {
        showMessage(act, msg, Toast.LENGTH_LONG);
    }

    /**
     * Toast发送消息，默认Toast.LENGTH_SHORT
     */
    public static void showMessage(final Context act, final int msg) {
        showMessage(act, msg, Toast.LENGTH_SHORT);
    }

    /**
     * Toast发送消息，默认Toast.LENGTH_LONG
     */
    public static void showMessageLong(final Context act, final int msg) {
        showMessage(act, msg, Toast.LENGTH_LONG);
    }

    /**
     * Toast发送消息
     */
    public static void showMessage(final Context act, final int msg,
                                   final int len) {
        try {
            showMessage(act, act.getResources().getString(msg), len);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Toast发送消息
     */
    public static void showMessage(final Context act, final String msg,
                                   final int len) {
        handler.post(new Runnable() {

            @Override
            public void run() {
                if (toast != null) {
                    toast.setText(msg);
                    toast.setDuration(len);
                } else {
                    toast = Toast.makeText(act == null ? MedAppInfo.appContext : act.getApplicationContext(), msg, len);
                }
                toast.show();
            }
        });
    }
}

