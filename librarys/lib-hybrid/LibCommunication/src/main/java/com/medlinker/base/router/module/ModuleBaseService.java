package com.medlinker.base.router.module;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * Base模块调用上层模块方法路由服务
 *
 * @author hmy
 */
public interface ModuleBaseService extends IProvider {

    void baseActivityOnResume(Activity activity, String pageName);

    void baseActivityOnPause(Activity activity, String pageName);

    void baseActivityDispatchTouchEvent(Activity activity, MotionEvent event);

    /**
     * 打开push打点
     *
     * @param intent
     */
    void statisticEventOpenPush(Intent intent);

    /**
     * 处理下线
     */
    void dealOffline(String msg);


    /**
     * 退出登录
     *
     * @param enterType 0代表退出登录，1代表重新登录
     */
    void logoutApp(FragmentActivity activity, int enterType);

    void toWebThridActivity(Context context, String url);

    void toWebDefaultActivity(Context context, String title, String url);

    void router(Context context, String url);

    void router(Context context, String url, int requestCode);

    void router(Context context, String url, int requestCode, boolean isNeedLogin);

    void router(Context context, String moduleName, String routeName, String extra);

    void router(Context context, String moduleName, String routeName, String extra, int requestCode);

    void router(Context context, String moduleName, String routeName, String extra, int requestCode, boolean isNeedLogin);

    String createJumpRouterUrl(String moduleName, String routeName, @Nullable String extra);
}
