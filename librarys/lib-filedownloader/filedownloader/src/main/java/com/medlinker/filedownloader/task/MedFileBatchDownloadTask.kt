package com.medlinker.filedownloader.task

import com.medlinker.filedownloader.entity.MedFileEntity
import com.medlinker.filedownloader.util.MedDownloadUtil
import java.util.concurrent.atomic.AtomicInteger


/**
 *
 * @author zhangquan
 */
class MedFileBatchDownloadTask : IDownloadTask {
    private var mDBEntity: MedFileEntity? = null
    private var mTaskList: List<IDownloadTask>? = null
    private var mCallback: UIDownloadCallback? = null
    private var mCompletedCount = AtomicInteger()

    constructor(builder: Builder) {
        mTaskList = builder.getTasks()
    }


    override fun isCompleted(): Boolean {
        mTaskList?.forEach { task ->
            if (!task.isCompleted()) {
                return false
            }
        }
        return true
    }

    override fun getInfo(): MedFileEntity {
        if (mDBEntity == null) {
            mDBEntity = MedFileEntity()
            mDBEntity?.fileName = "文件集"
            mDBEntity?.total = mTaskList?.size?.toLong()
        }
        return mDBEntity!!
    }

    override fun cancel() {
        try {
            mTaskList?.forEach { task ->
                task.cancel()
            }
            mCallback?.remove()
            mCallback = null
        } catch (e: Exception) {

        }
    }

    fun getTasks(): ArrayList<IDownloadTask> {
        var list = ArrayList<IDownloadTask>()
        if (null != mTaskList && mTaskList!!.isNotEmpty()) {
            list = ArrayList(mTaskList!!)
        }
        return list
    }

    override fun start(downloadCallback: FileDownloadCallback) {
        mCompletedCount.set(0)
        mCallback = UIDownloadCallback(getInfo(), downloadCallback)
        mTaskList?.forEach { task ->
            if (task.isCompleted()) {
                onResultCall()
            } else {
                task.start(object : FileDownloadCallback {
                    override fun onDownloadProgress(progress: Int, offset: Long, total: Long) {

                    }

                    override fun onDownloadFail() {
                        MedDownloadUtil.log("${task.getInfo().fileName} 下载失败")
                        onResultCall()
                    }

                    override fun onDownloadSuccess() {
                        MedDownloadUtil.log("${task.getInfo().fileName} 下载成功")
                        onResultCall()
                    }
                })
            }
        }
    }


    class Builder {
        private var mTaskList = ArrayList<IDownloadTask>()
        fun withTask(task: IDownloadTask): Builder {
            mTaskList.remove(task)
            mTaskList.add(task)
            return this
        }

        fun getTasks(): List<IDownloadTask> {
            return mTaskList
        }

        fun build(): MedFileBatchDownloadTask {
            return MedFileBatchDownloadTask(this)
        }
    }

    private fun onResultCall() {
        val count = mCompletedCount.addAndGet(1)

        val taskSize = mTaskList!!.size
        var successfulCount = 0
        for (i in 0 until taskSize) {
            if (mTaskList!![i].isCompleted()) {
                successfulCount++
            }
        }

        val progress = (successfulCount * 1f / taskSize * 100f).toInt()
        mCallback?.onDownloadProgress(progress, successfulCount.toLong(), taskSize.toLong())

        MedDownloadUtil.log("onDownloadProgress progress=$progress successfulCount=$successfulCount total=$taskSize")
        if (count == taskSize) {
            if (successfulCount == taskSize) {
                mCallback?.onDownloadSuccess()
            } else {
                mCallback?.onDownloadFail()
            }
        }
    }
}