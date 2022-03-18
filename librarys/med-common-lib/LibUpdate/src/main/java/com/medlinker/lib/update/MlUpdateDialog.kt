package com.medlinker.lib.update

import android.annotation.SuppressLint
import android.text.TextUtils
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.fragment.app.FragmentManager
import com.blankj.utilcode.util.FileUtils
import com.liulishuo.filedownloader.BaseDownloadTask
import com.medlinker.lib.log.LogUtil
import com.medlinker.lib.update.bean.AppVersionEntity
import com.medlinker.lib.update.bean.AppVersionReportEntity
import com.medlinker.lib.update.net.ApiManager
import com.medlinker.lib.update.net.download.ApkDownloader
import com.medlinker.lib.update.net.download.CommonFileDownloader
import com.medlinker.lib.utils.MedAppInfo
import com.medlinker.lib.utils.MedDeviceUtil
import com.medlinker.lib.utils.MedToastUtil
import com.medlinker.widget.dialog.base.MLBaseCenterDialogFragment
import kotlinx.android.synthetic.main.update_dialog_update.view.*
import net.medlinker.base.network.HttpResultFunc
import net.medlinker.base.network.SchedulersCompat
import java.io.File

class MlUpdateDialog: MLBaseCenterDialogFragment() {

    private lateinit var mUpdateInfo: AppVersionEntity
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mLlAction: View
    private lateinit var mLlProgress: View
    private lateinit var mBtnUpdate: Button

    override fun getDialogLayoutId(): Int {
        return R.layout.update_dialog_update
    }

    @SuppressLint("CheckResult")
    override fun setupViews(view: View) {
        mProgressBar = view.pb_bar
        mLlAction = view.ll_action
        mLlProgress = view.ll_progress
        mBtnUpdate = view.btn_update

        isCancelable = false
        mIsCanceledOnTouchOutside=false
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        view.btn_cancel.setOnClickListener {
            dismissAllowingStateLoss()
        }

        view.tv_title.text = mUpdateInfo.title
        view.tv_content.text = mUpdateInfo.describe

        view.tv_content.movementMethod = ScrollingMovementMethod.getInstance()

        if (mUpdateInfo.forceUpgradeStatus) {
            view.btn_cancel.visibility = View.GONE
        }

        view.btn_update.setOnClickListener {
            if (UpgradeUtil.installIfApkDownloaded(MedAppInfo.appContext, mUpdateInfo.downUrl, mUpdateInfo.appVersion)) {
                //已下载好安装包，UI状态处理
                mProgressBar.progress = 100
                mLlProgress.visibility = View.GONE
                mLlAction.visibility = View.VISIBLE
                mBtnUpdate.setText(R.string.update_download_finish)
            } else {
                download(mUpdateInfo)
            }
        }

        val params = HashMap<String, Any>()
        params.put("vid", mUpdateInfo.vid)
        params.put("deviceId", MedDeviceUtil.getDeviceId(MedAppInfo.appContext))

        ApiManager.getNetApi(UpgradeUtil.getApiApp()).upgradeReport(params)
                .map(HttpResultFunc<AppVersionReportEntity>())
                .compose(SchedulersCompat.applyIoSchedulers<AppVersionReportEntity>())
                .subscribe({
                }, { throwable -> throwable.printStackTrace() })
    }

    private fun download(info: AppVersionEntity) {
        mLlProgress.visibility = View.VISIBLE
        mLlAction.visibility = View.GONE

        if (!TextUtils.isEmpty(UpgradeUtil.getDownloadDir())) {
            FileUtils.deleteAllInDir(UpgradeUtil.getDownloadDir())
        }

        ApkDownloader.getInstance().registCallback(object : CommonFileDownloader.IDownloadCallback() {
            override fun onDownloadDone(task: BaseDownloadTask) {
                mLlProgress.visibility = View.GONE
                mLlAction.visibility = View.VISIBLE
                mBtnUpdate.setText(R.string.update_download_finish)
                mProgressBar.progress = 100
                UpgradeUtil.installBySystem(MedAppInfo.appContext, File(task.getPath()))
            }

            override fun onDownloadProgressUpdate(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int, progress: Int) {
                mProgressBar.progress = progress
            }

            override fun warnIsAlreadyDownloading(url: String) {}
            override fun onDownloadError(url: String, path: String) {
                UpgradeUtil.dismiss()
                MedToastUtil.showMessage(MedAppInfo.appContext.getString(R.string.update_download_faliure))
            }
        })
        LogUtil.i("update", String.format("upgrade app url = %s", info.downUrl))
        ApkDownloader.getInstance().executeDownload(info.downUrl, UpgradeUtil.getDownloadedApkFile(info.downUrl, info.appVersion).getAbsolutePath(), info)
    }

    override fun updateViews() {
    }

    override fun isSetDialogBg(): Boolean = false

    fun show(manager: FragmentManager, updateInfo: AppVersionEntity) {
        mUpdateInfo = updateInfo
        showDialog(manager)
    }

    override fun showDialog(manager: FragmentManager) {
        this.show(manager, "Ml_lib_update_Dialog")
    }

    companion object{
        private val mDialog = MlUpdateDialog()
        fun show(manager: FragmentManager, updateInfo: AppVersionEntity): MlUpdateDialog {
            mDialog.show(manager, updateInfo)
            return mDialog
        }
    }
}