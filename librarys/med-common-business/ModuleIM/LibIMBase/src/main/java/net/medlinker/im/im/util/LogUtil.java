package net.medlinker.im.im.util;

import android.util.Log;

import net.medlinker.im.BuildConfig;


/**
 * @author <a href="mailto:yangjiantao@medlinker.net">Jiantao.Yang</a>
 * @version 3.0
 * @time 2015/10/27 11:40
 */
public final class LogUtil {

    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String format, Object... args) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, String.format(format, args));
        }
    }

    public static void i(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, String format, Object... args) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, String.format(format, args));
        }
    }

    public static void e(String tag, String format, Object... args) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, String.format(format, args));
        }
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, msg);
        }
    }

}
