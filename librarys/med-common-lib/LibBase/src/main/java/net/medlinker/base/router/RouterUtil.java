package net.medlinker.base.router;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.core.LogisticsCenter;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.Utils;
import com.medlinker.router.MedRouterHelper;

import java.io.Serializable;

/**
 * @author hmy
 * @time 2020/9/17 09:55
 */
public class RouterUtil {

    public static final String DATA_KEY = "DATA_KEY";

    /***/
    public static void startActivity(String path) {
        MedRouterHelper.withUrl(path).queryTarget().navigation(Utils.getApp());
    }

    /***/
    public static void startActivity(Context context, String path) {
        MedRouterHelper.withUrl(path).queryTarget().navigation(context);
    }

    /***/
    public static void startActivity(Activity context, String path, int requestCode) {
        MedRouterHelper.withUrl(path).queryTarget().navigation(context, requestCode);
    }

    /***/
    public static void startActivity(String path, Serializable data) {
        MedRouterHelper.withUrl(path).withSeriablele(DATA_KEY, data).queryTarget().navigation(Utils.getApp());
    }

    /***/
    public static void startActivity(Context context, String path, Serializable data) {
        MedRouterHelper.withUrl(path).withSeriablele(DATA_KEY, data).queryTarget().navigation(context);
    }

    /***/
    public static void startActivity(Activity activity, String path, int requestCode, Serializable data) {
        MedRouterHelper.withUrl(path).withSeriablele(DATA_KEY, data).queryTarget().navigation(activity,requestCode);
    }

    /***/
    public static void startActivity(Activity activity, String path, int requestCode, String data) {
        MedRouterHelper.withUrl(path).withString(DATA_KEY, data).queryTarget().navigation(activity,requestCode);
    }

    /***/
    public static void startActivity(Activity activity, String path, String data) {
        MedRouterHelper.withUrl(path).withString(DATA_KEY, data).queryTarget().navigation(activity);
    }

    /***/
    public static void startActivity(String path, Parcelable data) {
        MedRouterHelper.withUrl(path).withParcelable(DATA_KEY, data).queryTarget().navigation(Utils.getApp());
    }

    /***/
    public static void startActivity(Context context, String path, Parcelable data) {
        MedRouterHelper.withUrl(path).withParcelable(DATA_KEY, data).queryTarget().navigation(context);
    }

    /***/
    public static void startActivity(Activity activity, String path, int requestCode, Parcelable data) {
        MedRouterHelper.withUrl(path).withParcelable(DATA_KEY, data).queryTarget().navigation(activity,requestCode);
    }

    /**
     * 根据路由获取目标类型
     *
     * @param path
     * @return
     */
    public static Class<?> getClass(String path) {
        Postcard postcard = ARouter.getInstance().build(path);
        LogisticsCenter.completion(postcard);
        return postcard.getDestination();
    }

    /**
     * 根据路由判断是否是当前界面
     *
     * @param activity
     * @param targetRouterPath
     * @return
     */
    public static boolean isTargetRouterActivity(Activity activity, String targetRouterPath) {
        return activity != null && !TextUtils.isEmpty(targetRouterPath)
                && activity.getClass().getName().equals(getClass(targetRouterPath).getName());
    }

    /***/
    public static Fragment getFragment(String path) {
        return (Fragment) ARouter.getInstance().build(path).navigation();
    }

    /***/
    public static Fragment getFragment(String path, boolean data) {
        return (Fragment) ARouter.getInstance().build(path).withBoolean(DATA_KEY, data).navigation();
    }

    /***/
    public static Fragment getFragment(String path, Serializable data) {
        return (Fragment) ARouter.getInstance().build(path).withSerializable(DATA_KEY, data).navigation();
    }

    /**
     * 初始化arouter
     */
    public static void init(Application application, boolean isDebug) {
        if (isDebug) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(application); // 尽可能早，推荐在Application中初始化
    }
}
