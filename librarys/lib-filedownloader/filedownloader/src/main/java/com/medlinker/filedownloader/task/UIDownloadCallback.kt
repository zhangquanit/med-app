package com.medlinker.filedownloader.task

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Message
import com.medlinker.filedownloader.entity.MedFileEntity
import com.medlinker.filedownloader.util.MedDownloadUtil

/**
 * 基于android UI线程回调
 * @author zhangquan
 */
class UIDownloadCallback : FileDownloadCallback {

    private var mCallback: FileDownloadCallback? = null
    private var mHandler: CallbackHandler? = null
    private var mDBEntity: MedFileEntity? = null

    constructor(entity: MedFileEntity?, callback: FileDownloadCallback?) {
        mDBEntity = entity
        mCallback = callback
        mHandler = CallbackHandler()
    }

    override fun onDownloadProgress(progress: Int, offset: Long, total: Long) {
        mHandler?.sendMessage(
            mHandler!!.obtainMessage(
                progressInfo,
                progress, offset.toInt()
            )
        )
    }

    override fun onDownloadFail() {
        MedDownloadUtil.log("${mDBEntity?.fileName} onDownloadFail")
        mHandler?.sendEmptyMessage(fail)
    }

    override fun onDownloadSuccess() {
        MedDownloadUtil.log("${mDBEntity?.fileName} onDownloadSuccess")
        mHandler?.sendEmptyMessage(success)
    }

    fun remove() {
        mHandler?.removeCallbacksAndMessages(null)
        mCallback = null
    }

    @SuppressLint("HandlerLeak")
    inner class CallbackHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                progressInfo -> {
                    mCallback?.onDownloadProgress(
                        msg.arg1,
                        msg.arg2.toLong(),
                        mDBEntity!!.total
                    )
                }
                success -> {
                    mCallback?.onDownloadSuccess()
                }
                fail -> {
                    mCallback?.onDownloadFail()
                }
            }
        }
    }

    companion object {
        private const val progressInfo = 1
        private const val success = 2
        private const val fail = 3
    }
}