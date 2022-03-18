package com.medlinker.widget.util;

import android.content.Context;

/**
 * @author zhangquan
 */
public class UiUtil {
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
