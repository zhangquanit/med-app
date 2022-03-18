package com.medlinker.dt.test

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication

class DTApplication : MultiDexApplication(){
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        DTHelper.init(this)
    }
}