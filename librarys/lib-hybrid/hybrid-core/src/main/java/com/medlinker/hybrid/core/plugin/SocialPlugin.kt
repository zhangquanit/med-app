package com.medlinker.hybrid.core.plugin

import android.annotation.SuppressLint
import androidx.fragment.app.FragmentActivity
import com.medlinker.bridge.BridgeErrorCode
import com.medlinker.bridge.BridgeManager
import com.medlinker.hybrid.core.jsbridge.WVApiPlugin
import com.medlinker.hybrid.core.jsbridge.WVCallbackContext
import com.medlinker.hybrid.core.jsbridge.entity.WVHybridCallbackEntity
import com.medlinker.hybrid.core.webview.IHybrid


/**
 * @author hmy
 * @time 4/15/21 16:13
 */
class SocialPlugin : WVApiPlugin() {

    override fun supportMethodNames(): Array<String> {
        return arrayOf("share", "getContact", "sendSms", "callPhone")
    }

    override fun execute(hybrid: IHybrid?, methodName: String, params: String, callbackContext: WVCallbackContext): Boolean {
        try {
            when (methodName) {
                "share" -> {
                    hybrid?.getCurContext()?.let {
                        BridgeManager.getSocialBridge()?.share(it, params)
                    }
                    sendHybridCallbackVoid(callbackContext)
                    return true
                }
                "getContact" -> {
                    hybrid?.getCurContext()?.let { context ->
                        if (context is FragmentActivity) {
                            getContact(context, callbackContext)
                        }
                    }
                    return true
                }
                "sendSms" -> {
                    hybrid?.getCurContext()?.let {
                        if (it is FragmentActivity) {
                            sendSms(it, callbackContext, params)
                        }
                    }
                    sendHybridCallbackVoid(callbackContext)
                    return true
                }
                "callPhone" -> {
                    hybrid?.getCurContext()?.let {
                        if (it is FragmentActivity) {
                            callPhone(it, callbackContext, params)
                        }
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

    @SuppressLint("CheckResult")
    private fun callPhone(context: FragmentActivity, callbackContext: WVCallbackContext, params: String) {
        BridgeManager.getSocialBridge()?.let {
            it.callPhone(context, params) { permissionAccept, throwable ->
                if (throwable != null) {
                    sendHybridCallback(callbackContext.callbackId, WVHybridCallbackEntity(BridgeErrorCode.EXCEPTION, throwable.message.toString()))
                } else if (!permissionAccept) {
                    sendHybridCallback(callbackContext.callbackId,
                            WVHybridCallbackEntity(BridgeErrorCode.PERMISSION_REFUSE, BridgeErrorCode.PERMISSION_REFUSE_CALL_PHONE))
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun sendSms(context: FragmentActivity, callbackContext: WVCallbackContext, params: String) {
        BridgeManager.getSocialBridge()?.let {
            it.sendSms(context, params) { permissionAccept, throwable ->
                if (throwable != null) {
                    sendHybridCallback(callbackContext.callbackId, WVHybridCallbackEntity(BridgeErrorCode.EXCEPTION, throwable.message.toString()))
                } else if (!permissionAccept) {
                    sendHybridCallback(callbackContext.callbackId,
                            WVHybridCallbackEntity(BridgeErrorCode.PERMISSION_REFUSE, BridgeErrorCode.PERMISSION_REFUSE_SEND_SMS))
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun getContact(context: FragmentActivity, callbackContext: WVCallbackContext) {
        BridgeManager.getSocialBridge()?.let {
            it.getContact(context) { contacts, permissionAccept, throwable ->
                if (throwable != null) {
                    sendHybridCallback(callbackContext.callbackId, WVHybridCallbackEntity(BridgeErrorCode.EXCEPTION, throwable.message.toString()))
                } else if (!permissionAccept) {
                    sendHybridCallback(callbackContext.callbackId,
                            WVHybridCallbackEntity(BridgeErrorCode.PERMISSION_REFUSE, BridgeErrorCode.PERMISSION_REFUSE_READ_CONTACTS))
                } else {
                    sendHybridCallback(callbackContext.callbackId, WVHybridCallbackEntity().setData(contacts))
                }
            }
        }
    }
}