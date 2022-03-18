package net.medlinker.imbusiness.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.medlinker.lib.utils.MedDimenUtil
import com.medlinker.router.MedRouterHelper
import net.medlinker.imbusiness.R
import net.medlinker.imbusiness.entity.ImButtonEntity


/**
 * @author hmy
 * @time 12/3/21 17:24
 */
class ImQuickEntranceLayout : HorizontalScrollView {

    private var mRootLayout: LinearLayout = LinearLayout(context)
    private val mList = ArrayList<ImButtonEntity>()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
    ) {
        setBackgroundColor(Color.WHITE)
        setPadding(MedDimenUtil.dip2px(context, 8.0), 0, MedDimenUtil.dip2px(context, 8.0), 0)

        mRootLayout.layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, MedDimenUtil.dip2px(context, 50.0))
        mRootLayout.orientation = LinearLayout.HORIZONTAL
        mRootLayout.gravity = Gravity.CENTER_VERTICAL
        addView(mRootLayout)

        visibility = GONE
    }

    fun setList(list: ArrayList<ImButtonEntity>?) {
        mList.clear()
        list?.let {
            if (it.size > 0) {
                mList.addAll(it)
            }
        }
        visibility = if (mList.size > 0) VISIBLE else GONE

        mRootLayout.removeAllViews()
        for (buttonEntity in mList) {
            showButtons(buttonEntity)
        }
    }

    private fun showButtons(buttonEntity: ImButtonEntity) {
        val view = LayoutInflater.from(context).inflate(R.layout.item_im_quick_entrance, null)
        mRootLayout.addView(view)
        val lp = view.layoutParams as LinearLayout.LayoutParams
        lp.setMargins(MedDimenUtil.dip2px(context, 8.0), 0, 0, 0)

        val iconIv = view.findViewById<ImageView>(R.id.iv_icon)
        val nameTv = view.findViewById<TextView>(R.id.tv_name)
        iconIv.visibility = if (buttonEntity.icon.isNullOrEmpty()) GONE else VISIBLE
        if (iconIv.visibility == VISIBLE) {
            Glide.with(context).load(buttonEntity.icon).into(iconIv)
        }
        nameTv.text = buttonEntity.name
        view.setOnClickListener {
            MedRouterHelper.withUrl(buttonEntity.jumpUrl).queryTarget().navigation(context)
        }
    }

}