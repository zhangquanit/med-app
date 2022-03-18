package com.medlinker.hybrid.core.plugin

import com.medlinker.bridge.BridgeManager
import com.medlinker.hybrid.core.jsbridge.WVApiPlugin
import com.medlinker.hybrid.core.jsbridge.WVCallbackContext
import com.medlinker.hybrid.core.webview.IHybrid


/**
 * @author hmy
 * @time 11/10/21 16:59
 */
class UpdaterPlugin : WVApiPlugin() {
    override fun supportMethodNames(): Array<String> {
        return arrayOf("checkUpdate")
    }

    override fun execute(hybrid: IHybrid?, methodName: String, params: String, callbackContext: WVCallbackContext): Boolean {
        try {
            when (methodName) {
                "checkUpdate" -> {
                    BridgeManager.getUpdaterBridge()?.checkAppUpdate(hybrid?.getCurContext())
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