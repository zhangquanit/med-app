package com.medlinker.hybridapp.plugin

import com.medlinker.hybrid.core.jsbridge.WVApiPlugin
import com.medlinker.hybrid.core.jsbridge.WVCallbackContext
import com.medlinker.hybrid.core.jsbridge.entity.WVHybridCallbackEntity
import com.medlinker.hybrid.core.webview.IHybrid
import org.json.JSONObject


/**
 * @author hmy
 * @time 3/17/21 16:40
 */
class BatteryPlugin : WVApiPlugin() {
    override fun supportMethodNames(): Array<String> {
        return arrayOf("getInfo")
    }

    override fun execute(hybrid: IHybrid?, methodName: String, params: String, callbackContext: WVCallbackContext): Boolean {
        if ("getInfo" == methodName) {
            val jsonObject = JSONObject(params)
            val methodValue: String = jsonObject.get("onUpdate").toString()
            sendHybridStatus(methodValue, 1)

            val callbackParam = WVHybridCallbackEntity()
            callbackParam.code = 0
            callbackParam.message = "xxxxxxxx"
            callbackParam.data = "aaaaaaaa"

            sendHybridCallback(callbackContext.callbackId, callbackParam)

            sendHybridEvent("pageshow", "ui", null)
            return true
        }
        return false
    }
}