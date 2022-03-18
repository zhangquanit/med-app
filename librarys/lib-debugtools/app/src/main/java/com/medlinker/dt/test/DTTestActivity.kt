package com.medlinker.dt.test

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.medlinker.debugtools.vlayout.viewhold.ViewHolder
import com.medlinker.dt.test.act.DTAopTestActivity
import com.medlinker.dt.test.act.DTCrashTestActivity
import com.medlinker.dt.test.adapter.ListAdapter
import com.medlinker.dt.test.adapter.SimpleItemOnClickListener
import com.medlinker.dt.test.act.DTLaneTestActivity
import com.medlinker.dt.test.entity.MainEntity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.internals.AnkoInternals
import java.util.*

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-29 09:03
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class DTTestActivity : AppCompatActivity() {

    private val clzArray = arrayOf(
        DTLaneTestActivity::class.java,
        DTAopTestActivity::class.java,
        DTCrashTestActivity::class.java
    )

    private val titleArray = arrayOf(
        "泳道",
        "aop测试",
        "crash测试"
    )

    private val contentArray = arrayOf(
        "隔离不同需求环境，在qa，online，泳道环境自由切换",
        "DroidAssist字节码操作测试",
        "全局监听crash事件，通过弹窗方式提示"
    )

    private val contentAdapter =
        object : ListAdapter<MainEntity>(this@DTTestActivity, R.layout.item_main) {
            override fun convert(baseViewHolder: ViewHolder, position: Int, itemData: MainEntity?) {
                itemData?.let {
                    baseViewHolder
                        .setText(R.id.title_tv, itemData.title)
                        .setText(R.id.des_tv, itemData.content)
                }
            }
        }.apply {
            setItemChildOnClickListener(object : SimpleItemOnClickListener<MainEntity>() {
                override fun onItemClick(v: View?, position: Int, data: MainEntity?) {
                    data?.let {
                        AnkoInternals.internalStartActivity(
                            this@DTTestActivity,
                            data.clz,
                            arrayOf(Pair("type", data.type))
                        )
                    }
                }
            })
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initData()
    }

    private fun initView() {
        val flm = LinearLayoutManager(this)
        recyclerView.apply {
            layoutManager = flm
            adapter = contentAdapter
        }
    }

    private fun initData() {
        val data: MutableList<MainEntity> = ArrayList()
        for ((i, value) in titleArray.withIndex()) {
            data.add(MainEntity(clzArray[i], value, contentArray[i]))
        }
        contentAdapter.dataList = data
    }
}

