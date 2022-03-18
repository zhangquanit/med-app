package com.medlinker.lib.share

import android.app.Activity
import com.medlinker.lib.share.exception.NotSupportOptException
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMAuthListener
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.*
import java.io.File

class SharePlatform(val mPlatform: SHARE_MEDIA, private val mPlatformName: String) {

    private val mAuthListener = UMAuthListenerImpl()
    private val mShareListenerIner = UMShareListenerImpl()
    private var mShareListener: ShareListener? = null
    private val mSupportOpt: List<ShareType> = ShareType.getSupportType(mPlatformName)

    fun setListener(listener: ShareListener?) {
        mShareListener = listener
        mShareListenerIner.setListener(mShareListener)
    }

    /**
     * 判断平台是否有效
     */
    fun isClientValid(activity: Activity): Boolean {
        return UMShareAPI.get(activity).isInstall(activity, mPlatform)
    }

    /**
     * 删除授权
     */
    fun deleteAuth(activity: Activity, listener: AuthListener?) {
        if (!mSupportOpt.contains(ShareType.AUTH)) {
            listener?.onError(mPlatformName, 0, NotSupportOptException(ShareType.AUTH.des))
            return
        }
        mAuthListener.setListener(listener)
        UMShareAPI.get(activity).deleteOauth(activity, mPlatform, mAuthListener)
    }

    /**
     * 登陆授权
     */
    fun auth(activity: Activity, listener: AuthListener?) {
        if (!mSupportOpt.contains(ShareType.AUTH)) {
            listener?.onError(mPlatformName, 0, NotSupportOptException(ShareType.AUTH.des))
            return
        }

        mAuthListener.setListener(listener)
        UMShareAPI.get(activity).getPlatformInfo(activity, mPlatform, mAuthListener)

    }

    private fun permissionNot(type: ShareType): Boolean {
        if (!mSupportOpt.contains(type)) {
            mShareListener?.onError(mPlatformName, NotSupportOptException(type.des))
            return true
        }

        return false
    }

    /**
     * 分享纯文本
     */
    fun shareText(activity: Activity, text: String?) {
        if (permissionNot(ShareType.TEXT)) {
            return
        }

        ShareAction(activity).withText(text ?: "")
                .setPlatform(mPlatform)
                .setCallback(mShareListenerIner).share()

    }

    /**
     * 分享本地图片
     */
    fun shareImageLocal(activity: Activity, logo: Int, thumb: Int) {
        if (permissionNot(ShareType.IMAGE_LOCAL)) {
            return
        }
        if (logo <= 0) {
            mShareListener?.onError(mPlatformName, IllegalArgumentException("invalid url"))
            return
        }

        val imagelocal = UMImage(activity, logo)
        if (logo > 0) {
            imagelocal.setThumb(UMImage(activity, thumb))
        }

        ShareAction(activity).withMedia(imagelocal)
                .setPlatform(mPlatform)
                .setCallback(mShareListenerIner).share()

    }

    /**
     * 分享网络图片
     */
    fun shareImageNet(activity: Activity, url: String?, thumb: Int) {
        if (permissionNot(ShareType.IMAGE_URL)) {
            return
        }

        if (null == url || !url.startsWith("http")) {
            mShareListener?.onError(mPlatformName, IllegalArgumentException("invalid url"))
            return
        }

        val imageurl = UMImage(activity, url)
        if (thumb > 0) {
            imageurl.setThumb(UMImage(activity, thumb))
        }
        ShareAction(activity).withMedia(imageurl)
                .setPlatform(mPlatform)
                .setCallback(mShareListenerIner).share()


    }

    fun shareUrl(activity: Activity, url: String?, thumb: Int, title: String?, des: String?) {
        if (permissionNot(ShareType.WEB)) {
            return
        }

        if (null == url || !url.startsWith("http")) {
            mShareListener?.onError(mPlatformName, IllegalArgumentException("invalid url"))
            return
        }


        val web = UMWeb(url)
        //web.title = title?:""
        web.title = title

        if (thumb > 0) {
            web.setThumb(UMImage(activity, thumb))
        }

        //web.description = des?:""
        web.description = des

        ShareAction(activity).withMedia(web)
                .setPlatform(mPlatform)
                .setCallback(mShareListenerIner).share()


    }

    fun shareMusic(activity: Activity, url: String?, thumb: Int, title: String?, des: String?) {
        if (permissionNot(ShareType.MUSIC)) {
            return
        }

        if (null == url || !url.startsWith("http")) {
            mShareListener?.onError(mPlatformName, IllegalArgumentException("invalid url"))
            return
        }


        val music = UMusic(url)

        music.title = title?:""


        if (thumb > 0) {
            music.setThumb(UMImage(activity, thumb))
        }


        music.description = des?:""

        music.setmTargetUrl(url)
        ShareAction(activity).withMedia(music)
                .setPlatform(mPlatform)
                .setCallback(mShareListenerIner).share()


    }

    fun shareVideo(activity: Activity, url: String?, thumb: Int, title: String?, des: String?) {
        if (permissionNot(ShareType.VIDEO)) {
            return
        }

        if (null == url || !url.startsWith("http")) {
            mShareListener?.onError(mPlatformName, IllegalArgumentException("invalid url"))
            return
        }


        val video = UMVideo(url)

        video.title = title?:""

        if (thumb > 0) {
            video.setThumb(UMImage(activity, thumb))
        }

        video.description = des?:""

        ShareAction(activity).withMedia(video)
                .setPlatform(mPlatform)
                .setCallback(mShareListenerIner).share()

    }

    fun shareLocalFile(activity: Activity, file: File?, title: String?, des: String?) {
        if (permissionNot(ShareType.FILE)) {
            return
        }

        if (null == file || !file.exists()) {
            mShareListener?.onError(mPlatformName, IllegalArgumentException("invalid file"))
            return
        }

        val action = ShareAction(activity)
                .withFile(file)
        if (null != des) {
            action.withText(des)
        }
        if (null != title) {
            action.withSubject(title)
        }
        action.setPlatform(mPlatform)
        action.setCallback(mShareListenerIner).share()


    }

    fun shareLocalVideo(activity: Activity, file: File?, des: String?, title: String?) {
        if (permissionNot(ShareType.VIDEO_LOCAL)) {
            return
        }

        if (null == file || !file.exists()) {
            mShareListener?.onError(mPlatformName, IllegalArgumentException("invalid file"))
            return
        }


        val action = ShareAction(activity)
                .withMedia(UMVideo(file))
        if (null != des) {
            action.withText(des)
        }

        if (null != title) {
            action.withSubject(title)
        }
        action.setPlatform(mPlatform)
        action.setCallback(mShareListenerIner).share()

    }

    fun shareTextAndImage(activity: Activity, imageId: Int, thumb: Int, text: String?) {
        if (permissionNot(ShareType.TEXT_AND_IMAGE)) {
            return
        }

        if (imageId <= 0 && null == text) {
            mShareListener?.onError(mPlatformName, IllegalArgumentException("invalid text and imge"))
            return

        }


        val action = ShareAction(activity)
        if (imageId > 0) {
            val imageLocal = UMImage(activity, imageId)
            if (thumb > 0) {
                imageLocal.setThumb(UMImage(activity, thumb))
            }
            action.withMedia(imageLocal)
        }

        if (null != text) {
            action.withText(text)
        }
        action.setPlatform(mPlatform)
        action.setCallback(mShareListenerIner).share()

    }


    fun shareEmoji(activity: Activity, image: Int, thumb: Int) {
        if (permissionNot(ShareType.EMOJI)) {
            return
        }

        if (image <= 0 && thumb <= 0) {
            mShareListener?.onError(mPlatformName, IllegalArgumentException("invalid image and thumb"))
            return

        }


        val emoji = UMEmoji(activity, image)
        if (thumb > 0) {
            emoji.setThumb(UMImage(activity, thumb))
        }
        ShareAction(activity)
                .withMedia(emoji)
                .setPlatform(mPlatform)
                .setCallback(mShareListenerIner).share()


    }

    inner class UMAuthListenerImpl : UMAuthListener {

        private var mListener: AuthListener? = null

        fun setListener(listener: AuthListener?) {
            mListener = listener
        }

        override fun onStart(p0: SHARE_MEDIA?) {
            mListener?.onStart(mPlatformName)
        }

        override fun onComplete(p0: SHARE_MEDIA?, p1: Int, p2: MutableMap<String, String>?) {
            mListener?.onComplete(mPlatformName, p1, p2)
        }

        override fun onError(p0: SHARE_MEDIA?, p1: Int, p2: Throwable?) {
            mListener?.onError(mPlatformName, p1, p2)
        }

        override fun onCancel(p0: SHARE_MEDIA?, p1: Int) {
            mListener?.onCancel(mPlatformName, p1)
        }
    }

    inner class UMShareListenerImpl : UMShareListener {

        private var mListener: ShareListener? = null

        fun setListener(listener: ShareListener?) {
            mListener = listener
        }

        override fun onStart(p0: SHARE_MEDIA?) {
            mListener?.onStart(mPlatformName)
        }

        override fun onResult(p0: SHARE_MEDIA?) {
            mListener?.onComplete(mPlatformName)
        }

        override fun onError(p0: SHARE_MEDIA?, p1: Throwable?) {
            mListener?.onError(mPlatformName, p1)
        }

        override fun onCancel(p0: SHARE_MEDIA?) {
            mListener?.onCancel(mPlatformName)
        }
    }
}