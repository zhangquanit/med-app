package com.medlinker.bridge

import android.content.Context
import org.json.JSONObject


/**
 * @author hmy
 * @time 4/14/21 17:10
 */
interface AuthBridge {

    /**
     * @return 是否已登录
     */
    fun isLogin(): Boolean

    fun getSessionInfo(): JSONObject?

    /**
     * 去登录，登录成功后回调sess
     */
    fun login(context: Context?, requestCode: Int)

    /**
     * 获取用户信息，返回jsonObject，如果没有登录返回 null，
     */
    fun getUserInfo(callBack: (userInfo: Any?) -> Unit)

    /**
     * 退出登录。一般来说 H5 页面不会主动退出登录，可以视为 js 在接口报错后向 NA 发送的通知
     */
    fun logout(context: Context?)
}