package net.medlinker.android.splash

import android.os.Handler
import com.alibaba.android.arouter.facade.annotation.Route
import com.medlinker.baseapp.route.AppJumpHelper
import com.medlinker.baseapp.route.RoutePath
import net.medlinker.android.MedlinkerApp
import net.medlinker.base.account.AccountUtil

/**
 * 闪屏页面
 * @author zhangquan
 */
@Route(path = RoutePath.SPLASH_ACTIVITY)
class SplashActivity : WelcomeActivity() {
    private var mHandler = Handler()
    private val mDelay = 1000L
    override fun doNextStep() {
//        if (AccountUtil.getLoginInfo() != null) {
//            IMGlobalManager.INSTANCE.loadImConfigInfo()
//        }
        if (MedlinkerApp.mColdStart) { //冷启动
            startPage()
        } else {
            if (AppJumpHelper.INSTANCE.checkRouter(this)) {
                finish()
            } else {
                mHandler.postDelayed(mRunnable, mDelay) //热启动
            }
        }
        MedlinkerApp.mColdStart = false
    }

    private fun startPage() {
        if (null == AccountUtil.getUserInfo()) {
            RoutePath.startLoginActivity()
        } else {
            //进入首页
            RoutePath.startMainActivity()
        }
        finish()
    }

    private var mRunnable = Runnable {
        startPage()
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacks(mRunnable)
    }
}