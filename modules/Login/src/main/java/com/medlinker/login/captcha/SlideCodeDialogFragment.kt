package com.medlinker.login.captcha

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.ConvertUtils
import com.medlinker.lib.utils.MedAppInfo
import com.medlinker.lib.utils.MedDeviceUtil
import com.medlinker.login.R
import com.medlinker.login.captcha.CaptchaHelper.OnCaptchaResult
import com.medlinker.login.captcha.net.CaptchaApi
import com.medlinker.widget.dialog.base.MLBaseCenterDialogFragment
import kotlinx.android.synthetic.main.view_slide_code_view.*
import net.medlinker.base.base.BaseActivity
import net.medlinker.libhttp.NetCallback
import net.medlinker.libhttp.extend.exeRequest
import net.medlinker.libhttp.host.HostManager
import java.util.*

class SlideCodeDialogFragment : MLBaseCenterDialogFragment() {
    private var backgroudImge: String? = null
    private var floatImge: String? = null
    private var phoneNum: String? = null
    private var mToken: String? = null
    private var mSecreteKey: String? = null
    private var mCoverImageWidth = 0

    private var oldProgress = 0
    var canGet = false
    private var mScale = 1.0f
    private var mThumbMoveDistance = 0
    private var mListener: OnCaptchaResult? = null


    fun setResultListener(listener: OnCaptchaResult?) {
        mListener = listener
    }

    private fun reset() {
        canGet = false
        m_seek_bar?.progress = 0
        m_iv_float?.translationX = 0f
        m_tv_thumb?.translationX = 0f
        oldProgress = 0
    }

    private fun showData() {
        reset()
        //float
        val floatcode = Base64.decode(floatImge, Base64.NO_WRAP)
        val floatbitmap = BitmapFactory.decodeByteArray(floatcode, 0, floatcode.size)
        //背景
        val decode = Base64.decode(backgroudImge, Base64.NO_WRAP)
        val bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.size)
        mCoverImageWidth = floatbitmap.width
        mScale = m_fl_backgroud.layoutParams.width.toFloat() / bitmap.width
        val blp = m_fl_backgroud.layoutParams
        blp.height = (bitmap.height * mScale).toInt()
        m_fl_backgroud.layoutParams = blp
        val flp = m_iv_float.layoutParams
        flp.width = (floatbitmap.width * mScale).toInt()
        m_iv_float.layoutParams = flp
        m_iv_float.setImageBitmap(floatbitmap)
        m_fl_backgroud.setBackgroundDrawable(BitmapDrawable(bitmap))
        mThumbMoveDistance = ConvertUtils.dp2px(270f - 97.5f - 10f)
        m_seek_bar.max = m_fl_backgroud!!.layoutParams.width - flp.width
        SLIDE_OFFSITE = m_seek_bar.max / mThumbMoveDistance * ConvertUtils.dp2px(97.5f / 2)
    }

    private fun initView() {
        m_seek_bar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (progress > oldProgress + SLIDE_OFFSITE || progress < oldProgress - SLIDE_OFFSITE) {
                    seekBar.progress = oldProgress
                    canGet = false
                    return
                }
                m_iv_float.translationX = progress.toFloat()
                m_tv_thumb.translationX = mThumbMoveDistance * (progress.toFloat() / seekBar.max)
                oldProgress = progress
                canGet = true
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                seekBar.progress = oldProgress
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (!canGet) {
                    return
                }
                //滑块左边位置
                val coverPos =
                    (seekBar.progress.toFloat() + m_iv_float.layoutParams.width) / mScale - mCoverImageWidth
                checkCaptcha(coverPos.toInt())
            }
        })
    }

    private fun reloadCaptcha() {
        showLoading(true)
        val params = HashMap<String, Any>()
        params["clientUid"] = MedDeviceUtil.getDeviceId(MedAppInfo.appContext)
        params["captchaType"] = "blockPuzzle"
        HostManager.getApi(CaptchaApi::class.java).getSlideImage(params)
            .exeRequest(object: NetCallback<com.medlinker.login.bean.SlideEntity>(){
                override fun onSuccess(slideEntity: com.medlinker.login.bean.SlideEntity) {
                    showLoading(false)
                    backgroudImge = slideEntity.backgroundImage
                    floatImge = slideEntity.coverImage
                    mToken = slideEntity.token
                    mSecreteKey = slideEntity.secretKey
                    showData()
                }

                override fun onError(throwable: Throwable?) {
                    super.onError(throwable)
                    showLoading(false)
                    dismissAllowingStateLoss()
                }
            })
    }

    private fun showLoading(isShow: Boolean) {
        activity?.let{
            if (isShow) {
                (it as BaseActivity).showDialogLoading()
            } else {
                (it as BaseActivity).hideDialogLoading()
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun checkCaptcha(pos: Int) {
        val pointStr = String.format("{\"x\":%d,\"y\":5}", pos)
        val params: HashMap<String, Any> = HashMap()
        params["captchaType"] = "blockPuzzle"
        params["token"] = mToken!!
        params["pointJson"] = AESUtil.encode(pointStr, mSecreteKey!!)
        params["data"] = phoneNum!!
        HostManager.getApi(CaptchaApi::class.java).slideCheck(params)
            .exeRequest(object: NetCallback<com.medlinker.login.bean.SlideEntity>(){
                override fun onSuccess(data: com.medlinker.login.bean.SlideEntity) {
                    if (data.result) {
                        dismissAllowingStateLoss()
                        val checkResult: String =
                            AESUtil.encode("$mToken---$pointStr", mSecreteKey!!)
                            mListener?.onResult(checkResult)
                    }
                }

                override fun onError(throwable: Throwable?) {
                    super.onError(throwable)
                    reloadCaptcha()
                }
            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments == null) {
            dismiss()
        } else {
            val args = requireArguments()
            backgroudImge = args.getString("backgroud")
            floatImge = args.getString("float")
            phoneNum = args.getString("phone")
            mToken = args.getString("token")
            mSecreteKey = args.getString("key")
            // bundle参数避免传入长String、大数组
            requireArguments().clear()
        }
    }

    override fun getDialogLayoutId(): Int {
        return R.layout.view_slide_code_view
    }

    override fun setupViews(view: View) {
        showData()
        initView()
    }
    override fun updateViews() {}
    override fun showDialog(fragmentManager: FragmentManager) {
        show(fragmentManager, "slide")
    }

    companion object {
        private const val THUMB_WIDTH = 30

        /**
         * 判断是否是点击，不是拖动的值
         */
        private var SLIDE_OFFSITE = 0
    }
}