package com.medlinker.protocol

import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.BarUtils
import com.medlinker.lib.utils.MedAppInfo
import com.medlinker.lib.utils.MedImmersiveModeUtil
import kotlinx.android.synthetic.main.activity_web_privacy.*
import net.medlinker.base.router.RouterUtil

class PrivacyWebActivity : AppCompatActivity() {
    private var webView: WebView? = null
    var progressBar: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_privacy)

        MedImmersiveModeUtil.setStatusBarTransparent(this)
        MedImmersiveModeUtil.setStatusBarDarkMode(this, true)
        BarUtils.addMarginTopEqualStatusBarHeight(titlebar)

        val url = intent.getStringExtra(RouterUtil.DATA_KEY)
        if (TextUtils.isEmpty(url)) {
            finish()
            return
        }

        hybrid_normal_back.setOnClickListener {
            onBackPressed()
        }
        progressBar = hybrid_progressbar
        webView = privacy_web_view

        val settings = webView?.settings
        settings?.javaScriptEnabled = true
        settings?.setAppCacheEnabled(true)
        settings?.allowFileAccess = true
        settings?.setSupportZoom(true)
        settings?.builtInZoomControls = true
        settings?.useWideViewPort = true
        settings?.setSupportMultipleWindows(false)
        settings?.loadWithOverviewMode = true
        settings?.databaseEnabled = true
        settings?.domStorageEnabled = true

        val clientName = PrivacyManager.INSTANCE.getClientName()
        val useragent =
            settings?.userAgentString + " ${clientName}/" + MedAppInfo.versionName + "(med_hybrid_${clientName}_" + MedAppInfo.versionName + ")"
        settings?.userAgentString = useragent
        webView?.webViewClient = PrivacyWebiewClient()
        webView?.webChromeClient = PrivacyWebChromeClient()
        settings?.javaScriptCanOpenWindowsAutomatically = true
        webView?.loadUrl(url)
    }

    override fun onResume() {
        super.onResume()
        webView!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        webView!!.onPause()
    }

    override fun onDestroy() {
        if (webView != null) {
            webView!!.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            webView!!.clearHistory()
            (webView!!.parent as ViewGroup).removeView(webView)
            webView!!.destroy()
            webView = null
        }
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (webView!!.canGoBack()) {
            webView!!.goBack()
        } else {
            super.onBackPressed()
        }
    }

    private inner class PrivacyWebiewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            return if (overrideUrlLoading(request.url)) true else super.shouldOverrideUrlLoading(
                view,
                request
            )
        }
    }

    private inner class PrivacyWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            if (newProgress == 100) {
                progressBar!!.visibility = View.GONE
            } else {
                if (progressBar!!.visibility == View.GONE) {
                    progressBar!!.visibility = View.VISIBLE
                }
                progressBar!!.progress = newProgress
            }
            super.onProgressChanged(view, newProgress)
        }

        override fun onReceivedTitle(view: WebView?, title: String?) {
            super.onReceivedTitle(view, title)
            hybrid_normal_title.text = title
        }
    }

    private fun overrideUrlLoading(uri: Uri): Boolean {
        if (TextUtils.equals("medmedlinkerhybrid", uri.scheme)) {
            if (TextUtils.equals("back", uri.host)) {
                finish()
            }
            return true
        }
        return false
    }
}