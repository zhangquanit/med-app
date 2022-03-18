package net.medlinker.imbusiness

import android.app.Application
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.blankj.utilcode.util.AppUtils
import com.medlinker.baseapp.ApiPath
import com.medlinker.baseapp.EventType
import com.medlinker.baseapp.route.RoutePath
import com.medlinker.photoviewer.PhotoViewerActivity
import com.medlinker.photoviewer.entity.FileEntity
import com.medlinker.router.MedRouterHelper
import com.medlinker.video.VideoCallManager
import net.medlinker.base.account.AccountUtil
import net.medlinker.base.common.ActivityLifecycle
import net.medlinker.base.event.EventBusUtils
import net.medlinker.base.event.EventMsg
import net.medlinker.base.router.RouterUtil
import net.medlinker.im.MsgConstants
import net.medlinker.im.realm.ImUserDbEntity
import net.medlinker.im.realm.MsgSessionDbEntity
import net.medlinker.im.realm.RealmHelper
import net.medlinker.im.router.ModuleIMManager
import net.medlinker.im.router.ModuleIMService
import net.medlinker.im.router.ModuleRealmService
import net.medlinker.imbusiness.eventbus.IMEventMsg
import net.medlinker.imbusiness.router.ModuleIMBusinessManager
import net.medlinker.imbusiness.router.ModuleIMBusinessService
import net.medlinker.imbusiness.router.ModuleMsgServiceImpl
import net.medlinker.imbusiness.util.NetworkUtil
import net.medlinker.imbusiness.util.NotificationUtil
import org.greenrobot.eventbus.EventBus
import java.util.*


/**
 * @author hmy
 * @time 11/29/21 17:59
 */
enum class MedIMInit {

    INSTANCE;

    fun init(
        context: Application,
        isDebug: Boolean,
        appVersionCode: Int,
        smallIcon: Int,
    ) {
        val userId = AccountUtil.getUserInfo()?.id
        ModuleIMManager.INSTANCE
            .setMsgService(ModuleMsgServiceImpl())
            .setRealmService(object : ModuleRealmService {
                override fun getSchemaVersion(): Int {
                    return appVersionCode  //数据库跟随app版本号
                }

                override fun getRealmName(): String {
                    return "$userId.realm" //数据库名，一般取用户id
                }

                override fun getRealmModule(): MutableList<Any>? {
                    return null
                }

            })
            .imService = object : ModuleIMService {
            override fun getCurrentNetStateCodeStr(context: Context?): String {
                return NetworkUtil.getCurrentNetStateCode(context).toString()
            }

            override fun getApplication(): Application {
                return context
            }

            override fun getCurrentUserId(): Long {
                AccountUtil.getUserInfo()?.id?.let {
                    return it.toLong()
                }
                return 0
            }

            override fun getOrigSession(): String {
                AccountUtil.getLoginInfo()?.sessionId?.let {
                    return it
                }
                return ""
            }

            override fun isVisitor(): Boolean {
                return AccountUtil.getUserInfo() == null
            }

            override fun isAppForeground(): Boolean {
                return ActivityLifecycle.getInstance().activityCount > 0
            }

            override fun getAppName(): String {
                return AppUtils.getAppName()
            }

            override fun getNotificationLollipopSmallIcon(): Int {
                return R.mipmap.ic_lollipop_notify
//                return smallIcon
            }

            override fun getNotificationSmallIcon(): Int {
                return smallIcon
            }

            override fun getStartAppActivity(): Class<*> {
                return RouterUtil.getClass(RoutePath.SPLASH_ACTIVITY)
            }

            override fun dealOffline(msg: String?) {
//                val activity = ActivityStashManager.getCurrentActivity()
//                RouterUtil.startActivity(
//                    activity,
//                    RoutePath.LOGIN_OFFLINE_DIALOG_ACTIVITY,
//                    msg
//                )
                //清除用户缓存后弹框
                EventBusUtils.post(EventMsg(EventType.LOGIN_SESSION_OUT, msg))
            }

            override fun onMsgUnreadCountChanged() {
                EventBus.getDefault()
                    .post(IMEventMsg<Any?>(IMEventMsg.REALM_UNREAD_COUNT_CHANGED))
            }
        }

        ModuleIMBusinessManager.INSTANCE.setApplication(context)
            .setDebug(isDebug)
            .businessService = object : ModuleIMBusinessService {

            override fun getUnLoginErrCode(): Int {
                return 20004
            }

            override fun getCurrentUserId(): Long {
                AccountUtil.getUserInfo()?.id?.let {
                    return it.toLong()
                }
                return 0
            }

            override fun getCurrentUserAvatar(): String? {
                return AccountUtil.getUserInfo()?.avatar
            }

            override fun getWebEnv(): String {
                return ApiPath.getH5Host()
            }

            override fun previewImageView(
                images: ArrayList<CharSequence>?,
                currentPosition: Int
            ) {
                images?.let {
                    val imageList: ArrayList<FileEntity> = ArrayList(images.size)
                    for (image in images) {
                        val fileEntity = FileEntity()
                        fileEntity.fileUrl = image as String?
                        imageList.add(fileEntity)
                    }
                    PhotoViewerActivity.startPhotoViewerActivity(
                        context,
                        imageList,
                        currentPosition
                    )
                }
            }

            override fun gotoWebActivity(context: Context?, url: String?) {
                MedRouterHelper.withUrl("/link?url=$url").queryTarget().navigation(context)
            }

            override fun gotoVideoCall(
                context: Context?, transNo: String?, userId: Long, roomId: Int
            ) {

                val url = java.lang.String.format(
                    "/call/video?userId=%s&userType=%s&roomId=%s", userId, 2, roomId
                )
                MedRouterHelper.withUrl(url).queryTarget().navigation(context)
            }

            override fun transformCurrentUserDb(imUserDbEntity: ImUserDbEntity?): ImUserDbEntity {
                var userDbEntity = imUserDbEntity
                AccountUtil.getUserInfo()?.let {
                    if (userDbEntity == null) {
                        userDbEntity = ImUserDbEntity()
                        userDbEntity!!.id = currentUserId
                    }
                    userDbEntity!!.name = it.name
                    userDbEntity!!.avatar = it.avatar
                    userDbEntity!!.type = MsgConstants.CHAT_USER_TYPE_PATIENT
                }

                return userDbEntity!!
            }

            override fun getNewFriendJumpUri(entity: MsgSessionDbEntity?): Uri? {
                return null
            }

            override fun isJumpToChat(entity: MsgSessionDbEntity?): Boolean {
                return entity?.sessionType == MsgConstants.SESSION_TYPE_SUIZHEN
            }

            override fun getNotJumpToChatUri(entity: MsgSessionDbEntity?): Uri? {
                return null
            }

            override fun getJumpToGroupChatUri(entity: MsgSessionDbEntity?): Uri? {
                return null
            }

            override fun getJumpToSingleChatUri(entity: MsgSessionDbEntity?): Uri? {
                return null
            }

        }

        VideoCallManager.INSTANCE.moduleService = object : VideoCallManager.IModuleService {
            override fun createKeepAliveNotification(
                title: String?,
                content: String?,
                intent: Intent?,
                tag: String?,
                id: Int
            ): Notification? {
                return NotificationUtil.createKeepAliveNotification(title, content, intent, tag, id)
            }

            override fun onVideoCallFinished() {
            }

        }

        RealmHelper.init(context)
        RealmHelper.updateConfig()
    }
}