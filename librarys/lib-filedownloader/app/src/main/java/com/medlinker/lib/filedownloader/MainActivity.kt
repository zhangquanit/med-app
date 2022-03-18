package com.medlinker.lib.filedownloader

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun singleTask(view: View) {
        startActivity(Intent(this, SingleTaskActivity::class.java))
    }

    fun multipleTask(view: View) {
        startActivity(Intent(this, MultipleTaskActivity::class.java))

    }

    fun batchTask(view: View) {
        startActivity(Intent(this, BatchTaskActivity::class.java))
    }
}