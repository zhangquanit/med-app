package net.medlinker.main.hybrid.plugin

import android.app.Activity
import android.text.TextUtils
import com.google.gson.JsonArray
import com.medlinker.hybrid.core.jsbridge.WVApiPlugin
import com.medlinker.hybrid.core.jsbridge.WVCallbackContext
import com.medlinker.hybrid.core.jsbridge.entity.WVHybridCallbackEntity
import com.medlinker.hybrid.core.webview.IHybrid
import com.medlinker.lib.pay.*
import com.medlinker.lib.utils.MedToastUtil
import org.json.JSONObject

/**
 * @description 支付插件
 * @author guojianming
 * @date 2021-11-29
 */
class PayPlugin : WVApiPlugin() {

    override fun supportMethodNames(): Array<String> {
        return arrayOf("getSupportedPayment", "wxPay", "aliPay")
    }


    override fun execute(
        hybrid: IHybrid?,
        methodName: String,
        params: String,
        callbackContext: WVCallbackContext
    ): Boolean {
        try {
            when (methodName) {
                "getSupportedPayment" -> {
                    val jsonArray = JsonArray()
                    jsonArray.add("wxPay")
//                    jsonArray.add("aliPay")
                    sendHybridCallback(
                        callbackContext.callbackId,
                        WVHybridCallbackEntity().setData(jsonArray)
                    )
                    return true
                }
                "wxPay" -> {
                    hybrid?.let {
                        val jsonObject = JSONObject(params)
                        val payInfo: String = jsonObject.optString("payInfo")
                        if (!TextUtils.isEmpty(payInfo)) {
                            var req = WxPayReq.Builder()
                                .with(it.getCurContext() as Activity)
                                .setPayInfo(payInfo)
                                .create()
                                .setOnWechatPayCallback(object : WxPayer.IPayCallback {
                                    override fun onFailure(errCode: Int, errStr: String?) {
                                        val jsonObject = JSONObject()
                                        jsonObject.put("errCode", errCode)
                                        jsonObject.put("errStr", errStr)

                                        sendHybridCallback(
                                            callbackContext.callbackId,
                                            WVHybridCallbackEntity().setData(
                                                jsonObject
                                            )
                                        )
                                    }

                                    override fun onSuccess(errCode: Int, errStr: String?) {
                                        val jsonObject = JSONObject()
                                        jsonObject.put("errCode", errCode)
                                        jsonObject.put("errStr", errStr)

                                        sendHybridCallback(
                                            callbackContext.callbackId,
                                            WVHybridCallbackEntity().setData(
                                                jsonObject
                                            )
                                        )
                                    }

                                })
                            PayAPI.getInstance().sendPayRequest(req)
                        } else {
                            MedToastUtil.showMessage("微信支付参数不可为空")
                        }
                    }
                    return true
                }
                "aliPay" -> {
                    hybrid?.let {
                        val jsonObject = JSONObject(params)
                        val payInfo: String = jsonObject.optString("payInfo")
                        if (!TextUtils.isEmpty(payInfo)) {
                            var req = AliPayReq.Builder()
                                .with(it.getCurContext() as Activity)
                                .setPayInfo(payInfo)
                                .create()
                                .setOnAliPayCallback(object : AliPayer.IPayCallBack {
                                    override fun onFailure(errStr: String?, resultStatus: String?) {
                                        val jsonObject = JSONObject()
                                        jsonObject.put("errStr", errStr)
                                        jsonObject.put("resultStatus", resultStatus)

                                        sendHybridCallback(
                                            callbackContext.callbackId,
                                            WVHybridCallbackEntity().setData(
                                                jsonObject
                                            )
                                        )
                                    }

                                    override fun onPaySuccess(
                                        resultInfo: String?,
                                        memo: String?,
                                        resultStatus: String?
                                    ) {
                                        val jsonObject = JSONObject()
                                        jsonObject.put("memo", memo)
                                        jsonObject.put("resultInfo", resultInfo)
                                        jsonObject.put("resultStatus", resultStatus)

                                        sendHybridCallback(
                                            callbackContext.callbackId,
                                            WVHybridCallbackEntity().setData(
                                                jsonObject
                                            )
                                        )
                                    }

                                    override fun onPayIndeterminate(
                                        resultInfo: String?,
                                        memo: String?,
                                        resultStatus: String?
                                    ) {
                                        val jsonObject = JSONObject()
                                        jsonObject.put("memo", memo)
                                        jsonObject.put("resultInfo", resultInfo)
                                        jsonObject.put("resultStatus", resultStatus)

                                        sendHybridCallback(
                                            callbackContext.callbackId,
                                            WVHybridCallbackEntity().setData(
                                                jsonObject
                                            )
                                        )
                                    }

                                })
                            PayAPI.getInstance().sendPayRequest(req)
                        } else {
                            MedToastUtil.showMessage("支付宝支付参数不可为空")
                        }
                    }
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

    override fun onDestroy() {
        PayAPI.getInstance().reset()
        super.onDestroy()
    }
}