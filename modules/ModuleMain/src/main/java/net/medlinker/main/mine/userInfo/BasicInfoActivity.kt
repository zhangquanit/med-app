package net.medlinker.main.mine.userInfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.medlinker.baseapp.route.RoutePath
import com.medlinker.lib.utils.MedToastUtil
import kotlinx.android.synthetic.main.user_basic_info_activity.*
import net.medlinker.base.account.AccountUtil
import net.medlinker.base.base.BaseActivity
import net.medlinker.main.R
import net.medlinker.main.mine.viewmodel.ModifyInfoVm
import java.util.*
import kotlin.collections.HashMap

/**
 * 完善基本信息
 */
@Route(path = RoutePath.USER_BASIC_INFO_ACTIVITY)
class BasicInfoActivity : BaseActivity() {
    private val mViewList = mutableListOf<ABasicInfoPage>()
    private var mCurPos = 0
    private val mDataMap = HashMap<String, Any>()
    private var mViewModel: ModifyInfoVm? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_basic_info_activity)
        initData()
        initView()
    }

    private fun initData() {
        mViewModel = ViewModelProviders.of(this).get(ModifyInfoVm::class.java)
        mViewModel?.mModifyResult?.observe(this, Observer {
            hideDialogLoading()
            if (!it) {
                MedToastUtil.showMessage("保存失败")
                return@Observer
            }
            AccountUtil.getUserInfo()?.let {
//                if (null != mDataMap["name"]) {
//                    it.name = mDataMap["name"] as String
//                }
//                if (null != mDataMap["sex"]) {
//                    it.sex = mDataMap["sex"] as Int
//                }
//                if (null != mDataMap["birthDate"]) {
//                    it.birthDate = mDataMap["birthDate"] as Int
//                }
                AccountUtil.saveUserInfo(it)
                if (mCurPos < mViewList.size - 1) { //下一步
                    mCurPos++
                    viewPager.setCurrentItem(mCurPos, true)
                } else {
                    RoutePath.startMainActivity()
                    finish()
                }
            }
        })
    }

    private fun initView() {
        //下一步
        tv_next.setOnClickListener {
            val page = mViewList[mCurPos]
            if (!page.checkData()) {
                return@setOnClickListener
            }
            showDialogLoading()
            val result = page.getResult()
            mDataMap.putAll(result)
            mViewModel?.modifyUserInfo(result)
        }
        createPageViews()
        viewPager.setPagingEnabled(false)
        viewPager.setSmoothScroll(true)
        viewPager.adapter = pagerAdapter
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(pos: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(pos: Int) {

            }

            override fun onPageScrollStateChanged(pos: Int) {
            }

        })
    }

    private fun createPageViews() {
        mViewList.clear()
        val userInfo = AccountUtil.getUserInfo()
        userInfo?.let {

        }
    }

    private val pagerAdapter = object : PagerAdapter() {
        override fun getCount(): Int {
            return mViewList.size
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val optionView = mViewList[position].getOptionView()
            container.addView(optionView)
            return optionView
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }
    }

    abstract inner class ABasicInfoPage {
        private var mView: View? = null
        protected var mInflater: LayoutInflater? = null

        constructor() {
            mInflater = LayoutInflater.from(mContext)
            mView = initView()
        }

        fun getOptionView(): View {
            return mView!!
        }

        protected abstract fun initView(): View
        abstract fun checkData(): Boolean
        abstract fun getResult(): HashMap<String, Any>
    }

}