package net.medlinker.main.mine

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.medlinker.protocol.PrivacyManager
import com.medlinker.widget.navigation.CommonNavigationBar
import kotlinx.android.synthetic.main.activity_about.*
import net.medlinker.base.base.BaseCompatActivity
import net.medlinker.main.R

/**
 * 关于
 * @author zhangquan
 */
class AboutActivity : BaseCompatActivity() {
    companion object {
        fun start(ctx: Context) {
            ctx.startActivity(Intent(ctx, AboutActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        rl_protocal.setOnClickListener {
            PrivacyManager.INSTANCE.startUserAgreementActivity(mContext)
        }
        rl_privacy.setOnClickListener {
            PrivacyManager.INSTANCE.startPrivacyActivity(mContext)
        }
    }

    override fun configImmersiveMode() {
        super.configImmersiveMode()
    }

    override fun initActionBar(navigation: CommonNavigationBar?) {
        navigation?.showTitle("关于")
        navigation?.showBackIcon {
            finish()
        }
        navigation?.showBottomLine()
    }
}