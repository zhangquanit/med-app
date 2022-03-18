package com.medlinker.protocol

import android.graphics.Color
import android.text.Html
import android.text.TextPaint
import android.text.method.ScrollingMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.ColorUtils
import com.medlinker.lib.utils.MedSpanUtil
import com.medlinker.widget.dialog.base.MLBaseDialogFragment
import kotlinx.android.synthetic.main.dialog_privacy_update.*


/**
 * @author hmy
 * @time 2020/11/19 13:48
 */
class PrivacyUpdateDialogFragment : MLBaseDialogFragment() {

    override fun showDialog(manager: FragmentManager) {
        show(manager, "PrivacyProtocolUpdate")
    }

    override fun setupViews(contentView: View?) {
    }

    override fun getDialogLayoutId(): Int {
        return R.layout.dialog_privacy_update
    }

    override fun updateViews() {
        isCancelable = false
        tv_update_content.movementMethod = ScrollingMovementMethod.getInstance()
        showDialog1()
    }

    private fun showDialog1() {
        btn_cancel.text = "不同意"
        btn_ok.text = "同意"
        btn_ok.solidColor = PrivacyManager.INSTANCE.getMainColor()
        btn_cancel.setOnClickListener {
            dismiss()
            activity?.finish()
        }
        btn_ok.setOnClickListener {
            PrivacyUtil.setPrivacyGranted(true)
            PrivacyManager.INSTANCE.cachePrivacyData(PrivacyManager.INSTANCE.getNewPrivacyData())
            dismiss()
        }
        val selColor = PrivacyManager.INSTANCE.getMainColor()
        val appName = AppUtils.getAppName()
        val grayColor = Color.LTGRAY
        MedSpanUtil.setSpannableString(
            tv_content, arrayOf(
                "为了便于您更详细了解您的权利和义务，请您仔细阅读",
                "《${appName}App隐私政策》",
                "，并郑重做出授权与否的决定。"
            ),
            intArrayOf(
                grayColor,
                selColor,
                grayColor
            ), null, null,
            arrayOf(
                null,
                object : ClickableSpan() {
                    override fun onClick(v: View) {
                        PrivacyManager.INSTANCE.startPrivacyActivityByNewUrl(v.context)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.color = selColor
                        ds.isUnderlineText = false
                    }
                }
            ), true
        )
        PrivacyManager.INSTANCE.getNewPrivacyData()?.let {
            tv_update_content.text = Html.fromHtml(it.changeLog)
        }

        tv_content.scrollTo(0, 0)
    }

}