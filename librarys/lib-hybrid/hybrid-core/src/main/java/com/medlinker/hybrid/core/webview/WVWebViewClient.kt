package com.medlinker.hybrid.core.webview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.medlinker.bridge.BridgeCallback
import com.medlinker.bridge.BridgeManager
import com.medlinker.hybrid.core.MedHybrid
import com.medlinker.hybrid.core.jsbridge.WVPluginManager
import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import java.util.*


/**
 * @author hmy
 * @time 3/12/21 14:29
 */
class WVWebViewClient(client: IWebViewClient, hybrid: IHybrid) : WebViewClient() {

    private var mIWebViewClient: IWebViewClient? = client
    private var mIHybrid: IHybrid? = hybrid

    @Volatile
    private var isPaySuccess = false

    companion object {
        const val SCHEME_DEFAULT = "medlinkerhybrid"

        fun shouldOverrideUrlLoading(parse: Uri, hybrid: IHybrid?): Boolean {
            val host = parse.host
            val params = parse.getQueryParameter("params").toString()
            val callback = parse.getQueryParameter("callback").toString()
            return WVPluginManager.shouldOverrideUrlLoading(hybrid, host, params, callback)
        }
    }

    override fun shouldInterceptRequest(webView: WebView?, url: String?): WebResourceResponse? {
        MedHybrid.getConfig()?.getWebViewAdapter()?.let {
            return it.shouldInterceptRequest(webView, url)
        }
        return super.shouldInterceptRequest(webView, url)
    }

    override fun shouldOverrideUrlLoading(webView: WebView, url: String?): Boolean {
        MedHybrid.log("shouldOverrideUrlLoading = $url")
        val parse = Uri.parse(url)

        if (SCHEME_DEFAULT == parse.scheme) {
            return shouldOverrideUrlLoading(parse, mIHybrid)
        }
        url?.let {
            if (url.startsWith("tel:")) {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                val context: Context = webView.context
                if (context !is Activity) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                return true
            } else if (url.startsWith("alipays:") || url.startsWith("alipay")) {
                if (isPaySuccess) {
                    val payBeforeIndex: Int = webView.copyBackForwardList().currentIndex
                    if (payBeforeIndex > 1) {
                        webView.goBackOrForward(-payBeforeIndex)
                    }
                    isPaySuccess = false
                    return true
                }
                try {
                    val activity = webView.context as Activity
                    activity.startActivity(Intent("android.intent.action.VIEW", Uri.parse(url)))
                    isPaySuccess = true
                } catch (e: Exception) {
                    isPaySuccess = false
                    BridgeManager.getPayBridge()?.payInterceptorWithUrlByAliPay(url, object : BridgeCallback<Void> {
                        override fun onCallback(data: Void?) {
                            webView.loadUrl(url)
                        }

                    })
                }
                return true
            } else if (url.startsWith("weixin://wap/pay")) {
                try {
                    val activity = webView.context as Activity
                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    intent.data = Uri.parse(url)
                    activity.startActivity(intent)
                } catch (e: java.lang.Exception) {
                    MedHybrid.getConfig()?.getUIBridge()?.showToast("无法调起微信支付")
                    e.printStackTrace()
                }
                return true
            } else if (url.startsWith("https://wx.tenpay.com/cgi-bin/mmpayweb-bin/checkmweb")) {
                val header: MutableMap<String, String> = HashMap()
                BridgeManager.getPayBridge()?.getWeiXinPayReferer()?.let {
                    header["Referer"] = it
                }
                webView.loadUrl(url, header)
                return false
            }
        }
        MedHybrid.getConfig()?.getWebViewAdapter()?.let {
            return it.shouldOverrideUrlLoading(webView, url)
        }
        return super.shouldOverrideUrlLoading(webView, url)
    }

    override fun onPageFinished(webView: WebView?, url: String?) {
        super.onPageFinished(webView, url)
        mIWebViewClient?.onPageFinished(webView, url)
    }

    interface IWebViewClient {
        fun onPageFinished(webView: WebView?, url: String?)
    }
}