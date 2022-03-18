package com.medlinker.filedownloader.call

import com.medlinker.filedownloader.db.ModelDao
import com.medlinker.filedownloader.entity.MedFileEntity
import com.medlinker.filedownloader.util.MedDownloadUtil
import okhttp3.Call
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

/**
 *基于OKHttp下载
 * @author zhangquan
 */
class OkhttpDownloadCall : AbstractDownloadCall() {
    private var mCall: Call? = null


    override fun doExecute(entity: MedFileEntity) {
        val file = File(entity.parentFile, entity.fileName)
        var breakPoint = file.length()
        MedDownloadUtil.log("${entity.fileName} breakPoint=$breakPoint total=${entity.total}")

        if (entity.total > 0 && breakPoint == entity.total) {
            if (!isCanceld()) {
                mCallback?.onDownloadProgress(100, breakPoint, entity.total)
                mCallback?.onDownloadSuccess()
            }
            return
        }

        if (entity.total in 1 until breakPoint) {
            entity.total = 0
            file.delete()
            breakPoint = 0
        }

        val builder = Request.Builder().url(entity.url)
        if (breakPoint > 0) {
            builder.header("Range", "bytes=$breakPoint-")
        }

        val request = builder.build()
        try {
            mCall = MedFileClient.getOKHttpClient().newCall(request)
            val response = mCall?.execute()
            if (!response!!.isSuccessful) {
                throw Exception(response.message)
            }
            if (isCanceld()) return
            var fos: FileOutputStream? = null
            var inputStream: InputStream? = null
            try {
                var total = response.body?.contentLength()!!
                MedDownloadUtil.log("${entity.fileName} code=${response.code},total=$total")
                if (response.code == 200 && total > 0) {
                    entity.total = total
                    ModelDao.save(entity)
                }
                total = entity.total

                inputStream = response.body?.byteStream()
                fos = FileOutputStream(file, true)
                val buffer = ByteArray(2048)
                var sum = file.length()
                var lastProgress = 0
                while (true) {
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
                if (!isCanceld() && file.length() == total) {
                    mCallback?.onDownloadProgress(100, total, total)
                    mCallback?.onDownloadSuccess()
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
            }

        } catch (e: Exception) {
            throw Exception(e)
        }
    }

//    override fun start(entity: MedFileEntity, callback: FileDownloadCallback?, retryCount: Int) {
//        mCallback = callback
//        mRetryCount = retryCount
//
//        val file = File(entity.parentFile, entity.fileName)
//
//        val builder = Request.Builder().url(entity.url)
//        val breakPoint = file.length()
//        DownloadUtil.log("${entity.fileName} breakPoint=$breakPoint total=${entity.total}")
//        if (breakPoint > 0) {
//            builder.header("Range", "bytes=$breakPoint-")
//        }
//
//        val request = builder.build()
//        mCall = FileClient.getOKHttpClient().newCall(request)
//        mCall?.enqueue(object : Callback {
//            override fun onFailure(call: Call, e: IOException) {
//                mCallback?.onDownloadFail()
//            }
//
//            override fun onResponse(call: Call, response: Response) {
//
//                var fos: FileOutputStream? = null
//                var inputStream: InputStream? = null
//                try {
//                    var total = response.body?.contentLength()!!
//                    DownloadUtil.log("${entity.fileName} code=${response.code},total=$total")
//                    if (total > 0 && entity.total == 0L) {
//                        entity.total = total
//                        ModelDao.save(entity)
//                    }
//                    total = entity.total
//                    inputStream = response.body?.byteStream()
//                    fos = FileOutputStream(file, true)
//                    val buffer = ByteArray(2048)
//                    var sum = file.length()
//                    var lastProgress = 0
//                    while (true) {
//                        val len = inputStream!!.read(buffer)
//                        if (len == -1) break
//                        fos.write(buffer, 0, len)
//                        sum += len
//                        //进度回调
//                        val progress = (sum * 1f / total * 100f).toInt()
//                        if (lastProgress != progress) {
//                            lastProgress = progress
//                            mCallback?.onDownloadProgress(progress, sum, total)
//                        }
//                    }
//                    fos.flush()
//                    mCallback?.onDownloadProgress(100, total, total)
//                    mCallback?.onDownloadSuccess()
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    mCallback?.onDownloadFail()
//                } finally {
//                    try {
//                        inputStream?.close()
//                    } catch (e: Exception) {
//                    }
//                    try {
//                        fos?.close()
//                    } catch (e: Exception) {
//                    }
//                }
//            }
//
//        })
//    }


    override fun isCanceld(): Boolean {
        return mCanceld.get()
    }

    override fun stop() {
        mCanceld.set(true)
        try {
            mCall?.cancel()
        } catch (e: Exception) {
        }
        mCallback = null
    }

}