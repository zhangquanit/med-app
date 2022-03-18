package com.medlinker.login.captcha

import android.annotation.SuppressLint
import android.os.Bundle
import com.medlinker.lib.utils.MedAppInfo
import com.medlinker.lib.utils.MedDeviceUtil
import com.medlinker.login.bean.SlideEntity
import com.medlinker.login.captcha.net.CaptchaApi
import net.medlinker.base.base.BaseCompatActivity
import net.medlinker.libhttp.NetCallback
import net.medlinker.libhttp.extend.bindStopLifeCycle
import net.medlinker.libhttp.extend.exeRequest
import net.medlinker.libhttp.host.HostManager.getApi
import java.util.*


object CaptchaHelper {
    const val COVER_WIDTH = 65
    const val COVER_PADDING = 15
    const val BACKGROUD_WIDTH = 300f

    /**
     * 请求网络获取图片
     *
     * @param activity
     */
    @SuppressLint("CheckResult")
    fun getSlideCode(
        activity: BaseCompatActivity,
        phone: String,
        resultListener: OnCaptchaResult?
    ) {
        activity.showDialogLoading()
        val params = HashMap<String, Any>()
        params["clientUid"] = MedDeviceUtil.getDeviceId(MedAppInfo.appContext)
        params["captchaType"] = "blockPuzzle"
        getApi(CaptchaApi::class.java).getSlideImage(params)
            .bindStopLifeCycle(activity)
            .exeRequest(object: NetCallback<SlideEntity>(){
                override fun onSuccess(slideEntity: SlideEntity) {
                    activity.hideDialogLoading()
                    val slideCodeDialogFragment = SlideCodeDialogFragment()
                    slideCodeDialogFragment.setResultListener(resultListener)
                    slideCodeDialogFragment.arguments = Bundle().apply {
                        putString("backgroud", slideEntity.backgroundImage)
                        putString("float", slideEntity.coverImage)
                        putString("phone", phone)
                        putString("token", slideEntity.token)
                        putString("key", slideEntity.secretKey)
                    }
                    slideCodeDialogFragment.showDialog(activity.supportFragmentManager)
            }

                override fun onError(throwable: Throwable?) {
                    super.onError(throwable)
                    activity.hideDialogLoading()
                }
            })
    }

    interface OnCaptchaResult {
        fun onResult(result: String)
    }
}