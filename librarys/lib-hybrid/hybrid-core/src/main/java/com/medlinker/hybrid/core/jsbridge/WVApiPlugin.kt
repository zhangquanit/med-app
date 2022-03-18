package com.medlinker.hybrid.core.jsbridge

import android.content.Intent
import android.net.Uri
import com.medlinker.hybrid.core.jsbridge.entity.WVHybridCallbackEntity
import com.medlinker.hybrid.core.webview.IHybrid
import com.tencent.smtt.sdk.WebView


/**
 * @author hmy
 * @time 3/9/21 10:24
 */
abstract class WVApiPlugin {

    private var isAlive = true
    protected var mPluginName = ""
    protected var mHybrid: IHybrid? = null

    fun initialize(pluginName: String, hybrid: IHybrid?) {
        mPluginName = pluginName
        mHybrid = hybrid
    }

    /**
     * 支持的协议方法
     */
    abstract fun supportMethodNames(): Array<String>

    /**
     * @return //要返回true否则内核会继续处理
     */
    abstract fun execute(hybrid: IHybrid?, methodName: String, params: String, callbackContext: WVCallbackContext): Boolean

    /**
     * 无需返回回调数据调用
     */
    fun sendHybridCallbackVoid(callbackContext: WVCallbackContext?) {
        WVPluginManager.sendHybridCallback(mHybrid?.getWebView(), callbackContext?.callbackId, WVHybridCallbackEntity())
    }

    fun sendHybridCallback(callbackId: String?, callbackEntity: WVHybridCallbackEntity?) {
        WVPluginManager.sendHybridCallback(mHybrid?.getWebView(), callbackId, callbackEntity)
    }

    fun sendHybridStatus(callbackId: String?, data: Any?) {
        WVPluginManager.sendHybridStatus(mHybrid?.getWebView(), callbackId, data)
    }

    fun sendHybridEvent(eventName: String, moduleName: String, data: Any?) {
        WVPluginManager.sendHybridEvent(mHybrid?.getWebView(), eventName, moduleName, data)
    }

    /**
     * 发送方法不存在
     */
    fun sendHybridMethodNotFoundCallback(callbackId: String?) {
        WVPluginManager.sendHybridMethodNotExistCallback(mHybrid?.getWebView(), callbackId)
    }

    fun interceptByOriginalHost(webView: WebView?): Boolean {
        webView?.let {
            val oriUri = Uri.parse(it.url)
            oriUri.host?.let { host ->
                return !host.matches(Regex(".+\\.medlinker\\.com$"))
            }
            return true
        }
        return true
    }

    open fun onNewIntent(intent: Intent?) {}

    open fun onStart() {
        isAlive = true
    }

    open fun onResume() {
        isAlive = true
    }

    open fun onPause() {
        isAlive = false
    }

    open fun onStop() {
        isAlive = false
    }

    open fun onDestroy() {
        isAlive = false
    }

    open fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {}
}