package net.medlinker.main.hybrid

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.AppUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.cn.glidelib.GlideUtil
import com.google.gson.Gson
import com.medlinker.baseapp.AppClient
import com.medlinker.baseapp.EventType
import com.medlinker.baseapp.route.RoutePath
import com.medlinker.baseapp.route.RouterInit
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
import com.medlinker.lib.imagepicker.ClipCameraActivity
import com.medlinker.lib.imagepicker.ClipPhotoPickerActivity
import com.medlinker.lib.imagepicker.PhotoPickerActivity
import com.medlinker.lib.imagepicker.PickerConstants
import com.medlinker.lib.imagepicker.entity.ImageEntity
import com.medlinker.lib.log.LogUtil
import com.medlinker.lib.update.UpgradeUtil
import com.medlinker.lib.update.bean.AppVersionEntity
import com.medlinker.lib.utils.MedAppInfo
import com.medlinker.lib.utils.MedImmersiveModeUtil
import com.medlinker.lib.utils.MedToastUtil
import com.medlinker.photoviewer.PhotoViewerActivity
import com.medlinker.router.MedRouterHelper
import com.medlinker.widget.dialog.MLConfirmDialog
import net.medlinker.base.account.AccountUtil
import net.medlinker.base.event.EventBusUtils
import net.medlinker.base.event.EventMsg
import net.medlinker.main.hybrid.plugin.PayPlugin
import org.json.JSONObject


/**
 * @author hmy
 * @time 10/13/21 14:53
 */
object HybridInit {

    @JvmStatic
    fun init(application: Application) {
        BridgeManager
            .setPayBridge(object : PayBridge() {
                override fun payInterceptorWithUrlByAliPay(
                    url: String?,
                    callback: BridgeCallback<Void>
                ) {
                    //h5支付调起支付宝失败后执行
                }

                override fun getWeiXinPayReferer(): String {
                    return super.getWeiXinPayReferer()
                }

            })
            .setLinkingBridge(LinkingBridge())
            .setUpdaterBridge(object : UpdaterBridge {
                override fun checkAppUpdate(context: Context?) {
                    if (context is FragmentActivity) {
                        UpgradeUtil.checkNewVersion(object : UpgradeUtil.UpdateCallback {
                            override fun onUpdateReturned(updateInfo: AppVersionEntity) {
                                if (updateInfo.hasNewVersion()) {
                                    UpgradeUtil.showDialogAlways(
                                        context.supportFragmentManager,
                                        updateInfo
                                    )
                                } else {
                                    MedToastUtil.showMessage(context, "当前已是最新版本")
                                }
                            }
                        })
                    }
                }

            })
            .setRouterBridge(object : RouterBridge {
                override fun route(context: Context?, url: String?, type: String?): Boolean {
                    LogUtil.d("Hybird", "route url=$url")
                    if (TextUtils.isEmpty(url)) {
                        return false
                    }
                    var aroutePath = url
                    val uri = Uri.parse(url)
                    if (RouterInit.hybirdRouteMap.containsKey(uri.path)) {
                        aroutePath = RouterInit.hybirdRouteMap[uri.path]
                        if (!TextUtils.isEmpty(uri.query)) {
                            aroutePath = aroutePath + "?" + uri.query
                        }
                    }
                    LogUtil.d("Hybird", "MedRouter执行跳转 url=$url")
                    MedRouterHelper.withUrl(aroutePath).queryTarget().navigation(context)
                    return false
                }

            })
            .setSocialBridge(object : SocialBridge() {
                override fun onShare(context: Context, shareEntity: ShareEntity) {
//  TODO                  gotoShare(context, shareEntity)
                }

            })
            .setAuthBridge(object : AuthBridge {

                override fun isLogin(): Boolean {
                    return AccountUtil.getUserInfo() != null
                }

                override fun getSessionInfo(): JSONObject? {
                    val jsonObject = JSONObject()
                    jsonObject.put("x_platform", AppClient.clientName)
                    AccountUtil.getLoginInfo()?.let {
                        jsonObject.put("sess", it.sessionId)
                    }
                    return jsonObject
                }

                override fun login(context: Context?, requestCode: Int) {
                    RoutePath.startLoginActivity()
                }

                override fun getUserInfo(callBack: (userInfo: Any?) -> Unit) {
                    callBack.invoke(Gson().toJson(AccountUtil.getUserInfo()))
                }

                override fun logout(context: Context?) {
                    if (context is FragmentActivity) {
//                        DialogLogoutTip()
//                            .showDialog(context)
                        EventBusUtils.post(EventMsg(EventType.LOGIN_SESSION_OUT, "会话过期，请重新登录"))
                    }
                }

            })
            .setDeviceBridge(DeviceBridge())
            .setCameraBridge(object : CameraBridge() {

                override fun onCallCamera(
                    context: Context?,
                    isFrontCamera: Boolean,
                    cropWidth: Int,
                    cropHeight: Int,
                    requestCode: Int
                ) {
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

                override fun onCallAlbum(
                    context: Context?,
                    maxImgCount: Int,
                    cropWidth: Int,
                    cropHeight: Int,
                    requestCode: Int
                ) {
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

                override fun onShowGallery(
                    context: Context?,
                    imagePreviewEntity: ImagePreviewEntity
                ) {
                    val list: List<String> = imagePreviewEntity.images.map {
                        it.url
                    }
                    PhotoViewerActivity.startPhotoViewerActivity(
                        context,
                        list,
                        imagePreviewEntity.index
                    )
                }

                override fun onActivityResultCamera(
                    intent: Intent?,
                    callback: (imagePath: String?) -> Unit
                ) {
                    intent?.let {
                        val imagePath = intent.getStringExtra(PickerConstants.BUNDLE_PARAMS)
                        if (!TextUtils.isEmpty(imagePath)) {
                            callback.invoke(imagePath)
                        }
                    }
                }

                override fun onActivityResultAlbum(
                    intent: Intent?,
                    callback: (imagePaths: ArrayList<String?>) -> Unit
                ) {
                    intent?.let {
                        val imagePath = intent.getStringExtra(PickerConstants.BUNDLE_PARAMS)
                        if (!TextUtils.isEmpty(imagePath)) {
                            val list = ArrayList<String?>(1)
                            list.add(imagePath)
                            callback.invoke(list)
                            return
                        }
                        val imageList =
                            intent.getParcelableArrayListExtra<ImageEntity>("SELECTED_PHOTOS")
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

        val clientName = AppClient.clientName  //orthopaedicsApp
        val ua = " ${clientName}/${MedAppInfo.versionName} medhybrid"
        val host = "medlinker.com"
        MedHybrid.setAppVersionCode(MedAppInfo.versionCode)
            .setAppVersionName(MedAppInfo.versionName)
            .setH5CookieDomain(host)
            .setOpenLog(MedAppInfo.isDebug)
            .setUserAgentString(ua)
            .setNavigationConfig(NavigationConfig(AppUtils.getAppName()))
            .setConfig(
                MedHybrid.Config.Builder()
                    .setWebViewAdapter(object : IWebViewAdapter() {

                        override fun getCookieParams(): Array<String>? {
                            return null
                        }

                        override fun log(msg: String?, tr: Throwable?) {
                            LogUtil.d("MedHybrid", msg!!)
                        }

                    })
                    .setUIAdapter(
                        object : IWVUIBridge {

                            override fun loadIcon(context: Context, url: String) {
                                GlideUtil.load(context, url)
                            }

                            override fun loadImage(
                                context: Context,
                                url: String,
                                imageView: ImageView,
                                isCircle: Boolean
                            ) {
                                if (isCircle) {
                                    Glide.with(context).load(url)
                                        .apply(RequestOptions().transform(CircleCrop()))
                                        .into(imageView)
                                } else {

                                    Glide.with(context).load(url)
                                        .into(imageView)
                                }
                            }

                            override fun configImmersiveMode(activity: Activity?) {
                                activity?.let {
                                    MedImmersiveModeUtil.setDefaultImmersiveMode(it)
                                }
                            }

                            override fun isSupportImmersed(): Boolean {
                                return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            }

                            override fun setImmersed(activity: Activity?, enable: Boolean) {
                                activity?.let {
                                    if (enable) {
                                        if (isSupportImmersed()) {
                                            MedImmersiveModeUtil.setStatusBarTransparent(it)
                                        } else {
                                            MedImmersiveModeUtil.setDefaultImmersiveMode(it)
                                        }
                                    } else {
                                        MedImmersiveModeUtil.restorePreviousSystemUiVisibility(
                                            it
                                        )
                                        MedImmersiveModeUtil.quitFullScreen(it)
                                    }
                                }
                            }

                            override fun showToast(msg: String) {
                                Toast.makeText(application, msg, Toast.LENGTH_SHORT).show()
                            }

                            override fun showDialog(
                                context: Context?,
                                dialogParamEntity: DialogParamEntity,
                                onItemClickListener: (position: Int) -> Unit
                            ) {
                                val dialog = MLConfirmDialog
                                    .newInstance()
                                    .setTitle(dialogParamEntity.title)
                                    .setMessage(dialogParamEntity.message)
                                dialogParamEntity.buttons?.let {
                                    for ((position, btn) in it.withIndex()) {
                                        when (btn.type) {
                                            DialogType.DEFAULT -> {
                                                dialog.setCancelButton(btn.title) {
                                                    onItemClickListener.invoke(position)
                                                }
                                            }
                                            DialogType.PRIMARY -> {
                                                dialog.setConfirmButton(btn.title) {
                                                    onItemClickListener.invoke(position)
                                                }
                                            }
                                        }
                                    }
                                }
                                if (context is FragmentActivity) {
                                    dialog.showDialog(context.supportFragmentManager)
                                }
                            }

                        })

                    .build()
            )
            .init(application)

        //自定义插件
        HybridPluginRegister.registerPlugin(
            "payment",
            PayPlugin::class.java
        )
    }
}