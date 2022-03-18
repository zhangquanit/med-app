package com.medlinker.hybrid.core.jsbridge

import android.content.Intent
import android.text.TextUtils
import com.medlinker.bridge.BridgeErrorCode
import com.medlinker.hybrid.core.MedHybrid
import com.medlinker.hybrid.core.jsbridge.entity.WVHybridCallbackEntity
import com.medlinker.hybrid.core.webview.IHybrid
import com.tencent.smtt.sdk.WebView
import org.json.JSONObject
import java.util.concurrent.ConcurrentHashMap

/**
 * @author hmy
 * @time 3/9/21 14:21
 */
object WVPluginManager {
    private val mPluginMap = ConcurrentHashMap<String, Class<out WVApiPlugin>>()
    private val mPluginCacheMap = ConcurrentHashMap<IHybrid?, HashMap<String, WVApiPlugin>?>()

    fun registerPlugin(pluginName: String, cls: Class<out WVApiPlugin>) {
        mPluginMap[pluginName] = cls
    }

    fun unregisterPlugin(pluginName: String) {
        if (mPluginMap.containsKey(pluginName)) {
            mPluginMap.remove(pluginName)
        }
    }

    fun isSupportHost(hybrid: IHybrid?, host: String?): Boolean {
        host?.let { it ->
            if (it.contains(".")) {
                val hosts = it.split(".")
                val pluginName = hosts[0]
                val methodName = hosts[1]

                if (TextUtils.isEmpty(pluginName) || TextUtils.isEmpty(methodName)) {
                    return false
                }
                if (!mPluginMap.containsKey(pluginName)) {
                    return false
                }
                getPlugin(hybrid, pluginName)?.supportMethodNames()?.let { methods ->
                    return methods.contains(methodName)
                }
                return false
            }
        }
        return false
    }

    private fun getPluginClass(pluginName: String): Class<out WVApiPlugin>? {
        if (mPluginMap.containsKey(pluginName)) {
            return mPluginMap[pluginName]
        }
        return null
    }

    private fun getPlugin(hybrid: IHybrid?, pluginName: String): WVApiPlugin? {
        var hybridMap: HashMap<String, WVApiPlugin>? = null
        if (mPluginCacheMap.containsKey(hybrid)) {
            mPluginCacheMap[hybrid]?.let {
                hybridMap = it
                if (it.containsKey(pluginName)) {
                    return it[pluginName]
                }
            }
        }
        val pluginClass: Class<out WVApiPlugin>? = getPluginClass(pluginName)
        pluginClass?.let {
            val plugin: WVApiPlugin = pluginClass.newInstance()
            plugin.initialize(pluginName, hybrid)

            if (hybridMap == null) {
                hybridMap = HashMap()
            }
            hybridMap!![pluginName] = plugin
            mPluginCacheMap[hybrid] = hybridMap
            return plugin
        }
        return null
    }

    fun shouldOverrideUrlLoading(hybrid: IHybrid?, host: String?, params: String, callbackId: String): Boolean {
        try {
            host?.let {
                if (it.contains(".")) {
                    val hosts = it.split(".")
                    val pluginName = hosts[0]
                    val methodName = hosts[1]

                    val plugin: WVApiPlugin? = getPlugin(hybrid, pluginName)
                    plugin?.let {
                        return executePlugin(hybrid, plugin, methodName, params, callbackId)
                    }
                    sendHybridModuleNotExistCallback(hybrid?.getWebView(), callbackId)
                    return false
                }
            }
        } catch (e: Exception) {
        }
        return false
    }

    private fun executePlugin(hybrid: IHybrid?, plugin: WVApiPlugin, methodName: String, params: String, callbackId: String): Boolean {
        var wvHashCode = 0
        hybrid?.getWebView()?.let {
            wvHashCode = it.hashCode()
        }

        val wvCallback = WVCallbackContext()
        wvCallback.webViewHashCode = wvHashCode
        wvCallback.methodName = methodName
        wvCallback.params = params
        wvCallback.callbackId = callbackId

        return plugin.execute(hybrid, methodName, params, wvCallback)
    }

    fun onNewIntent(hybrid: IHybrid?, intent: Intent?) {
        if (mPluginCacheMap.containsKey(hybrid)) {
            mPluginCacheMap[hybrid]?.let {
                for (plugin in it.values) {
                    plugin.onNewIntent(intent)
                }
            }
        }
    }

    fun setUserVisibleHint(hybrid: IHybrid?, isVisibleToUser: Boolean) {
        onPageDisplayState(hybrid?.getWebView(), isVisibleToUser)
    }

    fun onStart(hybrid: IHybrid?) {
        if (mPluginCacheMap.containsKey(hybrid)) {
            mPluginCacheMap[hybrid]?.let {
                for (plugin in it.values) {
                    plugin.onStart()
                }
            }
        }
    }

    fun onResume(hybrid: IHybrid?) {
        onPageDisplayState(hybrid?.getWebView(), true)
        if (mPluginCacheMap.containsKey(hybrid)) {
            mPluginCacheMap[hybrid]?.let {
                for (plugin in it.values) {
                    plugin.onResume()
                }
            }
        }
    }

    fun onPause(hybrid: IHybrid?) {
        if (mPluginCacheMap.containsKey(hybrid)) {
            mPluginCacheMap[hybrid]?.let {
                for (plugin in it.values) {
                    plugin.onPause()
                }
            }
        }
    }

    fun onStop(hybrid: IHybrid?) {
        onPageDisplayState(hybrid?.getWebView(), false)
        if (mPluginCacheMap.containsKey(hybrid)) {
            mPluginCacheMap[hybrid]?.let {
                for (plugin in it.values) {
                    plugin.onStop()
                }
            }
        }
    }

    fun onDestroy(hybrid: IHybrid?) {
        if (mPluginCacheMap.containsKey(hybrid)) {
            mPluginCacheMap[hybrid]?.let {
                for (plugin in it.values) {
                    plugin.onDestroy()
                }
            }
            mPluginCacheMap.remove(hybrid)
        }
    }

    fun onActivityResult(hybrid: IHybrid?, requestCode: Int, resultCode: Int, intent: Intent?) {
        if (mPluginCacheMap.containsKey(hybrid)) {
            mPluginCacheMap[hybrid]?.let {
                for (plugin in it.values) {
                    plugin.onActivityResult(requestCode, resultCode, intent)
                }
            }
        }
    }

    private fun onPageDisplayState(webview: WebView?, isShow: Boolean) {
        sendHybridEvent(webview, if (isShow) "show" else "hide", null, null)
    }

    fun sendHybridCallback(webview: WebView?, callbackId: String?, callbackEntity: WVHybridCallbackEntity?) {
        callbackEntity?.let {
            val jsonObject = JSONObject()
            jsonObject.put("code", callbackEntity.code)
            jsonObject.put("message", callbackEntity.message)
            jsonObject.put("data", callbackEntity.data)
            sendHybridStatus(webview, callbackId, jsonObject)
        }
    }

    fun sendHybridStatus(webview: WebView?, callbackId: String?, data: Any?) {
        if (!TextUtils.isEmpty(callbackId)) {
            val script = "window.medhybrid.__callback__.exec('$callbackId',$data);"
            MedHybrid.log("script = $script")
            webview?.let { it ->
                it.evaluateJavascript(script) {
                    MedHybrid.log("onReceiveValue = $it")
                }
            }
        }
    }

    fun sendHybridEvent(webview: WebView?, eventName: String, moduleName: String?, data: Any?) {
        val script = "window.medhybrid && window.medhybrid.__events__.trigger('$eventName', '$moduleName', '$data');"
        MedHybrid.log("script = $script")
        webview?.let { it ->
            it.evaluateJavascript(script) {
                MedHybrid.log("onReceiveValue = $it")
            }
        }
    }

    /**
     * 发送方法不存在
     */
    fun sendHybridMethodNotExistCallback(webview: WebView?, callbackId: String?) {
        sendHybridCallback(webview, callbackId, WVHybridCallbackEntity(BridgeErrorCode.METHOD_NOT_EXIST, "method not exist"))
    }

    /**
     * 模块不存在
     */
    private fun sendHybridModuleNotExistCallback(webview: WebView?, callbackId: String?) {
        sendHybridCallback(webview, callbackId, WVHybridCallbackEntity(BridgeErrorCode.MODULE_NOT_EXIST, "module not exist"))
    }
}