package com.medlinker.baseapp.route

import android.app.Activity
import android.net.Uri
import android.text.TextUtils
import com.medlinker.router.MedRouterHelper
import net.medlinker.base.manager.ActivityStashManager
import net.medlinker.base.router.RouterUtil


/**
 * @author hmy
 * @time 11/30/21 16:01
 */
enum class AppJumpHelper {
    INSTANCE;

    private var mRouterUri: Uri? = null

    fun setRouter(uri: Uri?) {
        mRouterUri = uri
    }

    fun clear() {
        mRouterUri = null
    }

    fun checkRouter(activity: Activity?): Boolean {
        mRouterUri?.let {
            val mainActivity = RouterUtil.getClass(RoutePath.MAIN_ACTIVITY)
            if (TextUtils.isEmpty(it.path) || !ActivityStashManager.hasActivityInStack(mainActivity.name)) {
                return false
            }
            val url = it.toString()
            clear()
            MedRouterHelper.withUrl(url).queryTarget().navigation(activity)
            return true
        }
        return false
    }
}