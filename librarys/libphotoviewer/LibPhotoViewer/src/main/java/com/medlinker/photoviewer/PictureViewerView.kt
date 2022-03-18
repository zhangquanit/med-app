package com.medlinker.photoviewer

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.medlinker.photoviewer.PhotoViewFragment.Companion.newInstance
import com.medlinker.photoviewer.widget.RichBottomDialog
import kotlinx.android.synthetic.main.view_photoview_pager.view.*
import java.io.File

/**
 * @author : HuBoChao
 * @date   : 2021/5/19
 * @desc   : 图片查看view
 */
class PictureViewerView : FrameLayout {

    private var mContext: Context
    private var countVisibility: Int
    var pageChangeListener: OnPageChangeListener? = null
    private var richBottomDialog: RichBottomDialog? = null

    private lateinit var mAdapter: FragmentStatePagerAdapter
    private var mFragments: ArrayList<PhotoViewFragment> = ArrayList()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mContext = context
        View.inflate(context, R.layout.view_photoview_pager, this)
        var ta = context.obtainStyledAttributes(attrs, R.styleable.PictureViewerView)
        countVisibility = ta.getInt(R.styleable.PictureViewerView_countVisibility, 0)
        ta.recycle()
        initView()
    }

    private fun initView() {
        text_photo_num.visibility = countVisibility
    }

    /**
     * 页面切换接口
     */
    open interface OnPageChangeListener {
        fun onPageSelected(position: Int)
    }

    /**
     * 图片路径获取接口，针对特殊bean类处理
     */
    open interface OnImageDataListener {
        fun onInitImageUrl(position: Int): String
    }

    /**
     * 底部弹框item接口
     */
    open interface OnItemsClickListener {
        fun onItemClick(index: Int, url: String?)
    }

    /**
     * 底部弹框默认item回调
     */
    open interface OnDefaultItemClickListener {
        fun onSaveImage(url: String?)
        fun onScanQrcode(scanResult: String)
        fun onCancel()
    }

    /**
     * 移除
     * position 下标
     */
    fun remove(position: Int) {
        if (mFragments.isNotEmpty()) {
            mFragments.removeAt(position)
            mAdapter.notifyDataSetChanged()
        }
    }

    /**
     * 重新加载图片url
     * url 图片地址
     * position 下标
     */
    fun reloadUrl(url: String, position: Int) {
        (mAdapter.getItem(position) as PhotoViewFragment)?.loadImageView(url)
    }

    /**
     * 设置当前显示位置
     * position 下标
     */
    fun setCurrentItem(position: Int) {
        view_pager.currentItem = position
    }

    /**
     * 任意bean类初始化，需要传入处理接口
     * position 初始显示位置
     * size 集合长度
     * listener 自定义处理
     */
    fun initData(position: Int, size: Int = 0,
                 onImageDataListener: OnImageDataListener
    ) {
        if (size <= 0 || onImageDataListener == null) {
            text_photo_num.text = "images data is empty!!!"
            return
        }
        repeat(size) { index ->
            mFragments.add(onImageDataListener?.let {
                newInstance(it.onInitImageUrl(index),
                        onLongClick = {
                            richBottomDialog?.show()
                        },
                        onScanQrcode = { result ->
                            richBottomDialog?.getDialogViewAtPosiont(1)?.let { itemView ->
                                itemView.visibility = View.VISIBLE
                                itemView.tag = result
                            }
                        })
            })
        }
        initAdapter(position)
    }

    /**
     * 简单的String类型的bean类初始化
     * position 初始显示位置
     * items 图片地址集合
     */
    fun initData(position: Int, items: ArrayList<String> = arrayListOf()) {
        if (items.isEmpty()) {
            text_photo_num.text = "images data is empty!!!"
            return
        }
        items.forEach {
            mFragments.add(newInstance(it, {
                richBottomDialog?.show()
            }, { result ->
                richBottomDialog?.getDialogViewAtPosiont(1)?.let { itemView ->
                    itemView.visibility = View.VISIBLE
                    itemView.tag = result
                }
            }))
        }
        initAdapter(position)
    }

    /**
     * 配置adapter和指示器初始index
     * position 初始显示位置
     */
    private fun initAdapter(position: Int) {
        if (mContext !is FragmentActivity) {
            return
        }
        mAdapter = object : FragmentStatePagerAdapter((mContext as FragmentActivity)
                .supportFragmentManager) {
            override fun getItem(i: Int): Fragment {
                return mFragments[i]
            }

            override fun getCount(): Int {
                return mFragments.size
            }

            override fun finishUpdate(container: ViewGroup) {
                try {
                    super.finishUpdate(container)
                } catch (nullPointerException: NullPointerException) {
                    nullPointerException.printStackTrace()
                }
            }

            override fun getItemPosition(`object`: Any): Int {
                return POSITION_NONE
            }
        }
        text_photo_num.text = mContext.getString(R.string.image_index, position + 1, mFragments.size)
        view_pager.adapter = mAdapter
        view_pager.currentItem = if (position >= mFragments.size || position <= 0) 0 else position
        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                text_photo_num.text = mContext.getString(R.string.image_index, position + 1, mFragments.size)
                pageChangeListener?.onPageSelected(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    /**
     * 底部弹框默认items【保存图片】【识别图中二维码】【取消】
     */
    fun setDefaultItems(onDefaultItemClickListener: OnDefaultItemClickListener) {
        if (onDefaultItemClickListener == null) {
            return
        }
        if (mContext !is FragmentActivity) {
            return
        }
        richBottomDialog = RichBottomDialog.Builder((mContext as FragmentActivity).supportFragmentManager, mContext)
                .apply {
                    this.addViewItem(mContext?.getString(R.string.more_item_save_pic)) {
                        onDefaultItemClickListener
                                .onSaveImage((mAdapter.getItem(view_pager.currentItem) as PhotoViewFragment).mUrl)
                    }.addViewItem(mContext?.getString(R.string.more_item_scan_qrcode)) {
                        val result = richBottomDialog?.getDialogViewAtPosiont(it)?.tag.toString()
                        onDefaultItemClickListener
                                .onScanQrcode(result)
                    }.addViewItem(mContext?.getString(R.string.cancel)) {
                        onDefaultItemClickListener
                                .onCancel()
                    }
                }
                .build()
                .apply {
                    this.getDialogDataAtPosiont(1)?.isVisible = false
                }

    }

    /**
     * 自定义底部弹框items
     * items item显示文案
     */
    fun setItems(items: Array<String> = arrayOf(mContext?.getString(R.string.more_item_save_pic),
            mContext?.getString(R.string.more_item_scan_qrcode), mContext?.getString(R.string.cancel)), onItemsClickListener: OnItemsClickListener) {
        if (items.isEmpty()) {
            return
        }
        if (mContext !is FragmentActivity) {
            return
        }
        richBottomDialog = RichBottomDialog.Builder((mContext as FragmentActivity).supportFragmentManager, mContext)
                .apply {
                    items.forEach {
                        this.addViewItem(it) { index ->
                            onItemsClickListener.onItemClick(index,
                                    (mAdapter.getItem(view_pager.currentItem) as PhotoViewFragment).mUrl)
                        }
                    }
                }
                .build()
    }
}