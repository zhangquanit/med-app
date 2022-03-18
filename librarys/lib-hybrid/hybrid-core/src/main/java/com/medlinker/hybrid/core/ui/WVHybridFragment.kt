package com.medlinker.hybrid.core.ui

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.medlinker.bridge.BridgeRequestCode
import com.medlinker.hybrid.R
import com.medlinker.hybrid.core.MedHybrid
import com.medlinker.hybrid.core.jsbridge.WVPluginManager
import com.medlinker.hybrid.core.plugin.ui.entity.RefreshStatusTextEntity
import com.medlinker.hybrid.core.ui.widget.IWVNavigationView
import com.medlinker.hybrid.core.ui.widget.WVNavigationView
import com.medlinker.hybrid.core.utils.WVUtils
import com.medlinker.hybrid.core.webview.IHybrid
import com.medlinker.hybrid.core.webview.WVWebViewClient
import com.medlinker.widget.smart.refresh.header.medlinker.MedlinkerHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.tencent.smtt.sdk.*
import java.io.File

/**
 * @author hmy
 * @time 3/9/21 14:38
 */
open class WVHybridFragment : Fragment(), IHybrid {

    private lateinit var mWebViewContainer: FrameLayout
    protected lateinit var mProgressBar: ProgressBar
    protected lateinit var mWebView: WebView
    protected lateinit var mNavigationView: WVNavigationView
    protected lateinit var mRefreshLayout: SmartRefreshLayout
    private lateinit var mMedlinkerHeader: MedlinkerHeader
    protected lateinit var mNavigationHeader: View

    private var mWebChromeClient: HybridWebChromeClient? = null
    private var mUrl = ""

    private var mUploadMessage: ValueCallback<Uri>? = null
    private var uploadMessageAboveL: ValueCallback<Array<Uri>>? = null

    companion object {
        fun newInstance(url: String): WVHybridFragment {
            val args = Bundle()
            val fragment = WVHybridFragment()
            args.putString("URL", url)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mUrl = it.getString("URL").toString()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.hybrid_fragment_webview, null)
        mNavigationView = view.findViewById(R.id.hybrid_navigation)
        mWebViewContainer = view.findViewById(R.id.hybrid_web_view_container)
        mProgressBar = view.findViewById(R.id.hybrid_progressbar)
        mWebView = view.findViewById(R.id.hybrid_web_view)
        mRefreshLayout = view.findViewById(R.id.hybrid_layout_refresh)
        mNavigationHeader = view.findViewById(R.id.hybrid_header)
        mMedlinkerHeader = MedlinkerHeader(view.context)
        mRefreshLayout.setRefreshHeader(mMedlinkerHeader)
        mRefreshLayout.setHeaderHeight(45f)
        mRefreshLayout.setEnableRefresh(false)

        initWebViewConfig(mWebView)
        initView()
        return view
    }

    private fun initView() {
        val navigationBarHeight = WVUtils.dpToPx(context, MedHybrid.geNavigationConfig().heightDp)
        if (navigationBarHeight > 0) {
            val layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, navigationBarHeight)
            mNavigationView.layoutParams = layoutParams
            mNavigationView.requestLayout()
        }
        mNavigationView.setCloseIconOnClickListener(View.OnClickListener { activity?.finish() })
        initNavigation(mNavigationView)
    }

    open fun initNavigation(navigationView: WVNavigationView) {
        navigationView.appendNavigation(IWVNavigationView.Direct.LEFT, "", R.mipmap.hybrid_ic_naviback_gray, View.OnClickListener {
            if (!onBackPressed()) {
                activity?.finish()
            }
        })?.setVisibility(View.VISIBLE)
        navigationView.setTitle(MedHybrid.geNavigationConfig().title)
    }

    protected open fun initWebViewConfig(webView: WebView) {
        val settings: WebSettings = webView.settings

        settings.setAppCacheEnabled(true)
        if (WVUtils.isConnectedNetwork(webView.context.applicationContext)) {
            settings.cacheMode = WebSettings.LOAD_DEFAULT
        } else {
            settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        }
        settings.cacheMode = if (WVUtils.isConnectedNetwork(webView.context.applicationContext)) WebSettings.LOAD_DEFAULT else WebSettings.LOAD_CACHE_ELSE_NETWORK
        settings.allowFileAccess = true
        settings.layoutAlgorithm = if (WVUtils.hasKitkat()) WebSettings.LayoutAlgorithm.NORMAL else WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
        settings.useWideViewPort = true
        settings.setSupportMultipleWindows(false)
        settings.loadWithOverviewMode = true
        settings.databaseEnabled = true
        settings.domStorageEnabled = true
        settings.javaScriptEnabled = true
        settings.setGeolocationEnabled(true)
        settings.setAppCacheMaxSize(Long.MAX_VALUE)
        settings.setAppCachePath(File(webView.context.cacheDir, "wb_appcache").absolutePath)
        settings.databasePath = File(webView.context.cacheDir, "wb_databases").absolutePath
        settings.setGeolocationDatabasePath(File(webView.context.cacheDir, "wb_geolocation").absolutePath)
        settings.pluginState = WebSettings.PluginState.ON_DEMAND
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
        settings.mediaPlaybackRequiresUserGesture = false
        settings.userAgentString = settings.userAgentString + MedHybrid.getUserAgentString()

        webView.webViewClient = getWebViewClient()
        mWebChromeClient = HybridWebChromeClient()
        webView.webChromeClient = mWebChromeClient
        settings.javaScriptCanOpenWindowsAutomatically = true
        MedHybrid.getConfig()?.getWebViewAdapter()?.addJavascriptInterface(webView)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //方便webview在choreme调试
            WebView.setWebContentsDebuggingEnabled(true)
        }
        //Http和Https混合问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
    }

    private fun getWebViewClient(): WebViewClient? {
        return WVWebViewClient(object : WVWebViewClient.IWebViewClient {
            override fun onPageFinished(webView: WebView?, url: String?) {
                webView?.let {
                    mNavigationView.setCloseIconVisible(it.canGoBack())
                }
            }

        }, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUrl()
    }

    open fun loadUrl() {
        if (!TextUtils.isEmpty(mUrl)) {
            mWebView.loadUrl(mUrl)
        }
    }

    inner class HybridWebChromeClient : WebChromeClient() {

        override fun onProgressChanged(webView: WebView?, newProgress: Int) {
            super.onProgressChanged(webView, newProgress)
            val showProgress = newProgress < 100
            mProgressBar.visibility = if (showProgress) View.VISIBLE else View.GONE
            mProgressBar.progress = newProgress
        }

        override fun onReceivedTitle(webView: WebView?, title: String?) {
            super.onReceivedTitle(webView, title)
            mNavigationView.updateTitle(title, null, null) //TODO 医联app自有h5走协议，外部h5才调
        }

        //For Android 4.1
        override fun openFileChooser(uploadMsg: ValueCallback<Uri>?, acceptType: String?, capture: String?) {
            activity?.runOnUiThread {
                mUploadMessage = uploadMsg
                picker()
            }
        }

        override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri>>?, fileChooserParams: FileChooserParams?): Boolean {
            activity?.runOnUiThread {
                uploadMessageAboveL = filePathCallback
                picker()
            }
            return true
        }

        private fun picker() {
            val intent = Intent(Intent.ACTION_GET_CONTENT) //ACTION_OPEN_DOCUMENT
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                startActivityForResult(intent, BridgeRequestCode.REQUEST_CODE_H5_FILE_CHOOSER)
            } else {
                startActivityForResult(Intent.createChooser(intent, "图片选择"), BridgeRequestCode.REQUEST_CODE_H5_FILE_CHOOSER)
            }
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        WVPluginManager.setUserVisibleHint(this, isVisibleToUser)
    }

    override fun onStart() {
        super.onStart()
        WVPluginManager.onStart(this)
    }

    override fun onResume() {
        super.onResume()
        mWebView.onResume()
        WVPluginManager.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        mWebView.onPause()
        WVPluginManager.onPause(this)
    }

    override fun onStop() {
        super.onStop()
        WVPluginManager.onStop(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        WVPluginManager.onDestroy(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mWebView.removeAllViews()
        (mWebView.parent as ViewGroup).removeView(mWebView)
        mWebView.resumeTimers()
        mWebView.destroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        WVPluginManager.onActivityResult(this, requestCode, resultCode, intent)

        if (requestCode == BridgeRequestCode.REQUEST_CODE_H5_FILE_CHOOSER) {
            if (null == mUploadMessage && null == uploadMessageAboveL) {
                return
            }
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(resultCode, intent)
            } else if (mUploadMessage != null) {
                val result = if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data
                mUploadMessage!!.onReceiveValue(result)
                mUploadMessage = null
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun onActivityResultAboveL(resultCode: Int, intent: Intent?): Unit {
        if (uploadMessageAboveL == null) return
        var results: Array<Uri?>? = null
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                val dataString = intent.dataString
                val clipData = intent.clipData
                if (clipData != null) {
                    results = arrayOfNulls(clipData.itemCount)
                    for (i in 0 until clipData.itemCount) {
                        val item = clipData.getItemAt(i)
                        results[i] = item.uri
                    }
                }
                if (dataString != null) results = arrayOf(Uri.parse(dataString))
            }
        }
        results?.let {
            val array: Array<Uri> = Array(results.size) {
                results[it]!!
            }
            uploadMessageAboveL!!.onReceiveValue(array)
        }
        uploadMessageAboveL = null
    }

    override fun onBackPressed(): Boolean {
        var result = false
        if (mWebView.canGoBack()) {
            mWebView.goBack()
            result = true
        }
        return result
    }

    override fun enablePullRefresh(status: RefreshStatusTextEntity, onRefreshListener: () -> Unit) {
        mRefreshLayout.setEnableRefresh(true)
        mRefreshLayout.setOnRefreshListener {
            onRefreshListener.invoke()
        }
        mMedlinkerHeader.setStateText(status.pullText, status.releaseText, status.loadingText,
                status.successText, status.errorText)
        mMedlinkerHeader.hideText(false)
    }

    override fun disablePullRefresh() {
        mRefreshLayout.setEnableRefresh(false)
    }

    override fun startPullRefresh() {
        mRefreshLayout.autoRefresh()
    }

    override fun stopPullRefresh(success: Boolean) {
        mRefreshLayout.finishRefresh()
    }

    override fun getCurContext(): Context? {
        return context
    }

    override fun getWebView(): WebView {
        return mWebView
    }

    override fun getNavigationView(): IWVNavigationView {
        return mNavigationView
    }

    override fun getNavigationHeader(): View {
        return mNavigationHeader
    }
}