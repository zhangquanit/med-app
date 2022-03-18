package com.medlinker.hybridapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.alibaba.android.arouter.launcher.ARouter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.cn.glidelib.GlideUtil
import com.medlinker.bridge.*
import com.medlinker.bridge.entity.ImagePreviewEntity
import com.medlinker.bridge.entity.ShareEntity
import com.medlinker.hybrid.core.MedHybrid
import com.medlinker.hybrid.core.plugin.HybridPluginRegister
import com.medlinker.hybrid.core.plugin.ui.IWVUIBridge
import com.medlinker.hybrid.core.plugin.ui.entity.DialogParamEntity
import com.medlinker.hybrid.core.plugin.ui.entity.DialogType
import com.medlinker.hybrid.core.plugin.ui.entity.NavigationConfig
import com.medlinker.hybrid.core.webview.IWebViewAdapter
import com.medlinker.hybridapp.app.ActivityLifecycle
import com.medlinker.hybridapp.plugin.BatteryPlugin
import com.medlinker.hybridapp.utils.ImmersiveModeUtil
import com.medlinker.lib.imagepicker.ClipCameraActivity
import com.medlinker.lib.imagepicker.ClipPhotoPickerActivity
import com.medlinker.lib.imagepicker.PhotoPickerActivity
import com.medlinker.lib.imagepicker.PickerConstants
import com.medlinker.lib.imagepicker.entity.ImageEntity
import com.medlinker.lib.utils.MedAppInfo
import org.json.JSONObject


/**
 * @author hmy
 * @time 3/22/21 11:48
 */
class HybridApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(ActivityLifecycle.getInstance())
        initSDK()
        HybridInit.init(this)
    }

    private fun initSDK() {
        if (BuildConfig.DEBUG) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog()    // 打印日志
            ARouter.openDebug()   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this) // 尽可能早，推荐在Application中初始化

        MedAppInfo.appContext = this
        MedAppInfo.applicationId = BuildConfig.APPLICATION_ID
    }

    private fun shoToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}