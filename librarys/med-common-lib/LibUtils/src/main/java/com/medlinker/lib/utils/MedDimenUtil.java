package com.medlinker.lib.utils;

import android.content.Context;
import android.content.res.Resources;


public class MedDimenUtil {

    public static int sp2px(Context context, float spValue) {
        float fontScale = getResource(context).getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int px2sp(Context context, int pxValue) {
        float fontScale = getResource(context).getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int dip2px(Context context, double dipValue) {
        final float scale = getResource(context).getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static double dip2pxF(Context context, double dipValue) {
        final float scale = getResource(context).getDisplayMetrics().density;
        return (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, int pxValue) {
        final float scale = getResource(context).getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private static Resources getResource(Context context) {
        return context.getResources();
    }
}
