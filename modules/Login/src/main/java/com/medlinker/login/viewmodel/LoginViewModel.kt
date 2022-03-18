package com.medlinker.login.viewmodel

import androidx.lifecycle.MutableLiveData
import com.medlinker.network.retrofit.error.ApiException
import net.medlinker.base.account.LoginInfo
import net.medlinker.base.mvvm.BaseViewModel
import net.medlinker.base.mvvm.VMNetCallback
import net.medlinker.base.mvvm.extend.startRequest
import net.medlinker.libhttp.host.HostManager
import com.medlinker.login.net.AccountApi

class LoginViewModel: BaseViewModel() {
    val mVerifyCodeResult = MutableLiveData<Boolean>()
    val mLoginResult = MutableLiveData<LoginInfo>()

    fun getVerifyCode(phone: String, captcha: String?) {
        val params = HashMap<String, Any>()
        params["phone"] = phone
        params["useType"] = "login"
        params["noticeWay"] = "text"
        captcha?.let {
            params["captchaVerification"] = it
        }

        HostManager.getApi(AccountApi::class.java).getVerifyCode(params)
                .startRequest(object : VMNetCallback<Void>(this){
                    override fun onSuccess(data: Void) {
                        mVerifyCodeResult.postValue(true)
                    }

                    override fun onError(throwable: Throwable?) {
                        if (throwable is ApiException) {
                            when(throwable.code){
                                12006,30437,30438, 30439 -> {
                                    mVerifyCodeResult.postValue(false)
                                    return
                                }
                            }
                        }
                        super.onError(throwable)
                    }
                })
    }

    fun doLoginWithCode(phone: String, code: String) {
        showLoading()
        val params = HashMap<String, Any>()
        params["phone"] = phone
        params["verifyCode"] = code

        HostManager.getApi(AccountApi::class.java).loginWithCode(params)
                .startRequest(object : VMNetCallback<LoginInfo>(this){
                    override fun onSuccess(data: LoginInfo) {
                        mLoginResult.postValue(data)
                        hiddenLoading()
                    }

                    override fun onError(throwable: Throwable?) {
                        super.onError(throwable)
                        hiddenLoading()
                    }
                })
    }
}