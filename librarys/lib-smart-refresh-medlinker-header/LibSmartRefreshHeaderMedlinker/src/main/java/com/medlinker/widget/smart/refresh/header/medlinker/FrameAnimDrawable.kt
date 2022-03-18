package com.medlinker.widget.smart.refresh.header.medlinker

import android.animation.Animator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.animation.LinearInterpolator
import androidx.annotation.IntRange


/**
 * @author hmy
 * @time 3/23/21 11:51
 */
class FrameAnimDrawable : Drawable, Animatable {
    private var mResources: Resources? = null
    private var fps = 25
    private var mPaint: Paint? = null
    private var RES_IDS: IntArray? = null
    private var RES_PATHS: Array<String?>? = null
    private var resIndex = 0
    private var mRepeatCount = ValueAnimator.INFINITE //播放次数，默认为循环播放
    private var mAnimator: ValueAnimator? = null
    private var mAnimUpdateListener: AnimatorUpdateListener? = null
    private var mAnimListener: Animator.AnimatorListener? = null

    //取第一帧，用于获取图片宽高
    private var mFirstDrawable: Drawable? = null
    private var mLength //资源或文件数量
            = 0
    private var mBgColor = Color.TRANSPARENT

    constructor() {}
    constructor(fps: Int, RES_IDS: IntArray, resources: Resources) {
        initById(fps, RES_IDS, resources)
    }

    constructor(
        fps: Int,
        RES_PATHS: Array<String?>,
        resources: Resources
    ) {
        initByPath(fps, RES_PATHS, resources)
    }

    fun initById(
        fps: Int,
        RES_IDS: IntArray,
        resources: Resources
    ) {
        this.fps = fps
        this.RES_IDS = RES_IDS
        mResources = resources
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.isFilterBitmap = true
        mPaint!!.isDither = true
        if (RES_IDS.size <= 0) {
            throw RuntimeException(" FrameAnimDrawable RES_IDS can not empty !!!")
        }
        mLength = RES_IDS.size
        mFirstDrawable = resources.getDrawable(RES_IDS[0])
        createAnimator()
    }

    fun initByPath(
        fps: Int,
        RES_PATHS: Array<String?>,
        resources: Resources
    ) {
        this.fps = fps
        this.RES_PATHS = RES_PATHS
        mResources = resources
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.isFilterBitmap = true
        mPaint!!.isDither = true
        if (RES_PATHS.size <= 0) {
            throw RuntimeException(" FrameAnimDrawable RES_PATHS can not empty !!!")
        }
        mLength = RES_PATHS.size
        mFirstDrawable = getDrawableFromFile(RES_PATHS[0])
        createAnimator()
    }

    /**
     * @param path
     * @return
     */
    private fun getDrawableFromFile(path: String?): BitmapDrawable? {
        val bitmap = BitmapFactory.decodeFile(path) ?: return null
        return BitmapDrawable(mResources, bitmap)
    }

    private fun createAnimator() {
        mAnimator = ValueAnimator.ofInt(mLength - 1)
        mAnimator?.interpolator = LinearInterpolator()
        mAnimator?.repeatCount = mRepeatCount
        mAnimator?.repeatMode = ValueAnimator.RESTART
        mAnimator?.duration = mLength / fps * 1000.toLong()
        mAnimUpdateListener =
            AnimatorUpdateListener { animation -> invalidate(animation.animatedValue as Int) }
    }

    /**
     * 重绘
     */
    fun invalidate(index: Int) {
        resIndex = index
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        canvas.drawColor(mBgColor)
        if (mPaint != null && mResources != null) {
            if (RES_IDS != null) {
                val drawable =
                    mResources!!.getDrawable(RES_IDS!![resIndex % RES_IDS!!.size]) as BitmapDrawable
                val bitmap = drawable.bitmap
                canvas.drawBitmap(bitmap, 0f, 0f, mPaint)
            } else if (RES_PATHS != null) {
                val bitmap =
                    BitmapFactory.decodeFile(RES_PATHS!![resIndex % RES_PATHS!!.size])
                if (bitmap != null) {
                    canvas.drawBitmap(bitmap, 0f, 0f, mPaint)
                }
            }
        }
    }

    override fun setAlpha(
        @IntRange(
            from = 0,
            to = 255
        ) alpha: Int
    ) {
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaint!!.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    override fun start() {
        // If the animators has not ended, do nothing.
        if (mAnimator == null || mAnimator!!.isStarted) return
        startAnimator()
        invalidateSelf()
    }

    private fun startAnimator() {
        mAnimator!!.addUpdateListener(mAnimUpdateListener)
        if (mAnimListener != null) {
            mAnimator!!.addListener(mAnimListener)
        }
        mAnimator!!.start()
    }

    override fun stop() {
        if (mAnimator != null && mAnimator!!.isStarted) {
            mAnimator!!.removeAllUpdateListeners()
            mAnimator!!.removeAllListeners()
            mAnimator!!.end()
        }
    }

    override fun isRunning(): Boolean {
        return mAnimator!!.isRunning
    }

    override fun getIntrinsicWidth(): Int {
        return if (mFirstDrawable != null) {
            mFirstDrawable!!.intrinsicWidth
        } else {
            super.getIntrinsicWidth()
        }
    }

    override fun getIntrinsicHeight(): Int {
        return if (mFirstDrawable != null) {
            mFirstDrawable!!.intrinsicHeight
        } else {
            super.getIntrinsicHeight()
        }
    }

    /**
     * @return 帧数量
     */
    fun getFrameCount(): Int {
        return mLength
    }

    /**
     * 设置一次动画执行时间
     */
    fun setDuration(duration: Float) {
        if (mAnimator != null) {
            mAnimator!!.duration = (duration * 1000).toLong()
        }
    }

    fun setRepeatCount(count: Int) {
        mRepeatCount = if (count <= 0) {
            ValueAnimator.INFINITE
        } else {
            count - 1
        }
        if (mAnimator != null) {
            mAnimator!!.repeatCount = mRepeatCount
            mAnimator!!.repeatMode = ValueAnimator.RESTART
        }
    }

    fun setAnimListener(listener: Animator.AnimatorListener?) {
        mAnimListener = listener
    }

    fun setBackgroundColor(color: Int) {
        mBgColor = color
    }
}
