package com.medlinker.dt.test.act

import android.os.Bundle
import android.util.Log
import android.view.View
import com.medlinker.debugtools.DTModule
import com.medlinker.debugtools.base.DTBaseActivity
import com.medlinker.dt.test.R
import java.lang.NullPointerException

class DTCrashTestActivity : DTBaseActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_crash)
    }

    fun crashTest(view: View) {
        Log.d(TAG,DTModule.app().filesDir.absolutePath)
        throw NullPointerException()
    }

    companion object {
        private const val TAG = "DTCrashTestActivity"
    }
}