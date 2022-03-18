package com.medlinker.lib.push.med

import android.content.Context
import android.text.TextUtils
import com.igexin.sdk.PushManager
import com.medlinker.lib.utils.MedAppInfo


/**
 *
 * @author zhangquan
 */
object PushClient {
    @JvmStatic
    fun init(ctx: Context, options: PushOptions) {
        PushManager.getInstance().initialize(ctx)
        if (MedAppInfo.isDebug) {
            PushManager.getInstance().setDebugLogger(ctx) { s ->
                PushLog.log(s)
            }
        }
        PushMsgProcessor.getInstance().setOptions(options)
    }

    fun turnOnPush(ctx: Context) {
        PushManager.getInstance().turnOnPush(ctx)
    }

    fun turnOffPush(ctx: Context) {
        PushManager.getInstance().turnOffPush(ctx)
    }

    fun isPushTurnedOn(ctx: Context): Boolean {
        return PushManager.getInstance().isPushTurnedOn(ctx)
    }

    fun getClientId(ctx: Context): String {
        return PushManager.getInstance().getClientid(ctx)
    }

    fun bindClientId() {
        val clientId = getClientId(MedAppInfo.appContext)
        if (TextUtils.isEmpty(clientId)) return
        PushMsgProcessor.getInstance().bindClientId(clientId)
    }

    /**
     * 检测用户设备是否开启通知权限
     */
    fun areNotificationsEnabled(ctx: Context): Boolean {
        return PushManager.getInstance().areNotificationsEnabled(ctx)
    }

    /**
     * 开启通知权限
     * 调用方法，将跳转到系统开启通知功能页面，引导用户开启通知权限
     */
    fun openNotification(ctx: Context) {
        PushManager.getInstance().openNotification(ctx)
    }

    /**
     * 设置华为、vivo、oppo桌面角标
     */

    /**
     * 设置静默时间
     * 静默期间内 SDK 断开连接，将不再接收消息
     * setSilentTime(Context context,int beginHour,int duration)
     */
}