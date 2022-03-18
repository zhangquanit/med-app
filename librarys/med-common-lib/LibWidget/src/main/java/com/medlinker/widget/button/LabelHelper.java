package com.medlinker.widget.button;

import android.content.Context;
import android.os.Build;


/**
 * 标签样式统一管理   CommonLabel->RoundCornerTextView的Helper
 *
 * @author gengyunxiao
 * @time 2021/3/15
 */
public class LabelHelper {

    /**
     * 获取带Style的Button按钮
     *
     * @param context
     * @param buttonStyleResId style_label中的自定义Style样式
     * @return
     */
    public static CommonLabel getStyleLabel(Context context, int buttonStyleResId) {
        CommonLabel commonButton = new CommonLabel(context);
        dealStyleLabel(context, commonButton, buttonStyleResId);
        return commonButton;
    }

    /**
     * 获取带Style的Button按钮
     *
     * @param context
     * @param buttonStyleResId style_label中的自定义Style样式
     * @return
     */
    public static void dealStyleLabel(Context context, CommonLabel label, int buttonStyleResId) {
        CommonLabel commonButton = label;
        if (commonButton == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            commonButton.setTextAppearance(buttonStyleResId);
        } else {
            commonButton.setTextAppearance(context, buttonStyleResId);
        }
    }
}
