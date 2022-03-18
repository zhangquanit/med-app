package net.medlinker.main.mine.setting

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.AppUtils
import com.medlinker.baseapp.ApiPath
import com.medlinker.baseapp.route.RoutePath
import com.medlinker.lib.push.med.PushClient
import com.medlinker.lib.update.UpgradeUtil
import com.medlinker.lib.update.bean.AppVersionEntity
import com.medlinker.lib.utils.MedAppInfo
import com.medlinker.lib.utils.MedToastUtil
import com.medlinker.widget.dialog.MLConfirmDialog
import com.medlinker.widget.navigation.CommonNavigationBar
import kotlinx.android.synthetic.main.activity_setting.*
import net.medlinker.base.mvvm.BaseViewModel
import net.medlinker.base.mvvm.VMActivity
import net.medlinker.main.R
import net.medlinker.main.mine.AboutActivity
import net.medlinker.main.util.NotificationOpenDialog

class SettingActivity : VMActivity<BaseViewModel>() {
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SettingActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        initUI()
        addListener()
    }

    override fun onResume() {
        super.onResume()
        //开启通知
        val notificationEnabled = PushClient.areNotificationsEnabled(mContext)
        tv_notification.text = if (notificationEnabled) "已开启" else "去开启"
    }

    private fun initUI() {
        //版本
        tv_version.text = MedAppInfo.versionName
        UpgradeUtil.checkNewVersion(object : UpgradeUtil.UpdateCallback {
            override fun onUpdateReturned(updateInfo: AppVersionEntity) {
                tv_has_new_version.visibility =
                    if (updateInfo.hasNewVersion()) View.VISIBLE else View.GONE
            }
        })
        //缓存
//        val length = FileUtils.getLength(MedFileDownloader.getRootDir())
//        if (length > 0) {
//            val size = ConvertUtils.byte2FitMemorySize(length, 1)
//            if (!size.startsWith("0")) {
//                tv_cache.text = size
//            }
//        }
        tv_about.text = "关于${AppUtils.getAppName()}"
    }

    override fun initActionBar(navigation: CommonNavigationBar) {
        navigation.showTitle(R.string.setting)
    }

    private fun addListener() {
        rl_notification.setOnClickListener {
            PushClient.openNotification(mContext)
            NotificationOpenDialog.recordTip()
        }

        rl_version.setOnClickListener {
            // 请求新版本
            UpgradeUtil.checkNewVersion(object : UpgradeUtil.UpdateCallback {
                override fun onUpdateReturned(updateInfo: AppVersionEntity) {
                    if (updateInfo.hasNewVersion()) {
                        UpgradeUtil.showDialogAlways(supportFragmentManager, updateInfo)
                    } else {
                        MedToastUtil.showMessage("已是最新版本")
                    }
                }
            })
        }
        rl_cache.setOnClickListener {
            val dialog = MLConfirmDialog.newInstance()
                .setMessage("确认要清除缓存吗？")
                .setConfirmButtonListener {
//                    MedFileDownloader.clearAll()
                    MedToastUtil.showMessage("缓存清除成功")
                    tv_cache.text = ""
                }
            dialog.showDialog(supportFragmentManager, "dialog_clear_cache")
        }
        rl_about.setOnClickListener {
            AboutActivity.start(mContext)
        }

        rl_cancellation.setOnClickListener {
            RoutePath.startWebActivity(mContext, ApiPath.CANCEL_ACCOUNT_URL)
        }

        tv_logout.setOnClickListener {
            DialogLogoutTip().showDialog(SettingActivity@ this)
        }

    }
}