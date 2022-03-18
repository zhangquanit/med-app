package com.medlinker.filedownloader.call

import com.blankj.utilcode.util.NetworkUtils
import com.medlinker.filedownloader.entity.MedFileEntity
import com.medlinker.filedownloader.task.FileDownloadCallback
import com.medlinker.filedownloader.util.MedDownloadUtil
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 *
 * @author zhangquan
 */
abstract class AbstractDownloadCall : FileDownloadCall {
    protected var mRetryCount = AtomicInteger(3)
    protected var mCallback: FileDownloadCallback? = null
    protected var mCanceld = AtomicBoolean(false)

    override fun start(entity: MedFileEntity, callback: FileDownloadCallback?, retryCount: Int) {
        mCallback = callback
        mRetryCount.set(retryCount)
        mCanceld.set(false)
        MedFileClient.getThreadPool().execute {
            executeTask(entity)
        }
    }

    private fun executeTask(entity: MedFileEntity) {
        try {
            if (NetworkUtils.isConnected()) {
                doExecute(entity)
            } else {
                if (!isCanceld()) mCallback?.onDownloadFail()
            }
        } catch (e: Exception) {
            MedDownloadUtil.log("${entity.fileName} 下载失败 retryCount=$mRetryCount isCanceld=${isCanceld()}  exception=${e.message}")
            if (!isCanceld()) {
                if (mRetryCount.get()==0) {
                    mCallback?.onDownloadFail()
                } else {
                    mRetryCount.getAndDecrement()
                    executeTask(entity)
                }
            }
        }
    }

    protected abstract fun doExecute(entity: MedFileEntity)
    protected abstract fun isCanceld(): Boolean
}