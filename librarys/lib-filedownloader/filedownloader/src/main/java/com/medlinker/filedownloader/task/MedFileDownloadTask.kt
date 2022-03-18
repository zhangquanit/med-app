package com.medlinker.filedownloader.task

import android.text.TextUtils
import com.blankj.utilcode.util.EncryptUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.NetworkUtils
import com.medlinker.filedownloader.call.FileDownloadCall
import com.medlinker.filedownloader.call.HttpCall
import com.medlinker.filedownloader.call.HttpUrlConnectionCall
import com.medlinker.filedownloader.call.OkhttpDownloadCall
import com.medlinker.filedownloader.db.ModelDao
import com.medlinker.filedownloader.entity.MedFileEntity
import com.medlinker.filedownloader.util.MedDownloadUtil
import java.io.File

/**
 * 文件下载任务
 * @author zhangquan
 */
class MedFileDownloadTask : IDownloadTask {
    private var mUrl: String? = null
    private var mParentFile: File? = null
    private var mFileName: String? = null
    private var mDBEntity: MedFileEntity? = null
    private var mRetryCount: Int = 0

    private var mCallback: UIDownloadCallback? = null
    private var mCall: FileDownloadCall? = null
    private var mHttpCall: HttpCall? = null


    private constructor(builder: Builder) {
        mUrl = builder.getUrl()
        mParentFile = builder.getParentFile()
        mFileName = builder.getFileName()
        if (TextUtils.isEmpty(mFileName)) {
            mFileName = EncryptUtils.encryptMD5ToString(mUrl)
        }
        mHttpCall = builder.getHttpCall() ?: HttpCall.OKHTTP
        if (!builder.getUseCache()) {//不使用缓存
            FileUtils.delete(getFile())
        }
        mRetryCount = builder.getRetryCount()
        if (mRetryCount < 0) {
            mRetryCount = 0
        }
        findDBEntity()
    }

    override fun start(callback: FileDownloadCallback) {
        if (null == mDBEntity) {
            findDBEntity()
        }

        val file = getFile()
        if (file.length() > 0 && mDBEntity?.total == 0L) { //视频文件存在 数据库没保存长度
            FileUtils.delete(file)
        }

        if (isCompleted()) {
            callback?.onDownloadSuccess()
            return
        }

        val offset = file.length()
        val total = mDBEntity!!.total
        if (offset > 0 && total > 0L) {
            val progress = (offset * 1f / total * 100f).toInt()
            callback?.onDownloadProgress(progress, offset, total)
        }
        if (!NetworkUtils.isConnected()) {
            callback?.onDownloadFail()
            return
        }
        cancel()
        MedDownloadUtil.log("$mFileName 开始下载 httpCall=$mHttpCall url=${mDBEntity?.url}")
        mCallback = UIDownloadCallback(mDBEntity, callback)
        mCall = if (mHttpCall == HttpCall.OKHTTP) OkhttpDownloadCall() else HttpUrlConnectionCall()
        mCall?.start(mDBEntity!!, mCallback, mRetryCount)
    }

    override fun getInfo(): MedFileEntity {
        return mDBEntity!!
    }

    fun getFile(): File {
        return mDBEntity!!.file
    }

    fun getTotal(): Long {
        return mDBEntity!!.total
    }

    override fun isCompleted(): Boolean {
        return mDBEntity!!.isCompleted
    }

    private fun findDBEntity(): MedFileEntity {
        if (null == mDBEntity) {
            val dbEntityList = ModelDao.find(mUrl, mParentFile?.absolutePath, mFileName)
            if (dbEntityList.isNotEmpty()) {
                mDBEntity = dbEntityList[0]
            } else {
                mDBEntity = MedFileEntity()
                mDBEntity?.url = mUrl
                mDBEntity?.parentFile = mParentFile?.absolutePath
                mDBEntity?.fileName = mFileName
            }
        }
        return mDBEntity!!
    }

    override fun cancel() {
        try {
            MedDownloadUtil.log("${findDBEntity().fileName}停止下载")
            mCall?.stop()
            mCallback?.remove()
            mCallback = null
        } catch (e: Exception) {

        }
    }

    class Builder {
        private var mUrl: String? = null
        private var parentFile: File? = null
        private var mFileName: String? = null
        private var mHttpCall: HttpCall? = null
        private var mRetry = 3
        private var mUseCache = true

        constructor(url: String, parent: File) {
            mUrl = url
            parentFile = parent
        }

        /**
         * 文件名，默认为md5(url)
         */
        fun setFileName(name: String): Builder {
            mFileName = name
            return this
        }

        /**
         * 接口请求方式，默认OkHttp
         */
        fun setHttpCall(call: HttpCall): Builder {
            mHttpCall = call
            return this
        }

        /**
         * 是否使用缓存(即断点下载)，默认true
         */
        fun useCache(cache: Boolean): Builder {
            mUseCache = cache
            return this
        }

        /**
         * 失败重试次数，默认3次
         */
        fun retryCount(count: Int): Builder {
            mRetry = count
            return this
        }

        fun getFileName(): String? {
            return mFileName
        }

        fun getUrl(): String? {
            return mUrl
        }

        fun getParentFile(): File? {
            return parentFile
        }

        fun getHttpCall(): HttpCall? {
            return mHttpCall
        }

        fun getUseCache(): Boolean {
            return mUseCache
        }

        fun getRetryCount(): Int {
            return mRetry
        }

        fun build(): MedFileDownloadTask {
            if (TextUtils.isEmpty(mUrl)) {
                throw RuntimeException("url为空")
            }
            if (null == parentFile) {
                throw RuntimeException("parentFile=null")
            }
            return MedFileDownloadTask(this)
        }
    }

}