package com.medlinker.widget.dialog.constant;

import android.graphics.Color;
import android.graphics.Typeface;

/**
 * 对话框常量
 *
 * @author gengyunxiao
 * @time 2021/4/26
 */
public class MLDialogConstant {

    /*-----------Dialog------------------*/
    public static final int DIALOG_SHADOW_BG_COLOR = Color.parseColor("#80000000");
    public static final float DIALOG_SHADOW_BG_ALPHA = 0.5f;

    /*-----------WheelView------------------*/
    //字体行space
    public static final float WHEEL_LINE_SPACING_MULTIPLIER = 3.0f;
    //字体大小，单位sp
    public static final int WHEEL_TEXT_SIZE = 16;
    //滑轮可见的item个数
    public static final int WHEEL_ITEMS_VISIBLE_COUNT = 5;
    //字体
    public static final Typeface WHEEL_TYPEFACE_FONT = Typeface.DEFAULT;
    //透明度渐变
    public static final boolean WHEEL_IS_ALPHA_GRADIENT = true;
}
