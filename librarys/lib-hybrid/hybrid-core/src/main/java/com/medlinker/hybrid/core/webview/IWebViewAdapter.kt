package com.medlinker.hybrid.core.webview

import android.net.Uri
import android.text.TextUtils
import com.medlinker.hybrid.core.MedHybrid
import com.tencent.smtt.export.external.interfaces.WebResourceResponse
import com.tencent.smtt.sdk.CookieManager
import com.tencent.smtt.sdk.CookieSyncManager
import com.tencent.smtt.sdk.WebView
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream


/**
 * @author hmy
 * @time 3/12/21 14:48
 */
abstract class IWebViewAdapter {

    open fun shouldInterceptRequest(webView: WebView?, url: String?): WebResourceResponse? {
        if (TextUtils.isEmpty(url)) {
            return null
        }
        val tempUrl = url?.let { urlFilter(it) }
        val uri = Uri.parse(tempUrl)
        if (TextUtils.isEmpty(uri.path)) {
            return null
        }
        webView?.let {
            val file = File(webView.context.filesDir.absolutePath.toString() + "/" + MedHybrid.getFileHybridDataPath() + uri.path)
            if (file.exists()) {
                var response: WebResourceResponse? = null
                try {
                    val localCopy: InputStream = FileInputStream(file)
                    val mimeType: String = getMimeType(tempUrl)
                    response = WebResourceResponse(mimeType, "UTF-8", localCopy)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                return response!!
            }
        }
        return null
    }

    open fun shouldOverrideUrlLoading(webView: WebView?, url: String?): Boolean {
        return false
    }

    open fun urlFilter(url: String): String {
        return url
    }

    open fun getMimeType(url: String?): String {
        return try {
            if (url != null) {
                if (url.contains(".")) {
                    val index = url.lastIndexOf(".")
                    if (index > -1) {
                        val paramIndex = url.indexOf("?")
                        val type = url.substring(index + 1, if (paramIndex == -1) url.length else paramIndex)
                        when (type) {
                            "js" -> return "text/javascript"
                            "css" -> return "text/css"
                            "html" -> return "text/html"
                            "png" -> return "image/png"
                            "jpg" -> return "image/jpg"
                            "gif" -> return "image/gif"
                            else -> {
                            }
                        }
                    }
                }
            }
            "text/plain"
        } catch (e: Exception) {
            e.printStackTrace()
            "text/plain"
        }
    }

    open fun addJavascriptInterface(webView: WebView) {}

    open fun syncCookieIntercept(cookieSyncManager: CookieSyncManager, cookieManager: CookieManager): Boolean = false

    abstract fun getCookieParams(): Array<String>?

    abstract fun log(msg: String?, tr: Throwable?)
}