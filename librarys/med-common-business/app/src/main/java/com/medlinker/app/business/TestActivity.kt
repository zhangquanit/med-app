package com.medlinker.app.business

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.medlinker.video.entity.VideoRoomEntity

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val a = intent.getParcelableExtra("DATA_KEY") as VideoRoomEntity?
        System.currentTimeMillis()
    }
}