package com.medlinker.payapp

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.medlinker.lib.pay.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PayAPI.getInstance().isDebugMode=true
    }

    fun testWechatPay(view: View) {
        var req = WxPayReq.Builder()
            .with(this)
            .setPayInfo("111")
            .create()
            .setOnWechatPayCallback(object : WxPayer.IPayCallback {
                override fun onFailure(errCode: Int, errStr: String?) {
                    toast(errStr)
                }

                override fun onSuccess(errCode: Int, errStr: String?) {
                    toast(errStr)
                }

            })
        PayAPI.getInstance().sendPayRequest(req);
    }

    fun testAliPay(view: View) {
        var req = AliPayReq.Builder()
            .with(this)
            .setPayInfo("111")
            .create()
            .setOnAliPayCallback(object : AliPayer.IPayCallBack {
                override fun onFailure(errStr: String?, resultStatus: String?) {
                    toast(errStr)
                }

                override fun onPaySuccess(
                    resultInfo: String?,
                    memo: String?,
                    resultStatus: String?
                ) {
                    toast(resultInfo)
                }

                override fun onPayIndeterminate(
                    resultInfo: String?,
                    memo: String?,
                    resultStatus: String?
                ) {
                    toast(resultInfo)
                }

            })
        PayAPI.getInstance().sendPayRequest(req)
    }

    fun toast(str: String?) {
        if (!TextUtils.isEmpty(str))
            Toast.makeText(MainActivity@ this, str, Toast.LENGTH_LONG).show()
    }

}