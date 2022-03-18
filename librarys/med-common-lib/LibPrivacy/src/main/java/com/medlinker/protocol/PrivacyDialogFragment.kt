package com.medlinker.protocol

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.TextPaint
import android.text.method.ScrollingMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ColorUtils
import com.medlinker.lib.utils.MedSpanUtil
import com.medlinker.widget.dialog.base.MLBaseDialogFragment
import kotlinx.android.synthetic.main.dialog_privacy.*
import net.medlinker.base.common.CommonCallBack


/**
 * @author hmy
 * @time 2020/11/19 13:48
 */
class PrivacyDialogFragment : MLBaseDialogFragment() {

    override fun showDialog(manager: FragmentManager) {
        show(manager, "PrivacyProtocol")
    }

    override fun setupViews(contentView: View?) {
    }

    override fun getDialogLayoutId(): Int {
        return R.layout.dialog_privacy
    }

    override fun updateViews() {
        isCancelable = false
        tv_summary.movementMethod = ScrollingMovementMethod.getInstance()
        showDialog1()
        activity?.let {
            PrivacyManager.INSTANCE.checkFirstLoadApp(it)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDialog1() {
        iv_top.setImageResource(PrivacyManager.INSTANCE.getTopIconRes())
        btn_cancel.text = "不同意"
        btn_ok.text = "同意"
        btn_ok.solidColor = PrivacyManager.INSTANCE.getMainColor()
        btn_cancel.setOnClickListener {
            showDialog2()
        }
        btn_ok.setOnClickListener {
            PrivacyUtil.setPrivacyGranted(true)
            mCallBack?.onCallBack(true)
            dismiss()
        }
        val selColor = PrivacyManager.INSTANCE.getMainColor()
        val appName = AppUtils.getAppName()
        tv_summary.text = "感谢您信任并使用$appName！\n" +
                "为向您提供互联网诊疗服务等基本功能，我们会收集、使用必要的信息，未经您同意，我们不会从第三方处获取、共享或向其提供您的信息。\n\n" +
                PrivacyManager.INSTANCE.getMainContentOfCollectedInformation() +
                "\n\n您可以在相关页面查询、更正、删除您的个人信息，我们也提供账户注销的渠道。\n" +
                "我们会采取业界先进的安全措施保护您的信息安全。\n"

        val grayColor = Color.LTGRAY
        MedSpanUtil.setSpannableString(
            tv_content, arrayOf(
                "为了便于您更详细了解您的权利和义务，请您仔细阅读",
                "《${appName}App隐私政策》",
                "及",
                "《${appName}用户服务协议》",
                "，并郑重做出授权与否的决定。"
            ),
            intArrayOf(
                grayColor, selColor,
                grayColor, selColor,
                grayColor
            ), null, null,
            arrayOf(null,
                object : ClickableSpan() {
                    override fun onClick(v: View) {
                        PrivacyManager.INSTANCE.startPrivacyActivity(v.context)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.color = selColor
                        ds.isUnderlineText = false
                    }
                },
                null,
                object : ClickableSpan() {
                    override fun onClick(v: View) {
                        PrivacyManager.INSTANCE.startUserAgreementActivity(v.context)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.color = selColor
                        ds.isUnderlineText = false
                    }
                }), true
        )
        tv_content.scrollTo(0, 0)
    }

    private fun showDialog2() {
        btn_cancel.text = "退出应用"
        btn_ok.text = "查看协议"
        btn_cancel.setOnClickListener {
            dismiss()
            activity?.finish()
        }
        btn_ok.setOnClickListener {
            showDialog1()
        }
        tv_content.text = "1.您需要同意本隐私权政策和用户协议才能继续使用医联\n\n2.若您不同意本隐私权政策和用户协议，很遗憾我们将无法为您提供服务"
        tv_content.scrollTo(0, 0)
    }

    private var mCallBack: CommonCallBack<Boolean>? = null
    fun callback(callBack: CommonCallBack<Boolean>) {
        mCallBack = callBack
    }
}