package com.medlinker.hybrid.core.plugin

import android.content.Intent
import android.net.Uri
import com.medlinker.bridge.BridgeManager
import com.medlinker.hybrid.core.jsbridge.WVApiPlugin
import com.medlinker.hybrid.core.jsbridge.WVCallbackContext
import com.medlinker.hybrid.core.jsbridge.entity.WVHybridCallbackEntity
import com.medlinker.hybrid.core.webview.IHybrid
import org.json.JSONArray
import org.json.JSONObject


/**
 * @author hmy
 * @time 11/10/21 17:33
 */
class LinkingPlugin : WVApiPlugin() {
    override fun supportMethodNames(): Array<String> {
        return arrayOf("checkInstalled", "openURL")
    }

    override fun execute(hybrid: IHybrid?, methodName: String, params: String, callbackContext: WVCallbackContext): Boolean {
        try {
            when (methodName) {
                "checkInstalled" -> {
                    BridgeManager.getLinkingBridge()?.let {
                        val jsonObject = JSONObject(params)
                        val jsonArray: JSONArray? = jsonObject.optJSONArray("apps")
                        if (jsonArray == null || jsonArray.length() == 0) {
                            return true
                        }

                        val apps = Array(jsonArray.length()) { "" }
                        for (i in 0 until jsonArray.length()) {
                            val app = jsonArray.get(i) as String
                            apps[i] = app
                        }
                        val result = it.checkInstalled(hybrid?.getCurContext(), apps)
                        sendHybridCallback(callbackContext.callbackId, WVHybridCallbackEntity().setData(JSONArray(result)))
                    }
                    return true
                }
                "openURL" -> {
                    hybrid?.getCurContext()?.let {
                        val jsonObject = JSONObject(params)
                        val url = jsonObject.optString("url")
                        val intent = Intent()
                        intent.data = Uri.parse(url)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        it.startActivity(intent)
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
}