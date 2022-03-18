package net.medlinker.main

import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.blankj.utilcode.util.NetworkUtils
import com.medlinker.baseapp.route.AppJumpHelper
import com.medlinker.lib.push.med.PushClient
import com.medlinker.lib.update.UpgradeUtil
import com.medlinker.lib.update.bean.AppVersionEntity
import com.medlinker.login.net.AccountApi
import net.medlinker.base.account.AccountUtil
import net.medlinker.base.account.UserInfo
import net.medlinker.libhttp.NetCallback
import net.medlinker.libhttp.extend.bindDestroyLifeCycle
import net.medlinker.libhttp.extend.exeRequest
import net.medlinker.libhttp.host.HostManager
import net.medlinker.main.util.NotificationOpenDialog

/**
 *
 * @author zhangquan
 */
class MainLifecycleObserver : LifecycleObserver {
    var mActivity: AppCompatActivity? = null

    constructor(activity: AppCompatActivity) {
        mActivity = activity
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        Looper.myQueue().addIdleHandler {
            //版本升级
            checkUpdate()
            //刷新用户信息
            fetchUserInfo()
            //绑定设备id
            PushClient.bindClientId()

            false
        }
        mActivity?.let {
            if (NotificationOpenDialog.isNeedTip()) {
                NotificationOpenDialog.show(it)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        AppJumpHelper.INSTANCE.checkRouter(mActivity)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {

    }

    private fun checkUpdate() {
        if (!NetworkUtils.isConnected()) {
            return
        }
        UpgradeUtil.checkNewVersion(object : UpgradeUtil.UpdateCallback {
            override fun onUpdateReturned(updateInfo: AppVersionEntity) {
                if (updateInfo.hasNewVersion()) {
                    mActivity?.let { context ->
                        if (!context.isDestroyed) {
                            UpgradeUtil.showDialog(
                                context.supportFragmentManager,
                                updateInfo
                            )
                        }
                    }
                }
            }
        })
    }

    private fun fetchUserInfo() {
        HostManager.getApi(AccountApi::class.java).fetchUserInfo()
            .bindDestroyLifeCycle(mActivity!!)
            .exeRequest(object : NetCallback<UserInfo>() {
                override fun onSuccess(data: UserInfo) {
                    AccountUtil.saveUserInfo(data)
                }

                override fun onError(throwable: Throwable?) {
                    super.onError(throwable)
                }
            })
    }
}