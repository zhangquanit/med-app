package com.medlinker.filedownloader.call

import com.medlinker.filedownloader.entity.MedFileEntity
import com.medlinker.filedownloader.db.ModelDao
import com.medlinker.filedownloader.util.MedDownloadUtil
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 基于HttpUrlConnection下载
 * @author zhangquan
 */
class HttpUrlConnectionCall : AbstractDownloadCall() {
    private var mConn: HttpURLConnection? = null

    override fun doExecute(entity: MedFileEntity) {
        val file = File(entity.parentFile, entity.fileName)
        val breakPoint = file.length()
        MedDownloadUtil.log("${entity.fileName} breakPoint=$breakPoint total=${entity.total}")
        if (entity.total > 0 && breakPoint == entity.total) {
            if (!isCanceld()) {
                mCallback?.onDownloadProgress(100, breakPoint, entity.total)
                mCallback?.onDownloadSuccess()
            }
            return
        }
        var fos: FileOutputStream? = null
        var inputStream: InputStream? = null
        try {
            val url = URL(entity.url)
            mConn = url.openConnection() as HttpURLConnection
            mConn?.connectTimeout = 60 * 1000
            mConn?.readTimeout = 60 * 1000
            mConn?.requestMethod = "GET"

            if (breakPoint > 0) {
                mConn?.addRequestProperty("Range", "bytes=$breakPoint-")
            }
            mConn?.addRequestProperty("Connection", "Keep-Alive")

            val code = mConn?.responseCode
            if (code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_PARTIAL) {
                var total = mConn?.contentLength!!.toLong()
                MedDownloadUtil.log(" ${file.name} code=$code total=$total")
                if (total > 0 && entity.total == 0L && code == HttpURLConnection.HTTP_OK) {
                    entity.total = total
                    ModelDao.save(entity)
                }
                total = entity.total

                inputStream = mConn?.inputStream
                fos = FileOutputStream(file, true)
                val buffer = ByteArray(1024 * 3)
                var sum = file.length()
                var lastProgress = 0
                while (true) {
                    if (isCanceld()) break
                    val len = inputStream!!.read(buffer)
                    if (isCanceld() || len == -1) break
                    fos.write(buffer, 0, len)
                    sum += len
                    //进度回调
                    val progress = (sum * 1f / total * 100f).toInt()
                    if (lastProgress != progress) {
                        lastProgress = progress
                        if (!isCanceld()) mCallback?.onDownloadProgress(progress, sum, total)
                    }
                }
                fos.flush()
                if (!isCanceld()) {
                    mCallback?.onDownloadProgress(100, total, total)
                    mCallback?.onDownloadSuccess()
                }
            }
        } catch (e: Exception) {
            throw Exception(e)
        } finally {
            try {
                inputStream?.close()
            } catch (e: Exception) {
            }
            try {
                fos?.close()
            } catch (e: Exception) {
            }
            try {
                mConn?.disconnect()
            } catch (e: Exception) {
            }
        }
    }

    override fun isCanceld(): Boolean {
        return mCanceld.get()
    }

    override fun stop() {
        mCanceld.set(true)
        try {
            mConn?.disconnect()
        } catch (e: Exception) {
        }
        mCallback = null
    }
}