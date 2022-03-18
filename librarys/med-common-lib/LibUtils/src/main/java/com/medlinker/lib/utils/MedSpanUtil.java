package com.medlinker.lib.utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;

/**
 * SpannableString工具类
 *
 * @author hmy
 */
public class MedSpanUtil {

    /**
     *
     */
    public static void setSpannableStringByPx(TextView textView, String[] text, int[] textColor, int[] textSize, int[] styleSpan) {
        setSpannableString(textView, text, textColor, textSize, styleSpan, false);
    }

    /**
     *
     */
    public static void setSpannableStringByDip(TextView textView, String[] text, int[] textColor, int[] textSize, int[] styleSpan) {
        setSpannableString(textView, text, textColor, textSize, styleSpan, true);
    }

    public static void setSpannableString(TextView textView, String[] text, int[] textColor,
                                          int[] textSize, int[] styleSpan, boolean dip) {
        setSpannableString(textView, text, textColor, textSize, styleSpan, null, dip);
    }

    /**
     *
     */
    public static void setSpannableString(TextView textView, String[] text, int[] textColor,
                                          int[] textSize, int[] styleSpan, ClickableSpan[] clickListeners, boolean dip) {
        if (text == null || text.length == 0) {
            return;
        }
        StringBuilder str = new StringBuilder();
        int colorLength = getArrayLength(textColor);
        int sizeLength = getArrayLength(textSize);
        for (String s : text) {
            str.append(s);
        }

        SpannableString ss = new SpannableString(str.toString());
        for (int j = 0; j < colorLength; j++) {
            int start = getStrStartPosition(text, j);
            ss.setSpan(new ForegroundColorSpan(textColor[j]), start, start + text[j].length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        for (int k = 0; k < sizeLength; k++) {
            int start = getStrStartPosition(text, k);
            ss.setSpan(new AbsoluteSizeSpan(textSize[k], dip), start, start + text[k].length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            //第二个参数boolean dip，如果为true，表示前面的字体大小单位为dip，否则为像素，同上。
        }

        int styleSpanCount = 0;
        if (styleSpan != null) {
            styleSpanCount = styleSpan.length;
        }
        for (int m = 0; m < styleSpanCount; m++) {
            int start = getStrStartPosition(text, m);
            ss.setSpan(new StyleSpan(styleSpan[m]), start, start + text[m].length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        int clickSize = clickListeners == null ? 0 : clickListeners.length;
        if (clickSize > 0) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
        for (int n = 0; n < clickSize; n++) {
            int start = getStrStartPosition(text, n);
            ss.setSpan(clickListeners[n], start, start + text[n].length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        textView.setText(ss);
    }

    private static int getStrStartPosition(String[] text, int position) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < position; i++) {
            str.append(text[i]);
        }
        return str.length();
    }

    private static int getArrayLength(int[] array) {
        if (array == null) {
            return 0;
        }
        return array.length;
    }


}
