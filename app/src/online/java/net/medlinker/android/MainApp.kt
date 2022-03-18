package net.medlinker.android

import android.app.Application
import com.medlinker.base.startup.ApplicationLike
import net.medlinker.main.hybrid.HybridInit

/**
 * 应用SDK初始化
 * @author zhangquan
 */
class MainApp : ApplicationLike {
    override fun onCreate(ctx: Application) {
    }

    override fun initSensitiveSDK(ctx: Application) {
    }

    override fun initSensitiveSDKOnMainProcess(ctx: Application) {
        HybridInit.init(ctx)
//        MedIMInit.INSTANCE.init(ctx, MedAppInfo.isDebug, BuildConfig.VERSION_CODE, R.mipmap.icon_app);
    }

    override fun initAsync(ctx: Application) {
    }

    override fun getPriority(): Int {
        return 0
    }
}