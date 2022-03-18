package com.medlinker.hybrid.core.plugin.ui

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ImageView
import com.medlinker.hybrid.core.plugin.ui.entity.DialogParamEntity
import com.medlinker.hybrid.core.plugin.ui.entity.DropdownItemEntity
import com.medlinker.hybrid.core.ui.widget.DropDownPopMenu


/**
 * @author hmy
 * @time 3/10/21 14:51
 */
interface IWVUIBridge {

    fun loadImage(context: Context, url: String, imageView: ImageView, isCircle: Boolean = false)
    fun loadIcon(context: Context, url: String)

    /**
     * 状态栏是否支持沉浸式
     */
    fun isSupportImmersed(): Boolean

    /**
     * 设置页面沉浸式默认样式
     */
    fun configImmersiveMode(activity: Activity?)

    /**
     * 当设置成 immersed 沉浸式状态栏时，头部和状态栏都设置成透明，并且 webView 铺满整个屏幕
     */
    fun setImmersed(activity: Activity?, enable: Boolean)
    fun showToast(msg: String)

    /**
     * @param onItemClickListener 获取按钮position
     */
    fun showDialog(context: Context?, dialogParamEntity: DialogParamEntity, onItemClickListener: (position: Int) -> Unit)

    /**
     * 显示下拉菜单
     */
    fun showDropDownMenu(context: Context?, targetView: View, dropdownList: ArrayList<DropdownItemEntity>, onItemClickListener: (position: Int) -> Unit) {
        context?.let {
            val menu = DropDownPopMenu(it)
            menu.setOnItemClickListener(object : DropDownPopMenu.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    onItemClickListener.invoke(position)
                }
            })
            menu.setList(dropdownList)
            menu.showWindow(targetView)
        }
    }
}