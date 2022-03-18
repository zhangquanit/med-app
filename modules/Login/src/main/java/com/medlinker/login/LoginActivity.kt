package com.medlinker.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextPaint
import android.text.TextWatcher
import android.text.style.ClickableSpan
import android.view.View
import android.view.Window
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.medlinker.baseapp.EventType
import com.medlinker.baseapp.route.RoutePath
import com.medlinker.lib.utils.MedAppInfo
import com.medlinker.lib.utils.MedRegexUtil
import com.medlinker.lib.utils.MedSpanUtil
import com.medlinker.lib.utils.MedToastUtil
import com.medlinker.login.captcha.CaptchaHelper
import com.medlinker.login.viewmodel.LoginViewModel
import com.medlinker.protocol.PrivacyManager
import kotlinx.android.synthetic.main.activity_login.*
import net.medlinker.base.event.EventBusUtils
import net.medlinker.base.event.EventMsg
import net.medlinker.base.mvvm.VMActivity
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Route(path = RoutePath.LOGIN_ACTIVITY)
class LoginActivity : VMActivity<LoginViewModel>() {
    companion object {
        @JvmStatic
        fun start(context: Context) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
        initData()
        if (MedAppInfo.isDebug) {
            EventBusUtils.register(this)
        }
    }

    override fun needNavigation() = false

    private fun initView() {
        addListener()
        setPrivacySpannableString()
        tv_login_sy.setOnClickListener {
            LoginHelper.startLogin(this)
        }
    }

    private fun initData() {
        checkSYLogin()
    }

    @SuppressLint("CheckResult")
    private fun checkSYLogin() {
        LoginHelper.startLogin(this)
//        val permission = Manifest.permission.READ_PHONE_STATE
//        val permissonGranted = ContextCompat.checkSelfPermission(
//            mContext,
//            permission
//        ) == PackageManager.PERMISSION_GRANTED
//
//        if (permissonGranted) { //已获得权限
//            LoginHelper.startLogin(this)
//            return
//        }
//
//        val key = "login_permission_sy"
//        val required = KVUtil.getBoolean(key, false)
//        if (!required) { //没有询问过权限
//            KVUtil.set(key, true)
//            LoginHelper.startLogin(this)
//        }
    }

    private fun setPrivacySpannableString() {
        val normalColor = Color.parseColor("#9A9A9A")
        val selColor = Color.parseColor("#9487EA")
        MedSpanUtil.setSpannableString(tv_privacy, arrayOf(
            "已阅读并同意",
            "《用户协议》", "和", "《隐私政策》"
        ),
            intArrayOf(
                normalColor, selColor,
                normalColor, selColor
            ),
            null, null,
            arrayOf(null,
                object : ClickableSpan() {
                    override fun onClick(v: View) {
                        PrivacyManager.INSTANCE.startUserAgreementActivity(mContext)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.color = selColor
                        ds.isUnderlineText = false
                    }
                },
                null,
                object : ClickableSpan() {
                    override fun onClick(v: View) {
                        PrivacyManager.INSTANCE.startPrivacyActivity(mContext)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.color = selColor
                        ds.isUnderlineText = false
                    }
                }
            ),
            true)
    }

    private fun addListener() {
        mViewModel?.mVerifyCodeResult!!.observe(this, Observer {
            if (it) {
                MedToastUtil.showMessage("获取验证码成功")
                var fragment = supportFragmentManager.findFragmentByTag("code_fragment")
                if (null == fragment) {
                    fragment = VerifyCodeFragment()
                    fragment.setPhone(edt_phone.text.toString())

                    supportFragmentManager.beginTransaction()
                        .replace(Window.ID_ANDROID_CONTENT, fragment, "code_fragment")
                        .commitAllowingStateLoss()
                } else {
                    (fragment as VerifyCodeFragment).setPhone(edt_phone.text.toString())
                }
            } else {
                CaptchaHelper.getSlideCode(
                    this,
                    edt_phone.text.toString(),
                    object : CaptchaHelper.OnCaptchaResult {
                        override fun onResult(result: String) {
                            mViewModel?.getVerifyCode(edt_phone.text.toString(), result)
                        }
                    })
            }
        })
        edt_phone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable) {
                btn_get_code.isEnabled = s.length == 11
                iv_clear_phone.visibility = if (s.isNotEmpty()) View.VISIBLE else View.INVISIBLE
            }
        })

        iv_clear_phone.setOnClickListener {
            edt_phone.setText("")
            iv_clear_phone.visibility = View.INVISIBLE
        }
        cb_privacy.setOnClickListener {
            it.isSelected = !it.isSelected
        }

        btn_get_code.setOnClickListener {
            val phone = edt_phone.text.toString()
            if (!MedRegexUtil.isPhoneNumberValid(phone)) {
                MedToastUtil.showMessage("请输入正确的手机号码")
                return@setOnClickListener
            }
            if (!cb_privacy.isSelected) {
                MedToastUtil.showMessage(getString(R.string.please_check_privacy))
                return@setOnClickListener
            }

            mViewModel?.getVerifyCode(phone, null)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: EventMsg) {
        when (event.what) {
            EventType.LOGIN_SESSION_OUT -> { //下线通知
                var fragment = supportFragmentManager.findFragmentByTag("code_fragment")
                if (null != fragment) {
                    supportFragmentManager.beginTransaction().remove(fragment)
                        .commitAllowingStateLoss()
                }
                MedToastUtil.showMessage("请重新登录")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (MedAppInfo.isDebug) {
            EventBusUtils.unregister(this)
        }
    }
}