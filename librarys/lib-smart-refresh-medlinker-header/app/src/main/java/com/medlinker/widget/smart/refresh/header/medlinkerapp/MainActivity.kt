package com.medlinker.widget.smart.refresh.header.medlinkerapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.medlinker.widget.smart.refresh.header.medlinker.MedlinkerHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val refreshLayout = findViewById<SmartRefreshLayout>(R.id.layout_refresh)
        val header = MedlinkerHeader(this)
        header.hideText(true)
        refreshLayout.setRefreshHeader(header)
        refreshLayout.setHeaderHeight(50f)
    }
}