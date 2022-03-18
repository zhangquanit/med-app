package com.medlinker.hybrid.core.plugin

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.medlinker.bridge.BridgeManager
import com.medlinker.hybrid.core.jsbridge.WVApiPlugin
import com.medlinker.hybrid.core.jsbridge.WVCallbackContext
import com.medlinker.hybrid.core.jsbridge.entity.WVHybridCallbackEntity
import com.medlinker.hybrid.core.ui.WVHybridWebViewActivity
import com.medlinker.hybrid.core.webview.IHybrid
import org.json.JSONObject


/**
 * @author hmy
 * @time 4/12/21 16:34
 */
class RouterPlugin : WVApiPlugin() {

    override fun supportMethodNames(): Array<String> {
        return arrayOf("canWebBack", "back", "close", "forward", "replace")
    }

    override fun execute(hybrid: IHybrid?, methodName: String, params: String, callbackContext: WVCallbackContext): Boolean {
        try {
            when (methodName) {
                "canWebBack" -> {
                    hybrid?.let {
                        sendHybridCallback(callbackContext.callbackId, WVHybridCallbackEntity().setData(hybrid.getWebView().canGoBack()))
                    }
                    return true
                }
                "back" -> {
                    hybrid?.let {
                        if (!it.onBackPressed() && it.getCurContext() is Activity) {
                            (it.getCurContext() as Activity).finish()
                        }
                    }
                    sendHybridCallbackVoid(callbackContext)
                    return true
                }
                "close" -> {
                    sendHybridCallbackVoid(callbackContext)
                    hybrid?.getCurContext()?.let {
                        if (it is Activity) {
                            it.finish()
                        }
                    }
                    return true
                }
                "forward" -> {
                    hybrid?.let {
                        val jsonObject = JSONObject(params)
                        val url = jsonObject.optString("url")
                        val type = jsonObject.optString("type")
                        route(it.getCurContext(), url, type)
                    }
                    sendHybridCallbackVoid(callbackContext)
                    return true
                }
                "replace" -> {
                    hybrid?.let {
                        if (it.getCurContext() is Activity) {
                            (it.getCurContext() as Activity).finish()
                        }
                        val jsonObject = JSONObject(params)
                        val url = jsonObject.optString("url")
                        val type = jsonObject.optString("type")
                        route(it.getCurContext(), url, type)
                    }
                    sendHybridCallbackVoid(callbackContext)
                    return true
                }
                else -> {
                    sendHybridMethodNotFoundCallback(callbackContext.callbackId)
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun route(context: Context?, url: String, type: String?) {
        BridgeManager.getRouterBridge()?.let {
            if (!it.route(context, url, type)) {
                if (!TextUtils.isEmpty(url) && (url.startsWith("http://")
                                || url.startsWith("https://")
                                || url.startsWith("www."))) {
                    val intent = Intent(context, WVHybridWebViewActivity::class.java)
                    intent.putExtra(WVHybridWebViewActivity.URL, url)
                    context?.startActivity(intent)
                }
            }
        }

    }
}