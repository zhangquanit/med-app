package com.medlinker.lib.share

import android.app.Activity
import android.app.Application
import android.content.Intent
import com.umeng.commonsdk.UMConfigure
import com.umeng.socialize.PlatformConfig
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.bean.SHARE_MEDIA

object ShareSdkHelper {

    private val mPlatforms = HashMap<String, SharePlatform>()


    private lateinit var sContext: Application


    //微信好友
    @JvmField
    val PLATFORM_WECHAT = "wechat"

    //微信朋友圈
    @JvmField
    val PLATFORM_WECHAT_CIRCLE = "wechat_circle"

    //qq好友
    @JvmField
    val PLATFORM_QQ = "qq"

    //qq空间
    @JvmField
    val PLATFORM_QQ_ZONE = "qq_zone"

    //新浪微博
    @JvmField
    val PLATFORM_SINA = "sina"

    //短信
    @JvmField
    val PLATFORM_SMS = "sms"


    @JvmStatic
    fun initUM(context: Application, appKey: String, channel: String): ShareSdkHelper {
        //初始化组件化基础库, 统计SDK/推送SDK/分享SDK都必须调用此初始化接口
        sContext = context
        UMConfigure.init(
                context.applicationContext,
                appKey,
                channel,
                UMConfigure.DEVICE_TYPE_PHONE,
                "")
        UMShareAPI.init(context, appKey)

        mPlatforms.clear()
        mPlatforms.put(PLATFORM_SMS, SharePlatform(SHARE_MEDIA.SMS, PLATFORM_SMS))
        return this
    }

    fun showLog(isShow: Boolean): ShareSdkHelper {
        UMConfigure.setLogEnabled(isShow)
        return this
    }

    fun initWx(appId: String, appKey: String): ShareSdkHelper {
        PlatformConfig.setWeixin(appId, appKey)
        PlatformConfig.setWXFileProvider("${sContext.packageName}.fileprovider")

        mPlatforms.put(PLATFORM_WECHAT, SharePlatform(SHARE_MEDIA.WEIXIN, PLATFORM_WECHAT))
        mPlatforms.put(PLATFORM_WECHAT_CIRCLE, SharePlatform(SHARE_MEDIA.WEIXIN_CIRCLE, PLATFORM_WECHAT_CIRCLE))

        return this
    }


    fun initQQ(appId: String, appKey: String): ShareSdkHelper {
        PlatformConfig.setQQZone(appId, appKey)
        PlatformConfig.setQQFileProvider("${sContext.packageName}.fileprovider")

        mPlatforms.put(PLATFORM_QQ, SharePlatform(SHARE_MEDIA.QQ, PLATFORM_QQ))
        mPlatforms.put(PLATFORM_QQ_ZONE, SharePlatform(SHARE_MEDIA.QZONE, PLATFORM_QQ_ZONE))

        return this
    }

    @JvmOverloads
    fun initWeibo(
            appId: String,
            appKey: String,
            callbackUrl: String = "http://sns.whalecloud.com/sina2/callback"
    ): ShareSdkHelper {
        PlatformConfig.setSinaWeibo(appId, appKey, callbackUrl)
        PlatformConfig.setSinaFileProvider("${sContext.packageName}.fileprovider")

        mPlatforms.put(PLATFORM_SINA, SharePlatform(SHARE_MEDIA.SINA, PLATFORM_SINA))

        return this
    }

    @JvmStatic
    fun getPlatforms(): List<SharePlatform> {
        return arrayListOf<SharePlatform>().apply {
            addAll(mPlatforms.values)
        }
    }

    @JvmStatic
    fun getPlatform(platformName: String): SharePlatform? = mPlatforms[platformName]

    @JvmStatic
    fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) {
        UMShareAPI.get(activity).onActivityResult(requestCode, resultCode, data)
    }

    @JvmStatic
    fun getPlatformName(uShareMedia: SHARE_MEDIA?): String? {
        return when (uShareMedia) {
            SHARE_MEDIA.SINA -> PLATFORM_SINA
            SHARE_MEDIA.WEIXIN -> PLATFORM_WECHAT
            SHARE_MEDIA.WEIXIN_CIRCLE -> PLATFORM_WECHAT_CIRCLE
            SHARE_MEDIA.QQ -> PLATFORM_QQ
            SHARE_MEDIA.QZONE -> PLATFORM_QQ_ZONE
            SHARE_MEDIA.SMS -> PLATFORM_SMS
            else -> null
        }
    }

    @JvmStatic
    fun getPlatformFromName(name: String): SHARE_MEDIA? {
        return when (name) {
            PLATFORM_QQ_ZONE -> SHARE_MEDIA.QZONE
            PLATFORM_QQ -> SHARE_MEDIA.QQ
            PLATFORM_WECHAT_CIRCLE -> SHARE_MEDIA.WEIXIN_CIRCLE
            PLATFORM_WECHAT -> SHARE_MEDIA.WEIXIN
            PLATFORM_SINA -> SHARE_MEDIA.SINA
            PLATFORM_SMS -> SHARE_MEDIA.SMS
            else -> null
        }
    }


}