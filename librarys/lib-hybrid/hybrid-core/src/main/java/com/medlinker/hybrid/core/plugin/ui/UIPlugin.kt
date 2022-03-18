package com.medlinker.hybrid.core.plugin.ui

import android.app.Activity
import android.text.TextUtils
import android.view.View
import com.medlinker.hybrid.R
import com.medlinker.hybrid.core.MedHybrid
import com.medlinker.hybrid.core.jsbridge.WVApiPlugin
import com.medlinker.hybrid.core.jsbridge.WVCallbackContext
import com.medlinker.hybrid.core.jsbridge.entity.WVHybridCallbackEntity
import com.medlinker.hybrid.core.plugin.ui.entity.*
import com.medlinker.hybrid.core.ui.widget.IWVNavigationView
import com.medlinker.hybrid.core.utils.WVUtils
import com.medlinker.hybrid.core.webview.IHybrid
import org.json.JSONArray
import org.json.JSONObject


/**
 * @author hmy
 * @time 3/22/21 11:18
 */
class UIPlugin : WVApiPlugin() {

    override fun supportMethodNames(): Array<String> {
        return arrayOf("getConstants", "resetHeader", "setTitle", "setHeaderBackground", "setBack",
                "setMenus", "enablePullRefresh", "disablePullRefresh", "stopPullRefresh",
                "startPullRefresh", "setImmersed", "isSupportImmersed", "showToast", "dialog")
    }

    override fun execute(hybrid: IHybrid?, methodName: String, params: String, callbackContext: WVCallbackContext): Boolean {
        try {
            when (methodName) {
                "getConstants" -> {
                    hybrid?.let {
                        val jsonObject = JSONObject()
                        jsonObject.put("statusbarHeight", it.getCurContext()?.let { it1 ->
                            WVUtils.pxToDp(it.getCurContext(),
                                    WVUtils.getStatusBarHeight(it1).toFloat())
                        })
                        jsonObject.put("headerHeight", WVUtils.pxToDp(it.getCurContext(), it.getNavigationView().getHeight().toFloat()))
                        sendHybridCallback(callbackContext.callbackId, WVHybridCallbackEntity().setData(jsonObject))
                    }
                    return true
                }
                "resetHeader" -> {
                    hybrid?.getNavigationView()?.let { navigationView ->
                        navigationView.reset()
                        navigationView.appendNavigation(IWVNavigationView.Direct.LEFT, "",
                                R.mipmap.hybrid_ic_naviback_gray, View.OnClickListener {
                            if (!hybrid.onBackPressed() && it.context is Activity) {
                                (it.context as Activity).finish()
                            }
                        })?.setVisibility(View.VISIBLE)
                        navigationView.setTitle(MedHybrid.geNavigationConfig().title)
                    }
                    sendHybridCallbackVoid(callbackContext)
                    return true
                }
                "setTitle" -> {
                    hybrid?.getNavigationView()?.let {
                        val jsonObject = JSONObject(params)
                        val title = jsonObject.optString("title", MedHybrid.geNavigationConfig().title)
                        val titleColor = jsonObject.optString("titleColor")
                        val titleSize = jsonObject.optString("titleSize")
                        val subtitle = jsonObject.optString("subtitle")
                        val subtitleColor = jsonObject.optString("subtitleColor")
                        val subtitleSize = jsonObject.optString("subtitleSize")

                        val titleSizeValue = if (TextUtils.isEmpty(titleSize)) MedHybrid.geNavigationConfig().titleSize else titleSize.toFloat()
                        var titleColorValue = WVUtils.parseColor(titleColor)
                        titleColorValue = titleColorValue
                                ?: MedHybrid.geNavigationConfig().titleColor
                        it.updateTitle(title, titleSizeValue, titleColorValue)

                        val subtitleSizeValue = if (TextUtils.isEmpty(subtitleSize)) MedHybrid.geNavigationConfig().subTitleSize else subtitleSize.toFloat()
                        var subtitleColorValue = WVUtils.parseColor(subtitleColor)
                        subtitleColorValue = subtitleColorValue
                                ?: MedHybrid.geNavigationConfig().subTitleColor
                        it.updateSubTitle(subtitle, subtitleSizeValue, subtitleColorValue)
                    }
                    sendHybridCallbackVoid(callbackContext)
                    return true
                }
                "setHeaderBackground" -> {
                    val jsonObject = JSONObject(params)
                    val color = jsonObject.optString("color")
                    WVUtils.parseColor(color)?.let { hybrid?.getNavigationView()?.setBackgroundColor(it) }
                    sendHybridCallbackVoid(callbackContext)
                    return true
                }
                "setBack" -> {
                    hybrid?.getNavigationView()?.let { navigationView ->
                        val jsonObject = JSONObject(params)
                        var icon = jsonObject.optString("icon")
                        val title = jsonObject.optString("title")
                        val titleColor = jsonObject.optString("titleColor")
                        val titleSize = jsonObject.optString("titleSize")
                        val onClick = jsonObject.optString("onClick")

                        val titleSizeValue = if (TextUtils.isEmpty(titleSize)) MedHybrid.geNavigationConfig().titleSize else titleSize.toFloat()
                        var titleColorValue = WVUtils.parseColor(titleColor)
                        titleColorValue = titleColorValue
                                ?: MedHybrid.geNavigationConfig().titleColor
                        navigationView.setCloseIconVisible(false)
                        navigationView.cleanNavigationLeft()
                        var iconUrl: Any? = icon
                        if (TextUtils.isEmpty(icon) && TextUtils.isEmpty(title)) {
                            iconUrl = R.mipmap.hybrid_ic_naviback_gray
                        }
                        navigationView.appendInner(IWVNavigationView.Direct.LEFT, title, titleSizeValue, titleColorValue, iconUrl, View.OnClickListener {
                            if (TextUtils.isEmpty(onClick)) {
                                if (!hybrid.onBackPressed() && it.context is Activity) {
                                    (it.context as Activity).finish()
                                }
                            } else {
                                sendHybridCallback(onClick, WVHybridCallbackEntity())
                            }

                        })?.setVisibility(View.VISIBLE)
                    }
                    return true
                }
                "setMenus" -> {
                    setMenus(hybrid, params, callbackContext)
                    sendHybridCallbackVoid(callbackContext)
                    return true
                }
                "enablePullRefresh" -> {
                    hybrid?.let {
                        val jsonObject = JSONObject(params)
                        val pullText = jsonObject.optString("pullText")
                        val releaseText = jsonObject.optString("releaseText")
                        val loadingText = jsonObject.optString("loadingText")
                        val successText = jsonObject.optString("successText")
                        val errorText = jsonObject.optString("errorText")
                        it.enablePullRefresh(RefreshStatusTextEntity(pullText, releaseText,
                                loadingText, successText, errorText)) {
                            sendHybridEvent("pullrefresh", mPluginName, null)
                        }
                    }
                    sendHybridCallbackVoid(callbackContext)
                    return true
                }
                "disablePullRefresh" -> {
                    hybrid?.disablePullRefresh()
                    sendHybridCallbackVoid(callbackContext)
                    return true
                }
                "stopPullRefresh" -> {
                    hybrid?.let {
                        val jsonObject = JSONObject(params)
                        val success = jsonObject.optBoolean("success")
                        it.stopPullRefresh(success)
                    }
                    sendHybridCallbackVoid(callbackContext)
                    return true
                }
                "startPullRefresh" -> {
                    hybrid?.startPullRefresh()
                    sendHybridCallbackVoid(callbackContext)
                    return true
                }
                "setImmersed" -> {
                    hybrid?.let {
                        val jsonObject = JSONObject(params)
                        val enable = jsonObject.optBoolean("enable")
                        it.getNavigationHeader().visibility = if (enable) View.GONE else View.VISIBLE
                        if (it.getCurContext() is Activity) {
                            MedHybrid.getConfig()?.getUIBridge()?.setImmersed(it.getCurContext() as Activity, enable)
                        }
                    }
                    sendHybridCallbackVoid(callbackContext)
                    return true
                }
                "isSupportImmersed" -> {
                    MedHybrid.getConfig()?.getUIBridge()?.let {
                        sendHybridCallback(callbackContext.callbackId, WVHybridCallbackEntity().setData(it.isSupportImmersed()))
                    }
                    return true
                }
                "showToast" -> {
                    MedHybrid.getConfig()?.getUIBridge()?.let {
                        val jsonObject = JSONObject(params)
                        val message = jsonObject.optString("message")
                        it.showToast(message)
                    }
                    sendHybridCallbackVoid(callbackContext)
                    return true
                }
                "dialog" -> {
                    showDialog(hybrid, params, callbackContext)
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

    private fun setMenus(hybrid: IHybrid?, params: String, callbackContext: WVCallbackContext) {
        hybrid?.getNavigationView()?.let {
            it.cleanNavigationRight()
            val jsonArray = JSONObject(params).optJSONArray("menus") ?: return
            val menus = ArrayList<MenuEntity>()
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.get(i) as JSONObject
                menus.add(MenuEntity().toObject(jsonObject))
            }
            for (menu in menus) {
                it.appendInner(IWVNavigationView.Direct.RIGHT, menu.title, menu.titleSize,
                        menu.titleColor, menu.icon, View.OnClickListener { view ->
                    when (menu.type) {
                        "dropdown" -> {
                            MedHybrid.getConfig()?.getUIBridge()?.showDropDownMenu(hybrid.getCurContext(), view, menu.dropdownItems) { position ->
                                sendHybridCallback(menu.dropdownItems[position].onClick, WVHybridCallbackEntity())
                            }
                        }
                        "button" -> {
                            sendHybridCallback(menu.onClick, WVHybridCallbackEntity())
                        }
                    }
                })
            }
        }
    }

    private fun showDialog(hybrid: IHybrid?, params: String, callbackContext: WVCallbackContext) {
        hybrid?.let {
            val jsonObject = JSONObject(params)
            val title = jsonObject.optString("title")
            val message = jsonObject.optString("message")
            val buttonsJsonArray: JSONArray? = jsonObject.optJSONArray("buttons")
            val dialogParamList = ArrayList<DialogButtonEntity>()
            buttonsJsonArray?.let {
                for (i in 0 until buttonsJsonArray.length()) {
                    val obj: JSONObject = buttonsJsonArray.get(i) as JSONObject
                    val title = obj.optString("title")
                    val type = obj.optString("type")
                    val buttonEntity = DialogButtonEntity(title, getButtonType(type))
                    dialogParamList.add(buttonEntity)
                }
            }
            val dialogParamEntity = DialogParamEntity(title, message, dialogParamList)
            MedHybrid.getConfig()?.getUIBridge()?.showDialog(it.getCurContext(), dialogParamEntity) { position ->
                sendHybridCallback(callbackContext.callbackId, WVHybridCallbackEntity().setData(position))
            }
        }
    }

    private fun getButtonType(type: String): DialogType {
        return when (type) {
            "default" -> DialogType.DEFAULT
            "primary" -> DialogType.PRIMARY
            else -> DialogType.DEFAULT
        }
    }

}