package com.medlinker.lib.utils;

/**
 * Email：luhaiyang@medlinker.com
 * TAG：【Util】
 * Description:正则表达式验证
 */
public class MedRegexUtil {

    /**
     * 医联号是否合法（只允许字母和数字的组合6-20位）
     *
     * @return 是否合法
     */
    public static boolean isMedNumberValid(String medNumber) {
        if (medNumber.length() < 6 || medNumber.length() > 20) {
            return false;
        }
        String regex = "^[a-z0-9A-Z]+$";
        return medNumber.matches(regex);
    }

    /**
     * 手机号是否规范（1开头的11位数字）
     *
     * @return 是否合法
     */
    public static boolean isPhoneNumberValid(String medNumber) {
        if (medNumber.length() != 11) {
            return false;
        }
        String regex = "^[1][0-9]+$";
        return medNumber.matches(regex);
    }

    /**
     * 身份证是否符合要求(18位数字，但最后一位可以是字母）
     *
     * @param number
     * @return
     */
    public static boolean isIdentifyCardValid(String number) {
        String regex = "^[1-9]\\d{5}"//1-9开始，接5位数字
                + "(19|21)\\d{2}"//1900-2199
                + "((0[1-9])|(10|11|12))"//月份
                + "(([0-2][1-9])|10|20|30|12)"//天数，包括2月29日
                + "\\d{3}[0-9Xx]";//最后4位，其中最后一位允许为Xx
        return number.matches(regex);
    }

    /**
     * 判断是否是时间格式  如07：00
     * @param time
     * @return
     */
    public static boolean isTimeValid(String time) {
        String regex = "^(([01]\\d)|(2[0-3])):([0-5]\\d)$";
        return time.matches(regex);
    }
}
