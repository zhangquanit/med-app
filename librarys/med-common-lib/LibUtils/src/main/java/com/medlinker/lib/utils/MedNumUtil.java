package com.medlinker.lib.utils;
/*
 * Created by kuiwen on 2015/10/21.
 */

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @author <a href="mailto:zhangkuiwen@medlinker.net">Kuiwen.Zhang</a>
 * @version 1.0
 * @description 功能描述
 * @time 2015/10/21 14:25
 **/
public class MedNumUtil {

    public static String toNumString(int value) {
        String ret;
        if (value >= 10000) {
            ret = MedStringUtil.subZeroAndDot((value / 1000) / 10f) + "w";
        } else if (value >= 1000) {
            ret = MedStringUtil.subZeroAndDot((value / 100) / 10f) + "k";
        } else {
            ret = String.valueOf(value < 0 ? 0 : value);
        }
        return ret;
    }

    public static String toNumWString(int value) {
        String ret = "";
        if (value >= 10000) {
            ret = (value / 1000) / 10f + "w";
        } else {
            ret = String.valueOf(value < 0 ? 0 : value);
        }
        return ret;
    }

    public static String toNumWString1(int value) {
        String ret;
        if (value > 99) {
            ret = 99 + "+";
        } else {
            ret = String.valueOf(value < 0 ? 0 : value);
        }
        return ret;
    }

    /**
     * 将距离转换成
     *
     * @param value
     * @return
     */
    public static String toDistanceString(double value) {
        int x = (int) value * 10;
        String ret = "";
        if (value >= 1000) {
            ret = (x / 1000) / 10f + "km";
        } else {
            ret = String.valueOf(value < 0 ? 0 : Math.round(value)) + "m";
        }
        return ret;
    }



    /**
     * 去掉小数位无用的零
     *
     * @param str
     * @return
     */
    public static String handleStr(String str) {
        if (str == null) {
            return "";
        }
        if (str.indexOf(".") > 0) {
            //正则表达
            str = str.replaceAll("0+?$", "");//去掉后面无用的零
            str = str.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
        }
        return str;
    }
    


    /**
     * 第一种方法 java.text.DecimalFormat
     */

    public static String getNum2(double num) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(num);
    }
}
