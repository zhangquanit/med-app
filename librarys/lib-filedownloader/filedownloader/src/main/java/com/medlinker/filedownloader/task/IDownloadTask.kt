package com.medlinker.filedownloader.task

import com.medlinker.filedownloader.entity.MedFileEntity

/**
 * 文件下载任务
 * @author zhangquan
 */
interface IDownloadTask {
    fun isCompleted(): Boolean
    fun cancel()
    fun start(callback: FileDownloadCallback)
    fun getInfo(): MedFileEntity
}