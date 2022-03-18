package net.medlinker.main.mine

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.medlinker.baseapp.route.RoutePath
import com.medlinker.widget.dialog.MLConfirmDialog
import kotlinx.android.synthetic.main.mine_fragment.*
import net.medlinker.base.account.AccountUtil
import net.medlinker.base.mvvm.BaseViewModel
import net.medlinker.base.mvvm.VMFragment
import net.medlinker.main.R
import net.medlinker.main.mine.setting.SettingActivity
import net.medlinker.main.mine.userInfo.ModifyUserInfoActivity


/**
 * 我的
 * @author zhangquan
 */
@Route(path =RoutePath.TAB_MINE)
class MineFragment : VMFragment<BaseViewModel>() {
    override fun getLayoutId(): Int {
        return R.layout.mine_fragment
    }

    override fun init(savedInstanceState: Bundle?) {
        AccountUtil.observer().observe(this, Observer { userInfo ->
            userInfo?.let {
                if (!TextUtils.isEmpty(it.avatar)) {
                    Glide.with(this).load(it.avatar).into(iv_header)
                }
                tv_nick_name.text = it.username
            }
        })

        //修改资料
        rl_info.setOnClickListener {
            ModifyUserInfoActivity.start(requireContext())
        }
        tv_modify_info.setOnClickListener {
            ModifyUserInfoActivity.start(requireContext())
        }

        //设置
        tv_setting.setOnClickListener {
            SettingActivity.start(requireContext())
        }

        //我的客服
        tv_customer_service.setOnClickListener {
            val dialog = MLConfirmDialog.newInstance()
                .setMessage("拨打电话 4008157100 ？")
                .setConfirmButtonListener {
                    startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "4008157100")))
                }
            dialog.showDialog(activity?.supportFragmentManager, "dialog_call_customer_service")
        }
    }
}