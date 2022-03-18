package com.medlinker.hybrid.core.plugin

import com.medlinker.hybrid.core.jsbridge.WVApiPlugin
import com.medlinker.hybrid.core.jsbridge.WVCallbackContext
import com.medlinker.hybrid.core.jsbridge.WVPluginManager
import com.medlinker.hybrid.core.jsbridge.entity.WVHybridCallbackEntity
import com.medlinker.hybrid.core.webview.IHybrid
import org.json.JSONObject


/**
 * @author hmy
 * @time 11/10/21 14:57
 */
class CorePlugin : WVApiPlugin() {

    override fun supportMethodNames(): Array<String> {
        return arrayOf("caniuse")
    }

    override fun execute(hybrid: IHybrid?, methodName: String, params: String, callbackContext: WVCallbackContext): Boolean {
        try {
            when (methodName) {
                "caniuse" -> {
                    val jsonObject = JSONObject(params)
                    val host = jsonObject.optString("name")
                    val caniuse = WVPluginManager.isSupportHost(hybrid, host)
                    sendHybridCallback(callbackContext.callbackId, WVHybridCallbackEntity().setData(caniuse))
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
}