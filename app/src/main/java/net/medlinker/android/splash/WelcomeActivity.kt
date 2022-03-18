package net.medlinker.android.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.medlinker.baseapp.route.AppJumpHelper
import com.medlinker.protocol.PrivacyDialogFragment
import com.medlinker.protocol.PrivacyUtil
import net.medlinker.android.MedlinkerApp
import net.medlinker.base.common.CommonCallBack

/**
 * @author zhangquan
 */
abstract class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkJumpRouter(intent)
        checkShowDialog()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        checkJumpRouter(intent)
    }

    private fun checkJumpRouter(intent: Intent?) {
        val uri = intent?.data
        if (uri != null) {
            AppJumpHelper.INSTANCE.setRouter(uri)
        }
    }


    private fun checkShowDialog() {
        if (!PrivacyUtil.isPrivacyGranted()) {
            showPrivacyDialog()
        } else {
            doNextStep()
        }
    }

    /**
     * 隐私政策弹框
     */
    private fun showPrivacyDialog() {
        val dialogFragment = PrivacyDialogFragment()
        dialogFragment.callback(CommonCallBack {
            MedlinkerApp.mApp.initSensitiveSDK()
            MedlinkerApp.mApp.initSensitiveSDKOnMainProcess()
            doNextStep()
        })
        dialogFragment.setCanceledOnTouchOutside(false)
        dialogFragment.isCancelable=false
        dialogFragment.show(supportFragmentManager, "PrivacyProtocol")
    }

    abstract fun doNextStep()
}