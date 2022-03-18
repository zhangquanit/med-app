package com.medlinker.hybrid.core.ui.widget

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.medlinker.hybrid.R
import com.medlinker.hybrid.core.MedHybrid
import com.medlinker.hybrid.core.plugin.ui.entity.DropdownItemEntity
import java.util.*

/**
 * 下拉菜单弹窗
 *
 * @author hmy
 */
class DropDownPopMenu(private val mContext: Context) : PopupWindow() {
    private var mContentView: View? = null
    private var mRecyclerView: RecyclerView? = null
    private val mList: ArrayList<DropdownItemEntity> = ArrayList()
    private var mAdapter: RecyclerView.Adapter<ListAdapter.ViewHolder>? = null
    private var mOnItemClickListener: OnItemClickListener? = null

    private fun initView() {
        mContentView = LayoutInflater.from(mContext).inflate(R.layout.layout_dropdown_menu, null)
        this.width = ViewGroup.LayoutParams.WRAP_CONTENT
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT
        this.contentView = mContentView
        this.isOutsideTouchable = true
        this.isFocusable = true
        setBackgroundDrawable(BitmapDrawable())
        this.animationStyle = R.style.popup_style
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mRecyclerView = mContentView!!.findViewById(R.id.rv_menu)
        mRecyclerView!!.layoutManager = LinearLayoutManager(mContext)
    }

    fun showWindow(anchor: View) {
        show(anchor)
        setAdapter()
    }

    private fun show(view: View) {
        view.post {
            translucentBackground(mContext as Activity)
            super@DropDownPopMenu.showAsDropDown(view)
            mAdapter!!.notifyDataSetChanged()
        }
    }

    fun setList(list: ArrayList<DropdownItemEntity>?) {
        mList.clear()
        if (list != null && list.isNotEmpty()) {
            mList.addAll(list)
        }
    }

    private fun setAdapter() {
        if (null == mAdapter) {
            mAdapter = ListAdapter(mList)
            mRecyclerView!!.adapter = mAdapter
        }
    }

    private inner class ListAdapter(private val list: List<DropdownItemEntity>?) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_menu, null))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val (title, icon) = list!![position]

            holder.titleTv.text = title
            holder.iconIv.visibility = if (TextUtils.isEmpty(icon)) View.GONE else View.VISIBLE
            if (!TextUtils.isEmpty(icon)) {
                MedHybrid.getConfig()?.getUIBridge()?.loadImage(holder.itemView.context, icon, holder.iconIv)
            }
            holder.itemView.tag = position
            holder.itemView.setOnClickListener(mItemClick)
        }

        override fun getItemCount(): Int {
            return list?.size ?: 0
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var iconIv: ImageView = itemView.findViewById(R.id.iv_icon)
            var titleTv: TextView = itemView.findViewById(R.id.tv_title)
        }
    }

    private val mItemClick = View.OnClickListener { v ->
        dismiss()
        if (mOnItemClickListener != null) {
            mOnItemClickListener!!.onItemClick(v.tag as Int)
        }
    }

    override fun dismiss() {
        cancelBackground(mContext as Activity)
        super.dismiss()
    }

    private fun translucentBackground(activity: Activity) {
        val lp = activity.window.attributes
        lp.alpha = 0.6f
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        activity.window.attributes = lp
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        mOnItemClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    companion object {
        private fun cancelBackground(activity: Activity) {
            val lp1 = activity.window.attributes
            lp1.alpha = 1f
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            activity.window.attributes = lp1
        }
    }

    init {
        initView()
    }
}