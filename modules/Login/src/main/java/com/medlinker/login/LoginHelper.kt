package com.medlinker.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import com.chuanglan.shanyan_sdk.OneKeyLoginManager
import com.chuanglan.shanyan_sdk.tool.ShanYanUIConfig
import com.medlinker.analytics.MedAnalytics
import com.medlinker.baseapp.EventType
import com.medlinker.baseapp.route.RoutePath
import com.medlinker.lib.log.LogUtil
import com.medlinker.login.net.AccountApi
import com.medlinker.login.sy.ConfigUtils
import com.medlinker.login.sy.SYManager
import net.medlinker.base.account.AccountUtil
import net.medlinker.base.account.LoginInfo
import net.medlinker.base.account.UserInfo
import net.medlinker.base.base.BaseActivity
import net.medlinker.base.event.EventBusUtils
import net.medlinker.base.event.EventMsg
import net.medlinker.libhttp.NetCallback
import net.medlinker.libhttp.extend.bindDestroyLifeCycle
import net.medlinker.libhttp.extend.exeRequest
import net.medlinker.libhttp.host.HostManager

object LoginHelper {
    private const val tag = "LoginHelper"

    @SuppressLint("CheckResult")
    fun startLogin(activity: BaseActivity) {
        doStartLogin(activity)
    }

    private fun doStartLogin(activity: BaseActivity) {
        activity.showDialogLoading()
        SYManager.startLogin(object :SYManager.OnSyLoginResult {
            override fun onLoginResult(result: String?) {
                LogUtil.d(tag, "SYManager.startLogin result=$result")
                if (null == result) {
                    OneKeyLoginManager.getInstance().finishAuthActivity()
                    activity.hideDialogLoading()
                } else {
                    doLoginWithSy(activity, result)
                }
            }

            override fun getActivity(): Activity {
                return activity
            }

            override fun getConfig(activity: Activity?): ShanYanUIConfig {
                return ConfigUtils.getCJSConfig(activity)
            }
        })
    }

    fun doLoginWithSy(activity: BaseActivity, token: String) {
        val params = HashMap<String, Any>()
        params["flashToken"] = token

        HostManager.getApi(AccountApi::class.java).loginWithSy(params)
            .bindDestroyLifeCycle(activity)
            .exeRequest(object : NetCallback<LoginInfo>() {
                override fun onSuccess(data: LoginInfo) {
                    LogUtil.d(tag, "loginWithSy 成功 data=$data")
                    dealLoginResult(activity, data)
                }

                override fun onError(throwable: Throwable?) {
                    super.onError(throwable)
                    LogUtil.d(tag, "loginWithSy 失败 msg=${throwable?.message}")
                    activity.hideDialogLoading()
                    OneKeyLoginManager.getInstance().finishAuthActivity()
                }
            })
    }


    fun dealLoginResult(act: BaseActivity, data: LoginInfo) {
        AccountUtil.saveLoginInfo(data)
        fetchUserInfo(act)
    }

    private fun fetchUserInfo(activity: BaseActivity) {
        HostManager.getApi(AccountApi::class.java).fetchUserInfo()
            .bindDestroyLifeCycle(activity)
            .exeRequest(object : NetCallback<UserInfo>() {
                override fun onSuccess(data: UserInfo) {
                    LogUtil.d(tag, "fetchUserInfo 成功 data=$data")
                    AccountUtil.saveUserInfo(data)

//                    RealmHelper.updateConfig()
//                    IMGlobalManager.INSTANCE.loadImConfigInfo()
//                    if (!isBasicInfoCompleted()) { //未完善基本信息
//                        RouterUtil.startActivity(RoutePath.USER_BASIC_INFO_ACTIVITY)
//                    } else { //进入首页
//                        RoutePath.startMainActivity()
//                    }
                    RoutePath.startMainActivity()

                    activity.hideDialogLoading()
                    activity.finish()
                }

                override fun onError(throwable: Throwable?) {
                    super.onError(throwable)
                    LogUtil.d(tag, "fetchUserInfo 失败 msg=${throwable?.message}")
                    activity.hideDialogLoading()
                }
            })
    }

    fun logout(activity: FragmentActivity, callback: (() -> Unit)?) {
        if (activity is BaseActivity) {
            activity.showDialogLoading()
        }
        logoutIm(activity)
        HostManager.getApi(AccountApi::class.java).logout()
            .bindDestroyLifeCycle(activity)
            .exeRequest(object : NetCallback<Void>() {
                override fun onSuccess(data: Void) {
                    LogUtil.d(tag, "退出登录成功")
                    AccountUtil.clear()
                    MedAnalytics.clearUser()
                    callback?.invoke()
                    EventBusUtils.post(EventMsg(EventType.LOGIN_OUT))
                }

                override fun onComplete() {
                    super.onComplete()
                    if (activity is BaseActivity) {
                        activity.hideDialogLoading()
                    }
                }
            })
    }

    fun logoutIm(context: Context) {
//        ImServiceHelper.getInstance(context).disConnect()
    }

    /**
     * 是否已完善基本信息
     */
    fun isBasicInfoCompleted(): Boolean {
        val userInfo = AccountUtil.getUserInfo() ?: return false
        if (TextUtils.isEmpty(userInfo.name)
            || userInfo.sex == 0
            || userInfo.birthDate == 0
        ) {
            return false
        }
        return true
    }
}