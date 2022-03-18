package net.medlinker.main.mine.setting

import androidx.fragment.app.FragmentActivity
import com.medlinker.login.LoginHelper
import com.medlinker.widget.dialog.MLConfirmDialog

class DialogLogoutTip {

    fun showDialog(activity: FragmentActivity) {
        val dialog = MLConfirmDialog.newInstance()
            .setTitle("提示")
            .setMessage("退出后您需要再次登录才能查看个人收藏的各种信息，确认退出？")
            .setCancelButton("取消")
            .setConfirmButton("确认")
            .setConfirmButtonListener {
                LoginHelper.logout(activity) {
                    activity.finish()
                }
            }
        dialog.setCanceledOnTouchOutside(false)
        dialog.isCancelable = false
        dialog.showDialog(activity.supportFragmentManager, "LoginOutDialog")
    }
}