package com.cn.glidelib.util;

import android.content.Context;
import android.text.TextUtils;

import java.lang.reflect.Method;

/**
 * library 加载资源文件
 * Created by jiantao on 2017/5/4.
 */

public class R2 {

    private static Object sResourceProvider;

    public static void setResourceProvider(Object rp) {
        try {
            Method t = rp.getClass().getMethod("getResId", Context.class, String.class, String.class);
            if(t != null) {
                sResourceProvider = rp;
            }
        } catch (Throwable var2) {
            var2.printStackTrace();
        }

    }

    public static int getResId(Context context, String resType, String resName) {
        int resId = 0;
        if(context != null && !TextUtils.isEmpty(resType) && !TextUtils.isEmpty(resName)) {
            if(sResourceProvider != null) {
                try {
                    Method pck = sResourceProvider.getClass().getMethod("getResId", Context.class, String.class, String.class);
                    pck.setAccessible(true);
                    resId = ((Integer)pck.invoke(sResourceProvider, context, resType, resName)).intValue();
                } catch (Throwable var5) {
                    var5.printStackTrace();
                }
            }

            if(resId <= 0) {
                String pck1 = context.getPackageName();
                if(TextUtils.isEmpty(pck1)) {
                    return resId;
                }

                if(resId <= 0) {
                    resId = context.getResources().getIdentifier(resName, resType, pck1);
                    if(resId <= 0) {
                        resId = context.getResources().getIdentifier(resName.toLowerCase(), resType, pck1);
                    }
                }

                if(resId <= 0) {
                    System.err.println("failed to parse " + resType + " resource \"" + resName + "\"");
                }
            }

            return resId;
        } else {
            return resId;
        }
    }

    public static int getDimenRes(Context context, String resName) {
        return getResId(context, "dimen", resName);
    }

    public static int getDrawableRes(Context context, String resName) {
        return getResId(context, "drawable", resName);
    }

    public static int getMipmapRes(Context context, String resName) {
        return getResId(context, "mipmap", resName);
    }

    public static int getStringRes(Context context, String resName) {
        return getResId(context, "string", resName);
    }

    public static int getStringArrayRes(Context context, String resName) {
        return getResId(context, "array", resName);
    }

    public static int getLayoutRes(Context context, String resName) {
        return getResId(context, "layout", resName);
    }

    public static int getStyleRes(Context context, String resName) {
        return getResId(context, "style", resName);
    }

    public static int getIdRes(Context context, String resName) {
        return getResId(context, "id", resName);
    }

    public static int getColorRes(Context context, String resName) {
        return getResId(context, "color", resName);
    }

    public static int getRawRes(Context context, String resName) {
        return getResId(context, "raw", resName);
    }

    public static int getPluralsRes(Context context, String resName) {
        return getResId(context, "plurals", resName);
    }

    public static int getAnimRes(Context context, String resName) {
        return getResId(context, "anim", resName);
    }
}
