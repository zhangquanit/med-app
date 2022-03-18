package com.medlinker.hybrid.core.ui.widget

import android.view.View
import android.widget.TextView


/**
 * @author hmy
 * @time 11/4/21 14:30
 */
interface IWVNavigationView {

    enum class Direct {
        RIGHT, LEFT
    }

    fun getTitleView(): TextView?
    fun updateTitle(title: String?, textSize: Float?, textColor: Int?)
    fun updateSubTitle(title: String?, textSize: Float?, textColor: Int?)
    fun setTitle(title: String?)
    fun setTitle(title: String?, subTitle: String?, lIcon: String?, rIcon: String?, clickListener: View.OnClickListener?)
    fun cleanNavigation(): IWVNavigationView?
    fun cleanNavigationRight(): IWVNavigationView?
    fun cleanNavigationLeft(): IWVNavigationView?
    fun reset()
    fun appendNavigation(direct: Direct, label: String?, icon: String?, clickListener: View.OnClickListener?): IWVNavigationView?
    fun appendNavigation(direct: Direct, label: String?, iconSource: Int?, clickListener: View.OnClickListener?): IWVNavigationView?
    fun appendInner(direct: Direct, label: String?, icon: Any?, clickListener: View.OnClickListener?): IWVNavigationView?
    fun appendInner(direct: Direct, title: String?, titleSize: Float?, titleColor: Int?, icon: Any?, clickListener: View.OnClickListener?): IWVNavigationView?
    fun setCloseIconVisible(show: Boolean)
    fun setCloseIconOnClickListener(listener: View.OnClickListener?)
    fun setVisibility(visibility: Int)
    fun getHeight(): Int
    fun setBackgroundColor(color: Int)
}