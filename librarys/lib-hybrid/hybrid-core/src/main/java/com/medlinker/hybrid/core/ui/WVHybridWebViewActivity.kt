package com.medlinker.hybrid.core.ui

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.medlinker.hybrid.core.MedHybrid
import com.medlinker.hybrid.core.jsbridge.WVPluginManager


/**
 * @author hmy
 * @time 3/15/21 13:45
 */
class WVHybridWebViewActivity : AppCompatActivity() {

    private lateinit var mFragment: WVHybridFragment

    companion object {
        const val URL = "url"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MedHybrid.getConfig()?.getUIBridge()?.configImmersiveMode(this)
        val url = intent.getStringExtra(URL).toString()
        if (TextUtils.isEmpty(url)) {
            finish()
            return
        }
        mFragment = WVHybridFragment.newInstance(url)
        supportFragmentManager.beginTransaction().replace(android.R.id.content, mFragment, WVHybridFragment::class.java.toString())
                .commit()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        WVPluginManager.onNewIntent(mFragment, intent)
    }

    override fun onBackPressed() {
        if (!mFragment.onBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mFragment.onActivityResult(requestCode, resultCode, data)
    }
}