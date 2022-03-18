package com.medlinker.lib.imagepicker.utils;

import android.text.TextUtils;

/**
 * Author: KindyFung.
 * CreateTime:  2015/11/2 18:16
 * Email：fangjing@medlinker.com.
 * Description:
 */
public class StringUtil {
    private static final String TAG = StringUtil.class.getSimpleName();



    /**
     * 拼接统计事件
     *
     * @param event
     * @return
     */
    public static String buildEvent(String event) {
        return buildEvent(event, 0);
    }

    public static String buildEvent(String event, long id) {
        return buildEvent(event, id, null);
    }

    public static String buildEvent(String event1, long id, String event2) {
        StringBuilder stringBuilder = new StringBuilder("btn:");
        if (!TextUtils.isEmpty(event1))
            stringBuilder.append("/").append(event1);
        if (id > 0) stringBuilder.append("/").append(id);
        if (!TextUtils.isEmpty(event2))
            stringBuilder.append("/").append(event2);
        return stringBuilder.toString();
    }

    public static String buildPage(String page) {
        return new StringBuilder("page:/").append(page).toString();
    }

    public static String buildPage(String page, long id) {
        return new StringBuilder("page:/")
                .append(page)
                .append("/")
                .append(id)
                .toString();
    }

    public static String addExtra(String url, String str) {
        if (url.endsWith("?")) {
            return url.concat("extra=").concat(str);
        } else if (url.contains("?")) {
            return url.concat("&extra=").concat(str);
        } else {
            return url.concat("?extra=").concat(str);
        }
    }
}
