package net.medlinker.main.mine.userInfo

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import com.medlinker.lib.utils.MedToastUtil
import com.medlinker.widget.navigation.CommonNavigationBar
import kotlinx.android.synthetic.main.activity_edit_name.*
import net.medlinker.base.mvvm.BaseViewModel
import net.medlinker.base.mvvm.VMActivity
import net.medlinker.main.R

/**
 * 修改昵称
 */
class EditNameActivity : VMActivity<BaseViewModel>() {
    companion object {
        val KEY_NAME = "key_name"

        fun startForResult(context: Activity, requestCode: Int) {
            context.startActivityForResult(
                Intent(context, EditNameActivity::class.java),
                requestCode
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_name)
        addListener()
    }

    override fun initActionBar(navigation: CommonNavigationBar) {
        navigation.showTitle(R.string.modify_nick_name)
        val textView =
            navigation.findViewById<TextView>(CommonNavigationBar.NavigationId.RIGHT_TV_TEXT.id)
        textView?.setTextColor(Color.parseColor("#9487EA"))
        navigation.showRightText(R.string.save) {
            if (edt_name.text.length < 2) {
                MedToastUtil.showMessage(getString(R.string.name_input_tip))
                return@showRightText
            }
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra(KEY_NAME, edt_name.text.toString())
            })

            finish()
        }
    }

    private fun addListener() {
        iv_clear.setOnClickListener {
            edt_name.setText("")
        }

        edt_name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                tv_count.text = "${edt_name.text.length}/15"
                iv_clear.visibility = if (s!!.isEmpty()) View.GONE else View.VISIBLE
            }
        })
        edt_name.filters = (arrayOf<InputFilter>(chineseFilter, LengthFilter(15)))
    }

    var chineseFilter = InputFilter { source, start, end, dest, dstart, dend ->
        if (TextUtils.equals(source, " ")) {
            return@InputFilter ""
        }
        source
//        val p = Pattern.compile("[\u4e00-\u9fa5]+")
//        val m = p.matcher(source.toString())
//        if (!m.matches()) "" else null
    }
}