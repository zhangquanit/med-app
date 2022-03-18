package com.medlinker.dt.test.act

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.medlinker.dt.test.R
import com.medlinker.dt.test.da.Test

class DTAopTestActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_aop)
    }

    fun beforeMethodExecutionInsert(view: View) {
        Log.d(TAG,Test().urlReplace("www.medlinker.com"))
    }

    companion object {
        private const val TAG = "DTAopTestActivity"
    }
}