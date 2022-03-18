package com.medlinker.lib.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

/**
 * Author: KindyFung.
 * CreateTime:  2015/11/3 20:35
 * Email：fangjing@medlinker.com.
 * Description:
 */
public class MedJumpUtil {


    /**
     * 拨打电话
     *
     * @param context
     * @param phoneNumber
     */
    public static void dialPhoneNumber(Context context, String phoneNumber) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(phoneNumber));
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拨打电话
     */
    public static void call(Context context, String phoneNum) {
        try {
            Uri uri = Uri.parse("tel:" + phoneNum);
            Intent intent = new Intent(Intent.ACTION_DIAL, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 跳转到短信发送界面
     *
     * @param context    上下文
     * @param smsContent 短信内容
     */
    public static void smsTo(Context context, String smsContent) {
        smsTo(context, smsContent, "");
    }

    /**
     * 跳转到短信发送界面
     *
     * @param context     上下文
     * @param smsContent  短信内容
     * @param phoneNumber 联系人号码
     */
    public static void smsTo(Context context, String smsContent, String phoneNumber) {
        Uri uri = Uri.parse("smsto:" + phoneNumber);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.putExtra("sms_body", smsContent);
        intent.setType("vnd.android-dir/mms-sms");
        intent.setData(uri);
        try {
            // 跳转到短信发送界面，此处需要捕捉异常
            context.startActivity(intent);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * 跳转到相关的应用程序
     *
     * @param context 上下文
     * @param url     链接地址
     */
    public static void toUrl(Context context, String url) {
        if (context == null || TextUtils.isEmpty(url)) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    /**
     * 跳转到邮件发送界面
     *
     * @param context      上下文
     * @param emailAddress 邮箱地址
     */
    public static void toEmail(Context context, String emailAddress) {
        if (context == null || TextUtils.isEmpty(emailAddress)) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + emailAddress));
        context.startActivity(intent);
    }

    /**
     * 跳转App市场
     */
    public static void toAppStore(Context context) {
        if (context == null) {
            return;
        }
        final String appPackageName = context.getPackageName();
        if (isTargetPhone("samsung")) {
            Uri uri = Uri.parse("http://www.samsungapps.com/appquery/appDetail.as?appId=" + appPackageName);
            Intent goToMarket = new Intent();
            goToMarket.setClassName("com.sec.android.app.samsungapps", "com.sec.android.app.samsungapps.Main");
            goToMarket.setData(uri);
            try {
                context.startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                toCommonAppStore(context);
            }
        } else {
            toCommonAppStore(context);
        }
    }

    private static void toCommonAppStore(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        try {
            final String appPackageName = context.getPackageName();
            context.startActivity(intent.setData(Uri.parse("market://details?id=" + appPackageName)));
        } catch (ActivityNotFoundException anfe) {
//                ToastUtil.showMessage(context.getApplicationContext(), "not found appstore, jump to googleplay websites.");
//                context.startActivity(intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            MedToastUtil.showMessage("您的手机没有应用市场");
        }
    }

    /**
     * 是否是对应品牌手机
     */
    public static boolean isTargetPhone(String phoneBrand) {
        return getManufacturer().toLowerCase().contains(phoneBrand);
    }

    /**
     * 获取厂商信息
     */
    public static String getManufacturer() {
        return (Build.MANUFACTURER) == null ? "" : (Build.MANUFACTURER).trim();
    }
}
