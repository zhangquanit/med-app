package com.medlinker.lib.update

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.MetaDataUtils
import com.blankj.utilcode.util.Utils
import com.google.gson.Gson
import com.medlinker.lib.log.LogUtil
import com.medlinker.lib.update.bean.AppVersionEntity
import com.medlinker.lib.update.net.ApiManager
import com.medlinker.lib.utils.MedAppInfo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import net.medlinker.base.network.HttpResultFunc
import net.medlinker.base.network.SchedulersCompat
import java.io.File

object UpgradeUtil {
    private val VERSION_INFO = "server_version_info"
    private var mUpdateDialog: MlUpdateDialog? = null
    private var mDispose: Disposable? = null
    private val mGson = Gson()
    private var mHost: String? = null


    private val mSp: SharedPreferences =
        MedAppInfo.appContext.getSharedPreferences("app_version_info", Context.MODE_PRIVATE)

    fun getAppkey(): String {
        return MetaDataUtils.getMetaDataInApp("app_update_key")
    }

    fun setHost(host: String): UpgradeUtil {
        mHost = host
        return this
    }

     fun getDownloadDir(): String {
        val apkDir = File(Utils.getApp().externalCacheDir, "apk")
        if (!apkDir.exists()) apkDir.mkdirs()
        return apkDir.absolutePath
    }

    /**
     * @return
     */
    fun getDownloadedApkFile(url: String, versionName: String): File {
        val fileName = url.hashCode().toString() + "-" + versionName.hashCode()
        LogUtil.i(
            String.format(
                "getDownloadedApkFile url = %s , versionName = %s , fileName = %s",
                url,
                versionName,
                fileName
            )
        )
        return File(getDownloadDir(), "$fileName.apk")
    }

    fun getDownloadTempApkFile(url: String, versionName: String): File {
        val fileName = url.hashCode().toString() + "-" + versionName.hashCode()
        LogUtil.i(
            String.format(
                "getDownloadTempApkFile url = %s , versionName = %s , fileName = %s",
                url,
                versionName,
                fileName
            )
        )
        return File(getDownloadDir(), "$fileName.tmp")
    }


    fun checkNewVersion(callback: UpdateCallback?) {
        if (null != mDispose && !mDispose!!.isDisposed) {
            return
        }

        mDispose = ApiManager.getNetApi(mHost ?: getApiApp())
            .getAppInfo(getAppkey(), MedAppInfo.versionName)
            .map(HttpResultFunc<AppVersionEntity>())
            .compose(SchedulersCompat.applyIoSchedulers<AppVersionEntity>())
            .subscribe({ versionEntity ->
                if (versionEntity != null && versionEntity.versionCode > MedAppInfo.versionCode) {
                    mSp.edit().putString(VERSION_INFO, mGson.toJson(versionEntity)).apply()
                }
                callback?.onUpdateReturned(versionEntity)
            }, { throwable ->
                throwable.printStackTrace()
            })
    }

    fun getApiApp(): String {
        var url = "https://api-dev.medlinker.com"
        when (MedAppInfo.envType) {
            4 -> url = "https://api-qa.medlinker.com"
            3 -> url = "https://api.medlinker.com"
        }
        return url
    }

    interface UpdateCallback {
        fun onUpdateReturned(updateInfo: AppVersionEntity)
    }

    fun hasNewVersion(): Boolean {
        try {
            val versionEntity =
                mGson.fromJson(mSp.getString(VERSION_INFO, ""), AppVersionEntity::class.java)
            return hasNewVersion(versionEntity)
        } catch (e: java.lang.Exception) {
        }

        return false
    }

    private fun hasNewVersion(upgradeInfo: AppVersionEntity) =
        upgradeInfo.versionCode > MedAppInfo.versionCode

    fun showDialog(fragmentManager: FragmentManager, updateInfo: AppVersionEntity) {
        if (updateInfo.pop || updateInfo.forceUpgradeStatus) {
            mUpdateDialog = MlUpdateDialog.show(fragmentManager, updateInfo)
        }
    }

    fun showDialogAlways(fragmentManager: FragmentManager, updateInfo: AppVersionEntity) {
        mUpdateDialog = MlUpdateDialog.show(fragmentManager, updateInfo)
    }

    /**
     * 检查要下载的版本是否已经存在，若存在直接安装
     *
     * @return
     */
    fun installIfApkDownloaded(context: Context, url: String, versionName: String): Boolean {
        val apk = getDownloadedApkFile(url, versionName)
        val exist = apk.exists() && apk.length() > 0
        if (exist) {
            installBySystem(context, apk)
        }
        return exist
    }

    /**
     * 交给系统安装apk
     *
     * @param context
     */
    fun installBySystem(context: Context, file: File?) {
        try {
            val intent = Intent()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val uri = FileProvider.getUriForFile(
                    context,
                    MedAppInfo.applicationId + ".provider",
                    file!!
                )
                intent.setDataAndType(uri, "application/vnd.android.package-archive")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.action = Intent.ACTION_VIEW
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            FileUtils.delete(file)
        }
    }

    fun dismiss() {
        if (null != mUpdateDialog && !mUpdateDialog!!.isDetached) {
            mUpdateDialog!!.dismiss()
        }
        mUpdateDialog = null
    }
}