package com.medlinker.hybrid.core.ui.widget

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.medlinker.hybrid.R
import com.medlinker.hybrid.core.MedHybrid
import com.medlinker.hybrid.core.ui.widget.IWVNavigationView.Direct


/**
 * @author hmy
 * @time 3/10/21 13:53
 */
open class WVNavigationView : FrameLayout, IWVNavigationView {

    private var mLeftView: ViewGroup? = null
    private var mRightView: ViewGroup? = null
    private var mTitleGroup: ViewGroup? = null
    private var mTitleView: TextView? = null
    private var mSubTitleView: TextView? = null
    private var mLeftIcon: ImageView? = null
    private var mRightIcon: ImageView? = null
    private var mCloseIcon: ImageView? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)

    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) : super(context, attr, defStyleAttr) {
        View.inflate(context, R.layout.hybrid_view_webview_navigation, this)
        setBackgroundColor(MedHybrid.geNavigationConfig().bgColor)

        mLeftView = findViewById(R.id.hybrid_navigation_left)
        mRightView = findViewById(R.id.hybrid_navigation_right)
        mTitleGroup = findViewById(R.id.hybrid_navigation_title_group)
        mTitleView = findViewById(R.id.hybrid_navigation_title)
        mSubTitleView = findViewById(R.id.hybrid_navigation_subtitle)

        mLeftIcon = findViewById(R.id.hybrid_icon_left)
        mRightIcon = findViewById(R.id.hybrid_icon_right)
        mCloseIcon = findViewById(R.id.hybrid_icon_close)
    }

    override fun getTitleView(): TextView? {
        return mTitleView
    }

    override fun updateTitle(title: String?, textSize: Float?, textColor: Int?) {
        mTitleView?.visibility = if (TextUtils.isEmpty(title))
            View.GONE
        else
            View.VISIBLE
        mTitleView?.let {
            it.text = title
        }
        textSize?.let {
            mTitleView!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, it)
        }
        textColor?.let {
            mTitleView?.setTextColor(it)
        }
    }

    override fun updateSubTitle(title: String?, textSize: Float?, textColor: Int?) {
        mSubTitleView?.visibility = if (TextUtils.isEmpty(title)) View.GONE else View.VISIBLE
        mSubTitleView?.let {
            it.text = title
        }
        textSize?.let {
            mSubTitleView!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, it)
        }
        textColor?.let {
            mSubTitleView?.setTextColor(it)
        }
    }

    override fun setTitle(title: String?) {
        setTitle(title, null, null, null, null)
    }

    override fun setTitle(title: String?, subTitle: String?, lIcon: String?, rIcon: String?, clickListener: OnClickListener?) {
        mTitleView?.text = title
        mSubTitleView?.text = subTitle
        if (TextUtils.isEmpty(subTitle)) {
            mSubTitleView?.visibility = View.GONE
            mTitleView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, MedHybrid.geNavigationConfig().titleSize)
        } else {
            mSubTitleView?.visibility = View.VISIBLE
            mTitleView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, MedHybrid.geNavigationConfig().titleSize)
            mSubTitleView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, MedHybrid.geNavigationConfig().subTitleSize)
        }
        if (TextUtils.isEmpty(lIcon)) {
            mLeftIcon?.visibility = View.GONE
        } else {
            mLeftIcon?.visibility = View.VISIBLE
            mLeftIcon?.setImageURI(Uri.parse(lIcon))
        }
        if (TextUtils.isEmpty(rIcon)) {
            mRightIcon?.visibility = View.GONE
        } else {
            mRightIcon?.visibility = View.VISIBLE
            mRightIcon?.setImageURI(Uri.parse(rIcon))
        }
        mTitleGroup?.setOnClickListener(clickListener)
    }

    override fun cleanNavigation(): IWVNavigationView? {
        mRightView?.removeAllViews()
        mLeftView?.removeAllViews()
        mRightView?.visibility = View.GONE
        mLeftView?.visibility = View.GONE
        setTitle(MedHybrid.geNavigationConfig().title, MedHybrid.geNavigationConfig().subtitle, null, null, null)
        updateTitle(MedHybrid.geNavigationConfig().title, MedHybrid.geNavigationConfig().titleSize, MedHybrid.geNavigationConfig().titleColor)
        updateSubTitle(null, MedHybrid.geNavigationConfig().subTitleSize, MedHybrid.geNavigationConfig().subTitleColor)
        mTitleView?.setOnClickListener(null)
        return this
    }

    override fun cleanNavigationRight(): IWVNavigationView? {
        mRightView?.removeAllViews()
        mRightView?.visibility = View.GONE
        return this
    }

    override fun cleanNavigationLeft(): IWVNavigationView? {
        mLeftView?.removeAllViews()
        mLeftView?.visibility = View.GONE
        return this
    }

    override fun reset() {
        cleanNavigation()
        setBackgroundColor(MedHybrid.geNavigationConfig().bgColor)
    }

    override fun appendNavigation(direct: Direct, label: String?, icon: String?, clickListener: OnClickListener?): IWVNavigationView? {
        appendInner(direct, label, icon, clickListener)
        return this
    }

    override fun appendNavigation(direct: Direct, label: String?, iconSource: Int?, clickListener: OnClickListener?): IWVNavigationView? {
        appendInner(direct, label, iconSource, clickListener)
        return this
    }

    override fun appendInner(direct: Direct, label: String?, icon: Any?, clickListener: OnClickListener?): IWVNavigationView? {
        appendInner(direct, label, null, null, icon, clickListener)
        return this
    }

    override fun appendInner(direct: Direct, title: String?, titleSize: Float?, titleColor: Int?, icon: Any?, clickListener: OnClickListener?): IWVNavigationView? {
        if (direct == Direct.LEFT) {
            mLeftView?.visibility = View.VISIBLE
        } else {
            mRightView?.visibility = View.VISIBLE
        }
        val viewGroup = LayoutInflater.from(context).inflate(R.layout.hybrid_item_navigation,
                if (direct == Direct.LEFT) mLeftView else mRightView, false) as ViewGroup
        val iconView = viewGroup.findViewById<ImageView>(R.id.hybrid_icon)
        val textView = viewGroup.findViewById<TextView>(R.id.hybrid_textview)
        titleColor?.let {
            textView.setTextColor(titleColor)
        }
        titleSize?.let {
            textView.textSize = titleSize
        }
        if (icon is String && !TextUtils.isEmpty(icon)) {
            MedHybrid.getConfig()?.getUIBridge()?.loadImage(context, icon, iconView, true)
            iconView.visibility = View.VISIBLE
        } else if (icon is Int) {
            if (icon > 0) {
                iconView.visibility = View.VISIBLE
                iconView.setImageResource(icon)
            } else {
                iconView.visibility = View.GONE
            }
        }
        viewGroup.setOnClickListener(clickListener)
        textView.text = title
        if (direct == Direct.LEFT) {
            mLeftView?.addView(viewGroup)
        } else {
            mRightView?.addView(viewGroup)
        }
        return this
    }

    /**
     * @param show 是否显示close icon
     */
    override fun setCloseIconVisible(show: Boolean) {
        mCloseIcon?.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun setCloseIconOnClickListener(listener: OnClickListener?) {
        mCloseIcon?.setOnClickListener(listener)
    }
}