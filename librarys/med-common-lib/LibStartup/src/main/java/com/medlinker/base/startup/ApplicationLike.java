package com.medlinker.base.startup;

import android.app.Application;

import androidx.annotation.IntRange;
import androidx.annotation.Keep;

/**
 * 组件SDK初始化
 * <p>1、请在manifest.xml中注册 确保android:value="app.startup"</p>
 * <pre class="prettyprint">
 *  <meta-data
 *    android:name="包名.类名"
 *    android:value="app.startup" />
 * </pre>
 * <p>2、如果想关闭某个app</p>
 * <pre class="prettyprint">
 *    <meta-data
 *       android:name="net.medlinker.android.MainApp"
 *       tools:node="remove"
 *       android:value="app.startup" />
 * </pre>
 *
 * @author zhangquan
 */
@Keep
public interface ApplicationLike {
    /**
     * 初始化 在Application.onCreate()中调用
     */
    void onCreate(Application ctx);

    /**
     * 初始化含有敏感信息(隐私、设备)的SDK 用户同意隐私政策后才会调用
     */
    void initSensitiveSDK(Application ctx);

    /**
     * 只在主进程中初始化含有敏感信息(隐私、设备)的SDK
     */
    void initSensitiveSDKOnMainProcess(Application ctx);

    /**
     * 可以在线程中异步初始化的SDK
     */
    void initAsync(Application ctx);

    /**
     * 加载优先级 0-10  优先级越高 越先被加载，没特殊需求建议为0
     *
     * @return
     */
    @IntRange(from = 0, to = 10)
    int getPriority();
}
