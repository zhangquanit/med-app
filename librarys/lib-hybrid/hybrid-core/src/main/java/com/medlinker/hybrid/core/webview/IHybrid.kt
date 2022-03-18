package com.medlinker.hybrid.core.webview

import android.content.Context
import android.view.View
import com.medlinker.hybrid.core.plugin.ui.entity.RefreshStatusTextEntity
import com.medlinker.hybrid.core.ui.widget.IWVNavigationView
import com.tencent.smtt.sdk.WebView


/**
 * @author hmy
 * @time 3/22/21 15:18
 */
interface IHybrid {

    fun getCurContext(): Context?
    fun getWebView(): WebView
    fun getNavigationView(): IWVNavigationView
    fun getNavigationHeader(): View
    fun onBackPressed(): Boolean

    /**
     * 开启页面下拉刷新，如果当前页面发生了跳转，容器需要自动关闭下拉刷新功能。
     */
    fun enablePullRefresh(statusTextEntity: RefreshStatusTextEntity, onRefreshListener: () -> Unit)
    fun disablePullRefresh()
    fun startPullRefresh()
    fun stopPullRefresh(success: Boolean)
}