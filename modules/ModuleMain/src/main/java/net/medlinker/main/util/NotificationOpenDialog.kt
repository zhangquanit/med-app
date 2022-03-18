package net.medlinker.main.util

import androidx.fragment.app.FragmentActivity
import com.medlinker.lib.push.med.PushClient
import com.medlinker.lib.utils.MedAppInfo
import com.medlinker.widget.dialog.MLConfirmDialog
import net.medlinker.base.storage.KVUtil
import java.text.SimpleDateFormat
import java.util.*

/**
 *  开启消息通知弹框
 * @author zhangquan
 */
object NotificationOpenDialog {
    private const val KEY_NOTIFICATION_TIP = "KEY_NOTIFICATION_TIP"

    @JvmStatic
    fun show(activity: FragmentActivity) {
        val dialog = MLConfirmDialog.newInstance()
            .setTitle("开启消息通知")
            .setMessage("请在设置中开启[允许通知],不错过医生向您发起的视频问诊以及为您开具的新处方")
            .setCancelButton("取消")
            .setConfirmButton("去开启")
            .setConfirmButtonListener {
                PushClient.openNotification(activity)
                recordTip()
            }
        dialog.showDialog(activity.supportFragmentManager, "NotificationTipDialog")
    }

    fun isNeedTip(): Boolean {
        val ctx = MedAppInfo.appContext
        if (PushClient.areNotificationsEnabled(ctx)) {
            return false
        }
//        val recordDay = KVUtil.getString(KEY_NOTIFICATION_TIP)
//        if (TextUtils.isEmpty(recordDay)) {
//            return true
//        }
//        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
//        return !TextUtils.equals(recordDay, today)
        return true;
    }

    /**
     * 记录弹框
     */
    fun recordTip() {
        val day = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        KVUtil.set(KEY_NOTIFICATION_TIP, day)
    }
}