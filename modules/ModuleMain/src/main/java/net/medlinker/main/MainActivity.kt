package net.medlinker.main

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.util.containsValue
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.medlinker.base.widget.badgeview.BadgeView
import com.medlinker.baseapp.EventType
import com.medlinker.baseapp.route.RoutePath
import com.medlinker.lib.utils.MedAppInfo
import com.medlinker.lib.utils.MedImmersiveModeUtil
import com.medlinker.lib.utils.MedToastUtil
import net.medlinker.base.base.BaseActivity
import net.medlinker.base.event.EventBusUtils
import net.medlinker.base.event.EventMsg
import net.medlinker.main.util.AccountCacheUtil
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

/**
 * 主页面
 * @author zhangquan
 */
@Route(path = RoutePath.MAIN_ACTIVITY)
class MainActivity : BaseActivity() {
    private val mTabRoutes = listOf(
        RoutePath.TAB_HOME,
        RoutePath.TAB_MINE
    )
    private val mTabIcons = listOf(
//        R.mipmap.main_tab_home_n,
//        R.mipmap.main_tab_mine_n
        R.drawable.tab_home,
        R.drawable.tab_mine
    )

    private val mTabTitles = listOf(
        R.string.main_label_home,
        R.string.main_label_mine
    )

    private val TABPOSITION = "tab_position"
    private var mCurrentPosition = 0
    private var mTabBadgeViews: ArrayList<BadgeView>? = null
    private var mMainTabData: FragmentPagerAdapter? = null
    private var mFragmentCaches: SparseArray<Fragment>? = null
    private var mContent: FrameLayout? = null
    private var mTabLayout: TabLayout? = null
    private var startTime = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startTime = System.currentTimeMillis()
        setContentView(R.layout.activity_main)
        initObserver()
        initTab(savedInstanceState)
        EventBusUtils.register(this)
        if (MedAppInfo.isDebug) {
            mContent?.post {
                try {
                    val now = System.currentTimeMillis()
                    val clz = Class.forName("net.medlinker.android.MedlinkerApp")
                    val field = clz.getDeclaredField("startTime")
                    field.isAccessible = true
                    val coldStart = field.get(clz).toString().toLong()
                    if (coldStart > 0) {
                        Log.d("ColdStart", "MainActivity 总共花费:" + (now - startTime))
                        Log.d("ColdStart", "启动 总共花费:" + (now - coldStart))
                    }
                    field.setLong(null, 0)
                } catch (e: Exception) {

                }
            }
        }
    }

    private fun initObserver() {
        lifecycle.addObserver(MainLifecycleObserver(this))
    }

    override fun configImmersiveMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            MedImmersiveModeUtil.setStatusBarTransparent(this)
            MedImmersiveModeUtil.setStatusBarDarkMode(mContext, true)
        } else {
            super.configImmersiveMode()
        }
    }

    private fun initTab(savedInstanceState: Bundle?) {
        if (null != savedInstanceState && savedInstanceState.containsKey(TABPOSITION)) {
            mCurrentPosition = savedInstanceState.getInt(TABPOSITION, 0)
        }
        initTabView()
    }

    private fun initTabView() {
        mContent = findViewById(R.id.content)
        mTabLayout = findViewById(R.id.main_tab)
        if (null != mMainTabData) {
            return
        }
        val tabCount: Int = mTabIcons.size
        mFragmentCaches = SparseArray(tabCount)
        mMainTabData = object :
            FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment {
                val id: Int = mTabIcons[position]
                var fragment = mFragmentCaches?.get(id)
                if (null == fragment) {
                    fragment = getTabFragment(position)
                    mFragmentCaches?.put(id, fragment)
                }
                return fragment
            }

            override fun getCount(): Int {
                return mTabIcons.size
            }

            override fun getItemPosition(`object`: Any): Int {
                return POSITION_NONE
            }

            override fun getItemId(position: Int): Long {
                return mTabIcons[position].toLong()
            }
        }
        mTabLayout?.addOnTabSelectedListener(mOnTabSelectedListener)
        createTabView(tabCount)

        //压后台 页面回收后重建
        val fragments = supportFragmentManager.fragments
        fragments.forEach { frag ->
            if (!mFragmentCaches!!.containsValue(frag)) {
                val size = mTabRoutes.size
                for (i in 0 until size) {
                    val tabFrag = getTabFragment(i)
                    if (TextUtils.equals(frag.javaClass.simpleName, tabFrag.javaClass.simpleName)) {
                        mFragmentCaches?.put(i, frag)
                        break
                    }
                }
            }
        }
    }

    private fun createTabView(tabCount: Int) {
        mTabBadgeViews = ArrayList()
        mTabLayout?.removeAllTabs()
        for (i in 0 until tabCount) {
            val tabView = getTabView(this, i)
            tabView?.tag = mTabIcons[i]
            mTabBadgeViews!!.add(tabView!!.findViewById(R.id.bv_main_tab_unread))
            mTabLayout?.addTab(mTabLayout!!.newTab().setCustomView(tabView), false)
            tabView.setOnClickListener(mTabOnClickListener)
        }
        mTabLayout?.getTabAt(mCurrentPosition)?.select()
    }

    private val mOnTabSelectedListener: OnTabSelectedListener = object : OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab) {
            mCurrentPosition = tab.position
            setCurrentItem(mCurrentPosition)
        }

        override fun onTabUnselected(tab: TabLayout.Tab) {}
        override fun onTabReselected(tab: TabLayout.Tab) {}
    }
    private val mTabOnClickListener: View.OnClickListener = View.OnClickListener { v ->
        val iconId = v.tag as Int
        var position = mTabIcons.indexOf(iconId)
        mTabLayout?.getTabAt(position)?.select()
    }

    /**
     * 获取Tab显示的内容
     */
    private fun getTabView(context: Context, position: Int): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.item_main_tab_layout, null)

        //静态
        val iconIv = view.findViewById<ImageView>(R.id.iv_main_tab_icon)
        iconIv.setImageResource(mTabIcons[position])
        //动态
//        val iconIv = view.findViewById<HomeTabIconView>(R.id.iv_main_tab_icon)
//        val id = mTabIcons[position]
//        iconIv.setImageResource(id)
//        iconIv.setNormalId(id)
//        iconIv.setIconCheckedId(HomeIconConstants.getIconList(id))

        view.findViewById<TextView>(R.id.tv_main_tab_text).text =
            context.getString(mTabTitles[position])
        return view
    }

    private fun getTabFragment(position: Int): Fragment {
        return ARouter.getInstance().build(mTabRoutes[position]).navigation() as Fragment
    }

    private fun setCurrentItem(position: Int) {
        mCurrentPosition = position
        val fragment = mMainTabData?.instantiateItem(mContent!!, position) as Fragment
        mMainTabData?.setPrimaryItem(mContent!!, position, fragment)
        mMainTabData?.finishUpdate(mContent!!)
        val transaction = supportFragmentManager.beginTransaction()
        val count = mFragmentCaches!!.size()
        for (index in 0 until count) {
            val otherFragment: Fragment = mFragmentCaches!!.valueAt(index)
            if (fragment === otherFragment) {
                transaction.show(fragment)
            } else {
                transaction.hide(otherFragment)
            }
        }
        transaction.commitNowAllowingStateLoss()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusUtils.unregister(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(TABPOSITION, mCurrentPosition)
        super.onSaveInstanceState(outState)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            //重新登录
            if (TextUtils.equals(intent.getStringExtra("DATA_KEY"), "relogin")) {
                RoutePath.startLoginActivity()
                finish()
                return
            }

            //切换tab
            val posStr = intent.getStringExtra("pos")
            if (!TextUtils.isEmpty(posStr)) {
                mCurrentPosition = Integer.valueOf(posStr)
                mTabLayout?.getTabAt(mCurrentPosition)?.select()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: EventMsg) {
        when (event.what) {
            EventType.LOGIN_SESSION_OUT -> { //下线通知
                clearCache()
                val msg = if (null != event.obj) event.obj as String else ""
                RoutePath.startOfflineDialogActivty(this, msg)
            }
            EventType.LOGIN_OUT -> { //退出登录
                clearCache()
                RoutePath.startLoginActivity()
                finish()
            }
        }
    }

    private fun clearCache() {
        //清除用户登录缓存
        AccountCacheUtil.clearCache()

    }

    private var mTime: Long = 0
    override fun onBackPressed() {
        val temp = System.currentTimeMillis()
        if (temp - mTime > 3000) {
            MedToastUtil.showMessage(this, R.string.tips_exit)
            mTime = temp
        } else {
            super.onBackPressed()
        }
    }
}