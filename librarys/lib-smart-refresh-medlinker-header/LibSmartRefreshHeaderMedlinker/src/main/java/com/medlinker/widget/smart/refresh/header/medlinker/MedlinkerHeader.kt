package com.medlinker.widget.smart.refresh.header.medlinker

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.simple.SimpleComponent
import kotlin.math.roundToInt


/**
 * 医联风格刷新头
 * @author hmy
 * @time 3/23/21 10:24
 */
class MedlinkerHeader : SimpleComponent, RefreshHeader {

    private var mLoadingView: ImageView
    private var mTitleText: TextView
    private var mTextPulling: String? = null
    private var mTextRefreshing: String? = null
    private var mTextRelease: String? = null
    private var mTextFinish: String? = null
    private var mTextFailed: String? = null

    private var mDrawable: FrameAnimDrawable? = null
    private var mHideText = true

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0) {
        View.inflate(context, R.layout.srl_medlinker_header, this)

        mLoadingView = findViewById(R.id.srl_medlinker_progress)
        mTitleText = findViewById(R.id.srl_medlinker_title)
        mTitleText.visibility = if (mHideText) View.GONE else View.VISIBLE
        init()
        initAnim()
    }

    private fun init() {
        mTextPulling = context.getString(R.string.srl_header_pulling)
        mTextRelease = context.getString(R.string.srl_header_release)
        mTextRefreshing = context.getString(R.string.srl_header_refreshing)
        mTextFinish = context.getString(R.string.srl_header_finish)
        mTextFailed = context.getString(R.string.srl_header_failed)
    }

    fun setStateText(
        textPulling: String?,
        textRelease: String?,
        textRefreshing: String?,
        textFinish: String?,
        textFailed: String?
    ) {
        mTextPulling = textPulling
        mTextRelease = textRelease
        mTextRefreshing = textRefreshing
        mTextFinish = textFinish
        mTextFailed = textFailed
    }

    fun hideText(hide: Boolean) {
        mHideText = hide
        mTitleText.visibility = if (hide) View.GONE else View.VISIBLE
    }

    private fun initAnim() {
        val res_ids = intArrayOf(
            R.mipmap.loading_00000,
            R.mipmap.loading_00001,
            R.mipmap.loading_00002,
            R.mipmap.loading_00003,
            R.mipmap.loading_00004,
            R.mipmap.loading_00005,
            R.mipmap.loading_00006,
            R.mipmap.loading_00007,
            R.mipmap.loading_00008,
            R.mipmap.loading_00009,
            R.mipmap.loading_00010,
            R.mipmap.loading_00011,
            R.mipmap.loading_00012,
            R.mipmap.loading_00013,
            R.mipmap.loading_00014,
            R.mipmap.loading_00015,
            R.mipmap.loading_00016,
            R.mipmap.loading_00017,
            R.mipmap.loading_00018,
            R.mipmap.loading_00019,
            R.mipmap.loading_00020,
            R.mipmap.loading_00021,
            R.mipmap.loading_00022,
            R.mipmap.loading_00023,
            R.mipmap.loading_00024,
            R.mipmap.loading_00025,
            R.mipmap.loading_00026,
            R.mipmap.loading_00027,
            R.mipmap.loading_00028,
            R.mipmap.loading_00029,
            R.mipmap.loading_00030,
            R.mipmap.loading_00031,
            R.mipmap.loading_00032,
            R.mipmap.loading_00033,
            R.mipmap.loading_00034,
            R.mipmap.loading_00035,
            R.mipmap.loading_00036,
            R.mipmap.loading_00037,
            R.mipmap.loading_00038,
            R.mipmap.loading_00039,
            R.mipmap.loading_00040,
            R.mipmap.loading_00041,
            R.mipmap.loading_00042,
            R.mipmap.loading_00043,
            R.mipmap.loading_00044
        )
        mDrawable = FrameAnimDrawable(23, res_ids, resources)
        mLoadingView.setImageDrawable(mDrawable)
    }

    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {
        super.onInitialized(kernel, height, maxDragHeight)
        mDrawable?.stop()
        mTitleText.text = mTextPulling
    }

    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        if (success) {
            mTitleText.text = mTextFinish
        } else {
            mTitleText.text = mTextFailed
        }
        mDrawable?.stop()
        return super.onFinish(refreshLayout, success)
    }

    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState
    ) {

        mTextPulling = context.getString(R.string.srl_header_pulling)
        mTextRelease = context.getString(R.string.srl_header_release)
        mTextRefreshing = context.getString(R.string.srl_header_refreshing)
        mTextFinish = context.getString(R.string.srl_header_finish)
        mTextFailed = context.getString(R.string.srl_header_failed)

        when (newState) {
            RefreshState.PullDownToRefresh -> {
                mTitleText.text = mTextPulling
                mLoadingView.animate().rotation(0f)
            }
            RefreshState.ReleaseToRefresh -> {
                mTitleText.text = mTextRelease
            }
            RefreshState.Refreshing, RefreshState.RefreshReleased -> {
                mTitleText.text = mTextRefreshing
                mDrawable?.start()
            }
        }
        super.onStateChanged(refreshLayout, oldState, newState)
    }

    override fun onMoving(
        isDragging: Boolean,
        percent: Float,
        offset: Int,
        height: Int,
        maxDragHeight: Int
    ) {
        mDrawable?.stop()
        mDrawable?.invalidate((percent * (mDrawable!!.getFrameCount() - 1)).roundToInt())
        super.onMoving(isDragging, percent, offset, height, maxDragHeight)
    }

}