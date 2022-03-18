package com.medlinker.hybridapp

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
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
import com.medlinker.hybridapp.plugin.BatteryPlugin
import com.medlinker.hybridapp.utils.ImmersiveModeUtil
import com.medlinker.lib.imagepicker.ClipCameraActivity
import com.medlinker.lib.imagepicker.ClipPhotoPickerActivity
import com.medlinker.lib.imagepicker.PhotoPickerActivity
import com.medlinker.lib.imagepicker.PickerConstants
import com.medlinker.lib.imagepicker.entity.ImageEntity
import org.json.JSONObject


/**
 * @author hmy
 * @time 11/17/21 15:40
 */
class HybridInit {

    companion object {

        fun init(applicationContext: Application) {
            BridgeManager
                    .setPayBridge(object : PayBridge() {
                        override fun payInterceptorWithUrlByAliPay(url: String?, callback: BridgeCallback<Void>) {
                            //TODO alipay 集成sdk
//                            final PayTask task = new PayTask (activity);
//                            task.payInterceptorWithUrl(url, true, new H5PayCallback () {
//                                @Override
//                                public void onPayResult(final H5PayResultModel result) {
//                                    observer.update(null, result.getReturnUrl());
//                                }
//                            });
                        }

                        override fun getWeiXinPayReferer(): String {
                            return super.getWeiXinPayReferer()
                        }

                    })
                    .setLinkingBridge(LinkingBridge())
                    .setUpdaterBridge(object : UpdaterBridge {
                        override fun checkAppUpdate(context: Context?) {
                            Toast.makeText(context, "checkAppUpdate", Toast.LENGTH_SHORT).show()
                        }

                    })
                    .setRouterBridge(object : RouterBridge {
                        override fun route(context: Context?, url: String?, type: String?): Boolean {
                            Toast.makeText(context, "路由：$url", Toast.LENGTH_SHORT).show()
                            return false //true拦截；false不拦截，如果url以www. http://  https://开头，跳转到默认容器，如果需要修改容器，需要返回true
                        }

                    })
                    .setSocialBridge(object : SocialBridge() {
                        override fun onShare(context: Context, shareEntity: ShareEntity) {
                            Toast.makeText(context, "分享", Toast.LENGTH_SHORT).show()
                        }

                    })
                    .setAuthBridge(object : AuthBridge {

                        override fun isLogin(): Boolean {
                            return LoginActivity.userInfo != null
                        }

                        override fun getSessionInfo(): JSONObject? {
                            val jsonObject = JSONObject()
                            jsonObject.put("x_platform", "xxxApp") //TODO
                            jsonObject.put("sess", "xxxxxxxxxxxxxxxxxxxx") //TODO
                            return jsonObject
                        }

                        override fun login(context: Context?, requestCode: Int) {
                            if (context is Activity) {
                                context.startActivityForResult(Intent(context, LoginActivity::class.java), requestCode)
                            }
                        }

                        override fun getUserInfo(callBack: (userInfo: Any?) -> Unit) {
                            callBack.invoke(LoginActivity.userInfo?.toJsonObject())
                        }

                        override fun logout(context: Context?) {
                            Toast.makeText(context, "退出登录成功", Toast.LENGTH_SHORT).show()
                            LoginActivity.userInfo = null
                        }

                    })
                    .setDeviceBridge(DeviceBridge())
                    .setCameraBridge(object : CameraBridge() {

                        override fun onCallCamera(context: Context?, isFrontCamera: Boolean, cropWidth: Int, cropHeight: Int, requestCode: Int) {
                            if (context is Activity) {
                                val intent = Intent(context, ClipCameraActivity::class.java)
                                intent.putExtra("clip_width", cropWidth)
                                intent.putExtra("clip_height", cropHeight)
                                if (cropWidth == 0 || cropHeight == 0) {
                                    intent.putExtra("capture_only", true)
                                }
                                context.startActivityForResult(intent, requestCode)
                            }
                        }

                        override fun onCallAlbum(context: Context?, maxImgCount: Int, cropWidth: Int, cropHeight: Int, requestCode: Int) {
                            if (context is Activity) {
                                if (maxImgCount > 1 || cropWidth == 0 || cropHeight == 0) {
                                    val intent = Intent(context, PhotoPickerActivity::class.java)
                                    intent.putExtra("MAX_COUNT", maxImgCount)
                                    context.startActivityForResult(intent, requestCode)
                                } else {
                                    val intent = Intent(context, ClipPhotoPickerActivity::class.java)
                                    intent.putExtra("clip_width", cropWidth)
                                    intent.putExtra("clip_height", cropHeight)
                                    context.startActivityForResult(intent, requestCode)
                                }
                            }
                        }

                        override fun onShowGallery(context: Context?, imagePreviewEntity: ImagePreviewEntity) {
                            Toast.makeText(context, imagePreviewEntity.toString(), Toast.LENGTH_SHORT).show() //TODO
                        }

                        override fun onActivityResultCamera(intent: Intent?, callback: (imagePath: String?) -> Unit) {
                            intent?.let {
                                val imagePath = intent.getStringExtra(PickerConstants.BUNDLE_PARAMS)
                                if (!TextUtils.isEmpty(imagePath)) {
                                    callback.invoke(imagePath)
                                }
                            }
                        }

                        override fun onActivityResultAlbum(intent: Intent?, callback: (imagePaths: ArrayList<String?>) -> Unit) {
                            intent?.let {
                                val imagePath = intent.getStringExtra(PickerConstants.BUNDLE_PARAMS)
                                if (!TextUtils.isEmpty(imagePath)) {
                                    val list = ArrayList<String?>(1)
                                    list.add(imagePath)
                                    callback.invoke(list)
                                    return
                                }
                                val imageList = intent.getParcelableArrayListExtra<ImageEntity>("SELECTED_PHOTOS")
                                if (imageList != null) {
                                    val list = ArrayList<String?>(imageList.size)
                                    for (imageEntity in imageList) {
                                        val filePath = imageEntity.filePath
                                        list.add(filePath)
                                    }
                                    callback.invoke(list)
                                }
                            }
                        }
                    })

            val host = "medlinker.com"
            // 不同APP，ua不同，需要修改
//            val ua = " medlinker/${BuildConfig.VERSION_NAME}(med_hybrid_medlinker_${BuildConfig.VERSION_NAME})"
            val ua = " orthopaedicsApp/1.0.0 medhybrid"
            MedHybrid.setAppVersionCode(BuildConfig.VERSION_CODE)
                    .setAppVersionName(BuildConfig.VERSION_NAME)
                    .setH5CookieDomain(host)
                    .setUserAgentString(ua)
                    .setOpenLog(BuildConfig.DEBUG)
                    .setNavigationConfig(NavigationConfig("APP_NAME")) // TODO 取APP真实名
                    .setConfig(MedHybrid.Config.Builder()
                            .setWebViewAdapter(object : IWebViewAdapter() {

                                override fun getCookieParams(): Array<String>? {
                                    // 不注入cookie，返回null
                                    return arrayOf(
                                            "sess=1111111111111;Domain=.$host;Path=/",
                                            "x_platform=orthopaedicsApp;Domain=.$host;Path=/"
                                    )
                                }

                                override fun log(msg: String?, tr: Throwable?) {
                                    Log.d("MedHybrid", msg!!)
                                }
                            })
                            .setUIAdapter(object : IWVUIBridge {

                                override fun loadImage(context: Context, url: String, imageView: ImageView, isCircle: Boolean) {
                                    if (isCircle) {
                                        Glide.with(context).load(url)
                                                .apply(RequestOptions().transform(CircleCrop())).into(imageView)
                                    } else {
                                        Glide.with(context).load(url).into(imageView)
                                    }
                                }

                                override fun loadIcon(context: Context, url: String) {
                                    GlideUtil.load(context, url)
                                }

                                override fun configImmersiveMode(activity: Activity?) {
                                    activity?.let {
                                        ImmersiveModeUtil.setDefaultImmersiveMode(it)
                                    }
                                }

                                override fun isSupportImmersed(): Boolean {
                                    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                                }

                                override fun setImmersed(activity: Activity?, enable: Boolean) {
                                    activity?.let {
                                        if (enable) {
                                            if (isSupportImmersed()) {
                                                ImmersiveModeUtil.setStatusBarTransparent(it)
                                            } else {
                                                ImmersiveModeUtil.setDefaultImmersiveMode(it)
                                            }
                                        } else {
                                            ImmersiveModeUtil.restorePreviousSystemUiVisibility(it)
                                            ImmersiveModeUtil.quitFullScreen(it)
                                        }
                                    }
                                }

                                override fun showToast(msg: String) {
                                    Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                                }

                                override fun showDialog(context: Context?, dialogParamEntity: DialogParamEntity, onItemClickListener: (position: Int) -> Unit) {
                                    val build = AlertDialog.Builder(context)
                                            .setTitle(dialogParamEntity.title)
                                            .setMessage(dialogParamEntity.message)
                                    dialogParamEntity.buttons?.let {
                                        for ((position, btn) in it.withIndex()) {
                                            when (btn.type) {
                                                DialogType.DEFAULT -> {
                                                    build.setNegativeButton(btn.title) { _: DialogInterface, _: Int ->
                                                        onItemClickListener.invoke(position)
                                                    }
                                                }
                                                DialogType.PRIMARY -> {
                                                    build.setPositiveButton(btn.title) { _: DialogInterface, _: Int ->
                                                        onItemClickListener.invoke(position)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    build.create().show()
                                }

                            })

                            .build())
                    .init(applicationContext)

            //添加自定义插件，如果没有，不调用
            HybridPluginRegister.registerPlugin("battery", BatteryPlugin::class.java)
            //同步cookie，如果不注入cookie，不执行
            MedHybrid.syncCookie(applicationContext)
        }
    }
}