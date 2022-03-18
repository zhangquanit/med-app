package com.medlinker.photoviewer.utils;/**
 * Created by kuiwen on 2015/10/28.
 */

import android.text.TextUtils;

import java.util.regex.Pattern;

/**
 * @author <a href="mailto:zhangkuiwen@medlinker.net">Kuiwen.Zhang</a>
 * @version 1.0
 * @description 功能描述
 * @time 2015/10/28 11:47
 **/
public class ImageUtil {

    /**
     * url已check过的标识
     */
    private static String ANCHOR_TAG = "#MED_IMG_CHECKED";

    private static String QINIU_SCHEME = "imgs.medlinker.net";//七牛图片
    private static String QINIU_SCHEME_TEST = "glb";//七牛图片
    private static String MEDLINKER_SCHEME = "temp";//medlinker图片


    /**
     * 基于原图大小，按指定百分比缩放。Scale取值范围1-999。
     *
     * @param url
     * @return
     */
    public static String checkUrl(String url, int scale) {
        if (scale < 1 || scale > 999 || TextUtils.isEmpty(url) || hasChecked(url) || !url.startsWith("http") || url.contains(MEDLINKER_SCHEME)) {
            return url;
        }
        if (url.contains(QINIU_SCHEME_TEST) || url.contains(QINIU_SCHEME)) {
            url = url.concat(url.endsWith("?") ? "imageMogr2/thumbnail/" : "?imageMogr2/thumbnail/")
                    .concat("!" + scale + "p").concat("/interlace/1");
        }
        return url.concat(ANCHOR_TAG);
    }

    public static boolean hasChecked(String url) {
        return TextUtils.isEmpty(url) || url.endsWith(ANCHOR_TAG);
    }
}
