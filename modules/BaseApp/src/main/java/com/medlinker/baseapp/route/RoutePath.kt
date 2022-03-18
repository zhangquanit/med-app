package com.medlinker.baseapp.route

import android.app.Activity
import net.medlinker.base.router.RouterUtil
import java.net.URLEncoder

/**
 *
 * @author zhangquan
 */
object RoutePath {
    private const val MODULE_APP = "app"
    const val SPLASH_ACTIVITY = "/$MODULE_APP/splash" //启动页
    const val HYBRID_WEBVIEW_ACTIVITY = "/$MODULE_APP/HybridWebViewActivity" //webview

    const val VIDEO_PLAYER_ACTIVITY = "/videoplayer/vod" //视频播放页面

    //登录模块
    private const val MODULE_LOGIN = "login"
    const val LOGIN_ACTIVITY = "/$MODULE_LOGIN/login" //登录
    const val LOGIN_OFFLINE_DIALOG_ACTIVITY = "/$MODULE_LOGIN/OfflineDialogActivity" //下线通知

    //主module
    private const val MODULE_MAIN = "main"
    const val MAIN_ACTIVITY = "/$MODULE_MAIN/MainActivity" //主页面
    const val USER_BASIC_INFO_ACTIVITY = "/$MODULE_MAIN/BasicInfoActivity" //用户基本信息
    const val TAB_MINE = "/$MODULE_MAIN/mine" //我的

    const val TAB_HOME="/test/home"

//#############################################################################################################
    /**
     * 打开主页面
     */
    fun startMainActivity() {
        RouterUtil.startActivity(MAIN_ACTIVITY)
    }

    /**
     * 打开主页面-重新登录
     */
    fun startMainActivityForRelogin(activity: Activity, msg: String) {
        RouterUtil.startActivity(activity, MAIN_ACTIVITY, msg)
    }

    /**
     * 登录页面
     */
    fun startLoginActivity() {
        RouterUtil.startActivity(LOGIN_ACTIVITY)
    }

    /**
     * 用户下线通知页面
     */
    fun startOfflineDialogActivty(activity: Activity, msg: String) {
        RouterUtil.startActivity(activity, LOGIN_OFFLINE_DIALOG_ACTIVITY, msg)
    }

    /**
     * 打开播放页面
     */
    fun startVideoPlayerActivity(videoPath: String) {
        val url = VIDEO_PLAYER_ACTIVITY + "?url=" + URLEncoder.encode(videoPath)
        RouterUtil.startActivity(url)
    }

    /**
     * 打开web页面
     */
    fun startWebActivity(activity: Activity, url: String) {
        val path = HYBRID_WEBVIEW_ACTIVITY + "?url=" + URLEncoder.encode(url)
        RouterUtil.startActivity(activity, path)
    }

}