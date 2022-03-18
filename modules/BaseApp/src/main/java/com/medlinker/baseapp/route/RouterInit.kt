package com.medlinker.baseapp.route

import android.app.Application
import android.content.Context
import com.medlinker.baseapp.BuildConfig
import com.medlinker.router.Config
import com.medlinker.router.MedRouterHelper
import com.medlinker.router.router.BaseMedRouterMapping
import net.medlinker.base.router.RouterUtil
import java.util.*


/**
 * @author hmy
 * @time 10/19/21 10:05
 */
object RouterInit {
    val hybirdRouteMap = HashMap<String, String>()

    @JvmStatic
    fun init(application: Application) {
        RouterUtil.init(application, BuildConfig.DEBUG)
        //
        hybirdRouteMap["/app/home"] = RoutePath.MAIN_ACTIVITY

        val routerMap: MutableMap<String, Class<out BaseMedRouterMapping>> = HashMap()
        routerMap[LinkRouter.ROUTER] = LinkRouter::class.java

        //...
        MedRouterHelper.init(routerMap, object : Config() {
            override fun launchReactActivity(
                medRouter: MedRouterHelper.MedRouter,
                context: Context, moduleName: String, routeName: String,
                extra: String, requestCode: Int
            ) {
            }

            override fun onLostPage(context: Context) {

            }

            override fun checkLogin(medRouter: MedRouterHelper.MedRouter): BaseMedRouterMapping {
                return super.checkLogin(medRouter)
            }
        })
    }
}