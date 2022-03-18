package com.medlinker.baseapp.route

import android.content.Context
import com.medlinker.router.MedRouterHelper
import com.medlinker.router.router.BaseMedRouterMapping
import net.medlinker.base.router.RouterUtil


/**
 * @author hmy
 * @time 10/19/21 10:02
 */
class LinkRouter : BaseMedRouterMapping {

    companion object {
        const val ROUTER = "/link"
    }

    constructor(router: MedRouterHelper.MedRouter) : super(router)

    override fun getRouterKey(): String {
        return ROUTER
    }

    override fun needInterrupt(context: Context?, requestCode: Int): Boolean {
        var url = mMedRouter.params["url"]
        try {
            //兼容url未经过encode 传值的情况
            if (mMedRouter.params.size > 1) {
                mMedRouter.params.remove("url")
                mMedRouter.params.forEach {
                    url += "&" + it.key + "=" + it.value
                }
            }
        } catch (e: Exception) {
        }
        RouterUtil.startActivity(context, RoutePath.HYBRID_WEBVIEW_ACTIVITY, url)
        return true
    }

    override fun getTargetClass(): Class<*>? {
        return null
    }
}