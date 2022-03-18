package com.medlinker.lib.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;

import java.util.List;

public class MedShortCutUtil {
    /**
     * 创建快捷方式
     *
     * @param context 应用上下文
     * @param cls     指定快捷方式的类
     */
    public static void createShortCut(Context context, Class<?> cls, int shotCutIconId, String shortCutName) {
        createShortCut(context, cls, false, shotCutIconId, shortCutName);
    }

    /**
     * 创建快捷方式
     *
     * @param context   上下文对象
     * @param cls       Class
     * @param duplicate 是否可以重复创建
     */
    public static void createShortCut(Context context, Class<?> cls, boolean duplicate, int shotCutIconId, String shortCutName) {
        if (context == null || isShortCutExist(context, shortCutName)) {
            return;
        }
        Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        Intent intent = new Intent(context, cls);
        // 设置这两个属性当应用程序卸载时桌面上的快捷方式会删除
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);

        Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(context,
                shotCutIconId);
        shortcutIntent.putExtra("duplicate", duplicate);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortCutName);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

        context.sendBroadcast(shortcutIntent);
    }

    /**
     * 判断快捷方式是否已存在
     *
     * @param context 应用上下文
     * @return 快捷方式是否已存在
     */
    private static boolean isShortCutExist(Context context, String shortCutName) {
        String providerAuthority = null;
        try {
            providerAuthority = readProviderAuthority(context, "com.android.launcher.permission.READ_SETTINGS");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (providerAuthority == null) {
            return true;
        }
        ContentResolver cr = context.getContentResolver();
        Uri contentUri = Uri.parse("content://" + providerAuthority + "/favorites?notify=true");
        Cursor cursor;
        try {
            cursor = cr.query(contentUri, null, "title=?",
                    new String[]{shortCutName}, null);
        } catch (Exception e) {
            return true;
        }
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        return false;
    }

    /**
     * 读取权限
     *
     * @param context    应用上下文
     * @param permission 应用权限
     * @return
     */
    private static String readProviderAuthority(Context context, String permission) {
        if (permission == null) {
            return null;
        }
        List<PackageInfo> packageInfoList = context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS);
        if (packageInfoList == null) {
            return null;
        }
        for (PackageInfo pack : packageInfoList) {
            ProviderInfo[] providers = pack.providers;
            if (providers != null) {
                for (ProviderInfo provider : providers) {
                    if (permission.equals(provider.readPermission)) {
                        return provider.authority;
                    } else if (permission.equals(provider.writePermission)) {
                        return provider.authority;
                    }
                }
            }
        }
        return null;
    }
}
