package com.medlinker.widget.button;

import android.content.Context;
import android.os.Build;


/**
 * 按钮样式统一管理   CommonButton->RoundCornerTextView的Helper
 *
 * @author gengyunxiao
 * @time 2021/3/15
 */
public class ButtonHelper {

    /**
     * 获取带Style的Button按钮
     *
     * @param context
     * @param buttonStyleResId style_button中的自定义Style样式
     * @return
     */
    public static CommonButton getStyleButton(Context context, int buttonStyleResId) {
        CommonButton commonButton = new CommonButton(context);
        dealStyleButton(context, commonButton, buttonStyleResId);
        return commonButton;
    }

    /**
     * 获取带Style的Button按钮
     *
     * @param context
     * @param buttonStyleResId style_button中的自定义Style样式
     * @return
     */
    public static void dealStyleButton(Context context, CommonButton button, int buttonStyleResId) {
        CommonButton commonButton = button;
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
