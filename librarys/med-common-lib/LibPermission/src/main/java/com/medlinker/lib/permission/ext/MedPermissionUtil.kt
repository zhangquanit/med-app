package com.medlinker.lib.permission.ext

import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.IntentUtils
import com.medlinker.lib.permission.Permission
import com.medlinker.lib.permission.PermissionUtil
import com.medlinker.lib.permission.R
import com.medlinker.widget.dialog.MLConfirmDialog

/**
 * 一、全局配置文案
 * <pre class="prettyprint">
 *    MedPermissionConfig.addExplain(Manifest.permission.WRITE_EXTERNAL_STORAGE,"修改用户头像、聊天发图片时访问存储权限")
 *    MedPermissionConfig.addExplain(Manifest.permission.CAMERA,"视频聊天、发送图片消息、扫一扫时访问相机权限")
 * </pre>
 * 二、使用全局配置文案
 * <pre class="prettyprint">
 *             MedPermissionUtil(this)
 *                .requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
 *                .onResult {
 *                    if (it) {
 *                         //所有权限申请成功
 *                    }
 *                }
 * </pre>
 * 完整调用  自定义文案
 * <pre class="prettyprint">
 *             MedPermissionUtil(this)
 *                .requestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
 *                .explain {
 *                    it.content="当您使用APP时，会在修改用户头像、发送图片消息、饮食打卡时访问相机权限。不授权上述权限，不影响APP其他功能使用。"
 *                    false  //是否在申请权限前向用户解释
 *                }
 *                .showRationalDialogWhenRejectedForever { rationInfo, rejectedForever ->
 *                    rationInfo.content = "为保证正常使用功能，请打开储存权限"
 *                    true  //当用户拒绝授权(永远)，是否显示自定义弹框引导用户授权
 *                }
 *                .onResult {
 *                    if (it) {
 *                         //所有权限申请成功
 *                    }
 *                }
 * </pre>
 *
 * @author zhangquan
 */
class MedPermissionUtil(activity: FragmentActivity) {
    private var mContext: FragmentActivity = activity
    private var mRequestPermissions: Array<out String> = arrayOf()

    fun requestPermissions(vararg permissions: String): MedPermissionExplain {
        mRequestPermissions = permissions
        return MedPermissionExplain()
    }


    inner class MedPermissionExplain {
        internal lateinit var mCallback: ((info: MedPermissionInfo) -> Boolean)

        fun explain(listener: (info: MedPermissionInfo) -> Boolean): MedPermissionRational {
            mCallback = listener
            return MedPermissionRational(this)
        }

        fun onResult(listener: ((allGranted: Boolean) -> Unit)) {
            mCallback = {
                true
            }
            MedPermissionRational(this)
                .showRationalDialogWhenRejectedForever { _, _ ->
                    true
                }.onResult(listener)
        }
    }


    inner class MedPermissionRational(explain: MedPermissionExplain) {
        internal var mPermissionExplain = explain
        internal lateinit var mCallback: ((rationInfo: MedPermissionInfo, rejectedForever: List<Permission>) -> Boolean)

        fun showRationalDialogWhenRejectedForever(listener: ((rationInfo: MedPermissionInfo, rejectedForever: List<Permission>) -> Boolean)): MedPermissionResult {
            mCallback = listener
            return MedPermissionResult(this)
        }

    }

    inner class MedPermissionResult(rational: MedPermissionRational) {
        private var mPermissionRational = rational
        private var mExplainDialog: Dialog? = null
        private var mRationPermissionMap = HashMap<String, Boolean>()
        private lateinit var mCallback: ((allGranted: Boolean) -> Unit)
        fun onResult(listener: ((allGranted: Boolean) -> Unit)) {
            mCallback = listener

            var allPermissionGranted = true
            var explanContent = StringBuilder()
            val explanInfos = MedPermissionConfig.getExplanInfos()
            mRequestPermissions.forEach {
                if (ActivityCompat.checkSelfPermission(
                        mContext,
                        it
                    ) != PackageManager.PERMISSION_GRANTED
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                ) {
                    if (explanInfos.containsKey(it)) {
                        explanContent.append(explanInfos[it]!!).append("，")
                    }
                    val showRational = mContext.shouldShowRequestPermissionRationale(it)
                    if (!showRational) {
                        mRationPermissionMap[it] = showRational
                    }
                    allPermissionGranted = false
                }
            }

            if (allPermissionGranted) {
                request()
                return
            }
            val info = MedPermissionInfo("", "")
            val showExplain = mPermissionRational.mPermissionExplain.mCallback.invoke(info)
            if (showExplain) { //权限弹框说明

                if (TextUtils.isEmpty(info.title)) {
                    info.title = mContext.resources.getString(R.string.permission_explain_title)
                }

                if (TextUtils.isEmpty(info.content)) {
                    var content = explanContent.toString()
                    if (!TextUtils.isEmpty(content)) {
                        content = content.substring(0, content.length - 1)
                        content = String.format(
                            mContext.resources.getString(R.string.permission_explain_content),
                            content
                        )
                    } else {
                        content =
                            mContext.resources.getString(R.string.permission_explain_content_default)
                    }
                    info.content = content
                }

                mExplainDialog =
                    MedPermissionExplainDialog(
                        mContext,
                        info
                    )
                mExplainDialog?.show()
                request()

            } else {
                request()
            }

        }

        private fun request() {
            PermissionUtil(mContext)
                .requestPermissiones(mRequestPermissions)
                .onResult { _, rejected ->

                    val allGranted = rejected.isEmpty()
                    if (allGranted) {
                        mExplainDialog?.dismiss()
                        mCallback.invoke(true)
                        return@onResult
                    }

                    val mRejectMap = HashMap<String, Boolean>()
                    val rejectedForever = arrayListOf<Permission>()
                    rejected.forEach {
                        if (!it.mShouldShowRational) {
                            rejectedForever.add(it)
                        }
                        mRejectMap[it.mPermission] = it.mShouldShowRational
                    }
                    var showRationDialog = mRationPermissionMap.equals(mRejectMap)
                    val info = MedPermissionInfo("", "")
                    if (rejected.size == rejectedForever.size) { //当用户拒绝授权(永远)
                        showRationDialog = mPermissionRational.mCallback.invoke(
                            info,
                            rejectedForever
                        ) && showRationDialog
                    }
                    //显示自定义弹框 去打开权限
                    if (showRationDialog) {
                        if (TextUtils.isEmpty(info.title)) {
                            info.title = mContext.resources.getString(R.string.permission_title)
                        }
                        if (TextUtils.isEmpty(info.content)) {
                            info.content = String.format(
                                mContext.resources.getString(R.string.permission_rational_content),
                                AppUtils.getAppName()
                            )

                        }
                        val dialog = MLConfirmDialog.newInstance()
                            .setTitle(info.title)
                            .setMessage(info.content)
                            .setConfirmButton("去设置")
                            .setConfirmButtonListener {
                                mContext.startActivity(
                                    IntentUtils.getLaunchAppDetailsSettingsIntent(
                                        AppUtils.getAppPackageName()
                                    )
                                )
                            }
                            .setCancelButton("取消")
                            .setCancelButtonListener {

                            }
                        dialog.isCancelable = false
                        dialog.setCanceledOnTouchOutside(false)
                        dialog.setOnDialogDismissListener {
                            mExplainDialog?.dismiss()
                            mCallback.invoke(false)
                        }
                        dialog.showDialog(
                            mContext.supportFragmentManager,
                            "PermissionExplainDialog"
                        )
                    } else {
                        mExplainDialog?.dismiss()
                        mCallback.invoke(false)
                    }
                }
        }
    }

}