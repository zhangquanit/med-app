package com.medlinker.filedownloader.task

/**
 * 文件下载回调
 * @author zhangquan
 */
interface FileDownloadCallback {
    fun onDownloadProgress(progress: Int, offset: Long, total: Long)
    fun onDownloadFail()
    fun onDownloadSuccess()
}