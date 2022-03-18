package com.medlinker.lib.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.blankj.utilcode.util.Utils;


/**
 * @author <a href="mailto:ganyu@medlinker.net">ganyu</a>
 * @version 3.2
 * @description 本地资源工具类
 * @time 2016/4/22 14:37
 */
public class MedResourceUtils {

    private MedResourceUtils() {
    }

    public static int getColor(int colorId) {
        return getColor(Utils.getApp(), colorId);
    }

    public static String getString(int stringId) {
        return getString(Utils.getApp(), stringId);
    }

    public static String getString(int stringId, Object... formatArgs) {
        return getString(Utils.getApp(), stringId, formatArgs);
    }

    public static int getColor(Context context, int colorId) {
        return context.getResources().getColor(colorId);
    }

    public static String getString(Context context, int stringId) {
        return context.getResources().getString(stringId);
    }

    public static String getString(Context context, int stringId, Object... formatArgs) {
        return context.getResources().getString(stringId, formatArgs);
    }

    public static Drawable getDrawable(int drawableId) {
        return Utils.getApp().getResources().getDrawable(drawableId);
    }
}
