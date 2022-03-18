package net.medlinker.android.hybrid

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.medlinker.baseapp.route.RoutePath
import com.medlinker.hybrid.core.MedHybrid
import com.medlinker.hybrid.core.jsbridge.WVPluginManager
import com.medlinker.hybrid.core.ui.WVHybridFragment
import com.medlinker.lib.log.LogUtil
import com.medlinker.lib.utils.MedAppInfo
import net.medlinker.base.router.RouterUtil
import net.medlinker.base.storage.KVUtil

/**
 * @author hmy
 * @time 10/13/21 17:17
 */
@Route(path = RoutePath.HYBRID_WEBVIEW_ACTIVITY)
class HybridWebViewActivity : AppCompatActivity() {

    private lateinit var mFragment: WVHybridFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MedHybrid.getConfig()?.getUIBridge()?.configImmersiveMode(this)
        var url = intent.getStringExtra(RouterUtil.DATA_KEY)
        if (TextUtils.isEmpty(url)) {
            url = intent.getStringExtra("url")
        }
        if (TextUtils.isEmpty(url)) {
            finish()
            return
        }
        LogUtil.d("hybrid", "替换前url：$url")
        url = urlInterceptor(url)
        LogUtil.d("hybrid", "替换后url：$url")
        mFragment = WVHybridFragment.newInstance(url)
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, mFragment, WVHybridFragment::class.java.toString())
            .commitAllowingStateLoss()
    }

    /**
     * 插件拦截 替换域名
     */
    fun urlInterceptor(url: String): String {
        if (MedAppInfo.isDebug) {
            val laneName = KVUtil.getString("medlinker_env_lane")
            if (!TextUtils.isEmpty(laneName) && !TextUtils.equals(laneName, "online")) {
                val host = Uri.parse(url).host
                if (host?.matches(Regex("web\\.medlinker\\.com|^web.+\\.medlinker\\.com$")) == true) {
                    val newHost = String.format("web-%s.medlinker.com", laneName)
                    return url.replace(host, newHost)
                }
            }
        }
        return url
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        WVPluginManager.onNewIntent(mFragment, intent)
    }

    override fun onBackPressed() {
        if (!mFragment.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mFragment.onActivityResult(requestCode, resultCode, data)
    }
}