package net.medlinker.android.splash

import android.os.Handler
import com.medlinker.baseapp.route.RoutePath
import net.medlinker.android.MedlinkerApp
import net.medlinker.base.router.RouterUtil

/**
 * 闪屏页面
 * @author zhangquan
 */
class SplashActivity : WelcomeActivity() {
    private var mHandler = Handler()
    private val mDelay = 1000L

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacks(mRunnable)
    }

    private var mRunnable = Runnable {
        startPage()
    }

    override fun doNextStep() {

        if (MedlinkerApp.mColdStart) { //冷启动
            startPage()
        } else {
            mHandler.postDelayed(mRunnable, mDelay) //热启动
        }
        MedlinkerApp.mColdStart = false
    }

    /**
     * 打开页面
     */
    private fun startPage() {
        //打开组件页面
//        LoginActivity.start(this)
        //首页运动处方组件开发页面
//        RouterUtil.startActivity(RoutePath.PLAN_DEV)
        finish()
    }
}