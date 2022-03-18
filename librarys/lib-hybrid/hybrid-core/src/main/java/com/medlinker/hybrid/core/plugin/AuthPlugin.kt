package com.medlinker.hybrid.core.plugin

import android.app.Activity
import android.content.Intent
import com.medlinker.bridge.BridgeManager
import com.medlinker.hybrid.core.jsbridge.WVApiPlugin
import com.medlinker.hybrid.core.jsbridge.WVCallbackContext
import com.medlinker.hybrid.core.jsbridge.entity.WVHybridCallbackEntity
import com.medlinker.bridge.BridgeRequestCode
import com.medlinker.hybrid.core.webview.IHybrid
import org.json.JSONObject


/**
 * @author hmy
 * @time 4/12/21 19:08
 */
class AuthPlugin : WVApiPlugin() {

    private lateinit var loginCallbackId: String
    override fun supportMethodNames(): Array<String> {
        return arrayOf("checkSession", "login", "getUserInfo", "logout")
    }

    override fun execute(hybrid: IHybrid?, methodName: String, params: String, callbackContext: WVCallbackContext): Boolean {
        try {
            when (methodName) {
                "checkSession" -> {
                    if (!interceptByOriginalHost(hybrid?.getWebView())) {
                        BridgeManager.getAuthBridge()?.let {
                            val jsonObject = JSONObject()
                            jsonObject.put("logined", it.isLogin())
                            jsonObject.put("sessInfo", it.getSessionInfo())
                            sendHybridCallback(callbackContext.callbackId, WVHybridCallbackEntity().setData(jsonObject))
                        }
                    }
                    return true
                }
                "login" -> {
                    hybrid?.getCurContext()?.let {
                        loginCallbackId = callbackContext.callbackId
                        BridgeManager.getAuthBridge()?.login(it, BridgeRequestCode.REQUEST_CODE_LOGIN)
                    }
                    sendHybridCallbackVoid(callbackContext)
                    return true
                }
                "getUserInfo" -> {
                    BridgeManager.getAuthBridge()?.let { bridge ->
                        bridge.getUserInfo {
                            sendHybridCallback(callbackContext.callbackId, WVHybridCallbackEntity().setData(it))
                        }
                    }
                    return true
                }
                "logout" -> {
                    hybrid?.getCurContext()?.let {
                        BridgeManager.getAuthBridge()?.logout(it)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            BridgeRequestCode.REQUEST_CODE_LOGIN -> {
                BridgeManager.getAuthBridge()?.let { bridge ->
                    bridge.getUserInfo {
                        sendHybridCallback(loginCallbackId, WVHybridCallbackEntity().setData(it))
                    }
                }
            }
        }
    }
}