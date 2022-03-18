package com.medlinker.filedownloader.call

import com.medlinker.filedownloader.entity.MedFileEntity
import com.medlinker.filedownloader.task.FileDownloadCallback

/**
 * 执行文件下载
 * @author zhangquan
 */
interface FileDownloadCall {
    fun start(entity: MedFileEntity, callback: FileDownloadCallback?, retryCount:Int)

    fun stop()
}