package com.medlinker.hybrid.core.plugin

import com.medlinker.bridge.BridgeManager
import com.medlinker.hybrid.core.jsbridge.WVApiPlugin
import com.medlinker.hybrid.core.jsbridge.WVCallbackContext
import com.medlinker.hybrid.core.jsbridge.entity.WVHybridCallbackEntity
import com.medlinker.hybrid.core.webview.IHybrid


/**
 * @author hmy
 * @time 4/21/21 17:54
 */
class DevicePlugin : WVApiPlugin() {
    override fun supportMethodNames(): Array<String> {
        return arrayOf("getClipboard", "setClipboard")
    }

    override fun execute(hybrid: IHybrid?, methodName: String, params: String, callbackContext: WVCallbackContext): Boolean {
        try {
            when (methodName) {
                "getClipboard" -> {
                    BridgeManager.getDeviceBridge()?.let { bridge ->
                        bridge.getClipboard(hybrid?.getCurContext()) {
                            sendHybridCallback(callbackContext.callbackId, WVHybridCallbackEntity().setData(it))
                        }
                    }
                    return true
                }
                "setClipboard" -> {
                    BridgeManager.getDeviceBridge()?.setClipboard(hybrid?.getCurContext(), params)
                    sendHybridCallback(callbackContext.callbackId, WVHybridCallbackEntity())
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