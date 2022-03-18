package com.medlinker.login

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.blankj.utilcode.util.KeyboardUtils
import com.medlinker.lib.utils.MedToastUtil
import com.medlinker.login.viewmodel.LoginViewModel
import com.medlinker.login.widget.OnInputListener
import kotlinx.android.synthetic.main.fragment_verify_code_input.*
import net.medlinker.base.base.BaseActivity
import net.medlinker.base.mvvm.BaseViewModel
import net.medlinker.base.mvvm.VMFragment

class VerifyCodeFragment : VMFragment<BaseViewModel>() {
    private lateinit var mPhone: String
    private var mCountDown = 60
    private val mActivityVM: LoginViewModel by lazy {
        ViewModelProvider(requireActivity()).get(LoginViewModel::class.java)
    }

    override fun getLayoutId(): Int = R.layout.fragment_verify_code_input

    override fun init(savedInstanceState: Bundle?) {
        tv_code_send.text = getString(R.string.verify_code_send_to, mPhone)
        tv_count_down.text = getString(R.string.get_verify_code_again, mCountDown)
        startCountDown()
        addListener()
        KeyboardUtils.showSoftInput(vc_code)
    }

    private fun addListener() {
        btn_next.setOnClickListener {
//            requireActivity().supportFragmentManager.beginTransaction().remove(this).commitAllowingStateLoss()
            val codes = vc_code.text
            if (!TextUtils.isEmpty(codes) && codes!!.length == 6) {
                mActivityVM.doLoginWithCode(mPhone, codes.toString())
            } else {
                MedToastUtil.showMessage("请输入6位验证码")
            }
        }

        vc_code.setOnInputListener(object : OnInputListener() {
            override fun onInputFinished(content: String?) {
                mActivityVM.doLoginWithCode(mPhone, content!!)
                btn_next.isEnabled = true
            }
        })

        mActivityVM.mLoginResult.observe(this, Observer {
            if (null == it) {
                //登陆失败
            } else {
                LoginHelper.dealLoginResult(activity as BaseActivity, it)
            }
        })

        tv_count_down.setOnClickListener {
            it.isEnabled = false
            tv_count_down.setTextColor(ContextCompat.getColor(it.context, R.color.ui_black3))
            mActivityVM.getVerifyCode(mPhone, null)
            requireActivity().supportFragmentManager.beginTransaction().remove(this)
                .commitAllowingStateLoss()
        }
    }

    private fun startCountDown() {
        tv_count_down.postDelayed({
            mCountDown--
            tv_count_down?.let {
                it.text = getString(R.string.get_verify_code_again, mCountDown)
                if (mCountDown > 0) {
                    startCountDown()
                    tv_count_down.setTextColor(ContextCompat.getColor(it.context, R.color.ui_black3))
                } else {
                    it.text = getString(R.string.get_verify_code_again_1)
                    context?.let { _ ->
                        it.isEnabled = true
                        it.setTextColor(ContextCompat.getColor(it.context, R.color.color_main))
                    }
                }
            }
        }, 1000)
    }

    fun setPhone(phone: String) {
        mPhone = phone
        if (isAdded) {
            mCountDown = 60
            startCountDown()
        }
    }
}