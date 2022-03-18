package net.medlinker.imbusiness.ui

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.medlinker.baseapp.route.RoutePath
import com.medlinker.lib.utils.MedDeviceUtil
import com.medlinker.widget.navigation.CommonNavigationBar
import kotlinx.android.synthetic.main.fragment_my_doctor.*
import net.medlinker.base.base.BaseFragment
import net.medlinker.imbusiness.IMGlobalManager
import net.medlinker.imbusiness.R
import net.medlinker.imbusiness.ui.adapter.DoctorConversationAdapter
import net.medlinker.imbusiness.ui.viewmodel.MyDoctorViewModel

/**
 * 我的医生
 * @author hmy
 */
@Route(path = RoutePath.CONSULT_TAB)
class MyDoctorFragment : BaseFragment() {

    private var mListAdapter: DoctorConversationAdapter? = null
    private var mViewModel: MyDoctorViewModel? = null
    private var mMsgLoadingTv: TextView? = null

    override fun getLayoutId(): Int {
        return R.layout.fragment_my_doctor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = activity?.let { ViewModelProviders.of(it).get(MyDoctorViewModel::class.java) }
    }

    override fun init(savedInstanceState: Bundle?) {
        initView()

        mViewModel?.let {
            mListAdapter = DoctorConversationAdapter(it.doctorList)
            mListAdapter?.setOnItemClickListener { _, entity, parent ->
                mViewModel?.gotoChat(
                    parent.context,
                    entity
                )
            }
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = mListAdapter

            it.doctorListLiveData.observe(this, {
                mListAdapter?.notifyDataSetChanged()
                layout_refresh.finishRefresh()
            })

        }
    }

    private fun initView() {
        val barH = MedDeviceUtil.getStatusBarHeight(requireActivity())
        layout_root.setPadding(0, barH, 0, 0)
        bar_title.showTitle("我的医生")
        val titleTv =
            bar_title.getNavigationView<TextView>(CommonNavigationBar.NavigationId.CENTER_TV_TITLE)
        titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
        titleTv.setTextColor(Color.parseColor("#2A2A2A"))
        mMsgLoadingTv =
            bar_title.getNavigationView(CommonNavigationBar.NavigationId.CENTER_GET_TV_TEXT)
        mMsgLoadingTv?.text = "接收中..."

        layout_refresh.setOnRefreshListener {
            mViewModel?.getDoctorList()
        }
    }

    override fun onResume() {
        super.onResume()
        mViewModel?.historySessionLiveData?.observe(this, {
            if (!it) {
                mMsgLoadingTv?.let {
                    mMsgLoadingTv!!.visibility = View.VISIBLE
                    val objectAnimator = ObjectAnimator.ofFloat(mMsgLoadingTv, "alpha", 1f, 0.2f)
                    objectAnimator.duration = 800
                    objectAnimator.repeatCount = ValueAnimator.INFINITE
                    objectAnimator.repeatMode = ValueAnimator.REVERSE
                    objectAnimator.start()
                }
            } else {
                mMsgLoadingTv!!.visibility = View.GONE
            }
        })
        IMGlobalManager.INSTANCE.getHistorySession(mViewModel?.historySessionLiveData)
    }

}
