package com.medlinker.hybrid.core

import android.app.Application
import android.content.Context
import android.os.Build
import com.medlinker.hybrid.core.plugin.HybridPluginRegister
import com.medlinker.hybrid.core.plugin.ui.IWVUIBridge
import com.medlinker.hybrid.core.plugin.ui.entity.NavigationConfig
import com.medlinker.hybrid.core.plugin.ui.entity.RefreshStatusTextEntity
import com.medlinker.hybrid.core.webview.IWebViewAdapter
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.CookieManager
import com.tencent.smtt.sdk.CookieSyncManager
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback
import java.util.*


/**
 * @author hmy
 * @time 3/10/21 10:50
 */
object MedHybrid {

    private var openLog = false
    private var appVersionCode = 0
    private var appVersionName = ""
    private var userAgentString = ""
    private var fileHybridDataPath = "hybrid_webapp"
    private var h5CookieDomain = ""
    private var navigationConfig = NavigationConfig()
    private var config: Config? = null

    fun setOpenLog(openLog: Boolean): MedHybrid {
        this.openLog = openLog
        return this
    }

    fun setAppVersionName(appVersionName: String): MedHybrid {
        this.appVersionName = appVersionName
        return this
    }

    fun setAppVersionCode(versionCode: Int): MedHybrid {
        this.appVersionCode = versionCode
        return this
    }

    fun setUserAgentString(userAgentString: String): MedHybrid {
        this.userAgentString = userAgentString
        return this
    }

    fun setFileHybridDataPath(fileDataPath: String): MedHybrid {
        this.fileHybridDataPath = fileDataPath
        return this
    }

    fun setH5CookieDomain(h5CookieDomain: String): MedHybrid {
        this.h5CookieDomain = h5CookieDomain
        return this
    }

    fun setNavigationConfig(navigationConfig: NavigationConfig): MedHybrid {
        this.navigationConfig = navigationConfig
        return this
    }

    fun setConfig(config: Config): MedHybrid {
        this.config = config
        return this
    }

    fun init(application: Application) {
        //TBS内核首次使用和加载时，ART虚拟机会将Dex文件转为Oat，该过程由系统底层触发且耗时较长，很容易引起anr问题，解决方法是使用TBS的 ”dex2oat优化方案“。
        // 在调用TBS初始化、创建WebView之前进行如下配置
        val map = HashMap<String, Any>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
        QbSdk.initTbsSettings(map)
        QbSdk.initX5Environment(application, object : PreInitCallback {
            override fun onCoreInitFinished() {
                log("x5 core load success")
            }

            override fun onViewInitFinished(b: Boolean) {
                log("x5 onViewInitFinished load b=$b")
            }
        })
        HybridPluginRegister.setup()
    }

    fun isOpenLog(): Boolean {
        return openLog
    }

    fun getAppVersionName(): String {
        return appVersionName
    }

    fun getAppVersionCode(): Int {
        return appVersionCode
    }

    fun getUserAgentString(): String {
        return userAgentString
    }

    fun getFileHybridDataPath(): String {
        return fileHybridDataPath
    }

    fun getH5CookieDomain(): String {
        return h5CookieDomain
    }

    fun geNavigationConfig(): NavigationConfig {
        return navigationConfig
    }

    fun getConfig(): Config? {
        return config
    }

    fun syncCookie(context: Context) {
        log("syncCookie")
        CookieSyncManager.createInstance(context)
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        config?.getWebViewAdapter()?.let { viewAdapter ->
            if (viewAdapter.syncCookieIntercept(CookieSyncManager.getInstance(), cookieManager)) {
                return
            }
            viewAdapter.getCookieParams()?.let {
                for (param in it) {
                    log("cookie param = $param")
                    cookieManager.setCookie(getH5CookieDomain(), param)
                }
            }
            sync()
            val cookie = cookieManager.getCookie(getH5CookieDomain())
            log("cookie = $cookie")
        }
    }

    fun cleanCookie(context: Context) {
        CookieSyncManager.createInstance(context)
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.removeAllCookie()
        sync()
    }

    private fun sync() {
        if (Build.VERSION.SDK_INT < 21) {
            CookieSyncManager.getInstance().sync()
        } else {
            CookieManager.getInstance().flush()
        }
    }

    open class Config private constructor(builder: Builder) {
        private var refreshStatusTextEntity: RefreshStatusTextEntity? = null
        private var webViewAdapter: IWebViewAdapter? = null
        private var uiBridge: IWVUIBridge? = null

        init {
            refreshStatusTextEntity = builder.refreshStatusTextEntity
            webViewAdapter = builder.webViewAdapter
            uiBridge = builder.wvUIBridge
        }

        fun getWebViewAdapter(): IWebViewAdapter? {
            return webViewAdapter
        }

        fun getUIBridge(): IWVUIBridge? {
            return uiBridge
        }

        fun getRefreshHeaderText(): RefreshStatusTextEntity? {
            return refreshStatusTextEntity
        }

        open class Builder {
            internal var refreshStatusTextEntity: RefreshStatusTextEntity? = null
            internal var webViewAdapter: IWebViewAdapter? = null
            internal var wvUIBridge: IWVUIBridge? = null

            fun setWebViewAdapter(webViewAdapter: IWebViewAdapter): Builder {
                this.webViewAdapter = webViewAdapter
                return this
            }

            fun setUIAdapter(uiBridge: IWVUIBridge): Builder {
                this.wvUIBridge = uiBridge
                return this
            }

            fun setRefreshHeaderText(refreshStatusTextEntity: RefreshStatusTextEntity?): Builder {
                this.refreshStatusTextEntity = refreshStatusTextEntity
                return this
            }

            fun build(): Config {
                return Config(this)
            }

        }
    }

    fun log(msg: String?) {
        log(msg, null)
    }

    fun log(msg: String?, tr: Throwable?) {
        if (openLog) {
            config?.getWebViewAdapter()?.log(msg, tr)
        }
    }

}