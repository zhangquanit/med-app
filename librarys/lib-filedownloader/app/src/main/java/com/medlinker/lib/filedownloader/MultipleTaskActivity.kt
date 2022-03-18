package com.medlinker.lib.filedownloader

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import com.medlinker.filedownloader.task.FileDownloadCallback
import com.medlinker.filedownloader.task.MedFileDownloadTask
import com.medlinker.filedownloader.util.MedDownloadUtil
import kotlinx.android.synthetic.main.activity_multiple.*
import java.io.File

/**
 *
 * @author zhangquan
 */
class MultipleTaskActivity : AppCompatActivity() {
    private var mDownloadTasks = ArrayList<TaskItem>()
    private var mInflater: LayoutInflater? = null
    private var isDownloading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiple)
        mInflater = LayoutInflater.from(this)

        btn_start.setOnClickListener {
            isDownloading = true
            start()
        }
        btn_stop.setOnClickListener {
            isDownloading = false
            cancel()
        }
        btn_clear.setOnClickListener {
            if (!isDownloading || downloadCompleted()) {
                FileUtils.deleteAllInDir(getParentFile())
                mDownloadTasks?.forEach { item ->
                    item.setProgressValue(0, 0)
                }
            }
        }
        initTasks()
    }

    private fun getParentFile(): File {
        val parentFile = File(externalCacheDir, "download")
        FileUtils.createOrExistsDir(parentFile)
        return parentFile
    }

    private fun initTasks() {
        val urlList = mutableListOf<String>()
        urlList.add("http://disease-resource-qa.medlinker.com/v1/%E5%86%85%E6%97%8B-R-%E7%AB%96%E7%89%88.mov")
        urlList.add("http://disease-resource-qa.medlinker.com/v1/%E5%B1%88%E4%BC%B8%E8%82%98%E5%85%B3%E8%8A%82-R-%E7%AB%96%E7%89%88.mov")
        urlList.add("http://disease-resource-qa.medlinker.com/v1/%E8%82%A9%E5%85%B3%E8%8A%82%E5%89%8D%E5%B1%88-R-%E7%AB%96%E7%89%88.mov")
        urlList.add("http://disease-resource-qa.medlinker.com/v1/%E8%82%A9%E5%85%B3%E8%8A%82%E5%90%8E%E4%BC%B8-R-%E7%AB%96%E7%89%88.mov")
        urlList.add("http://disease-resource-qa.medlinker.com/v1/%E8%82%A9%E5%85%B3%E8%8A%82%E5%A4%96%E5%B1%95-R-%E7%AB%96%E7%89%88.mov")
        val size = urlList.size

        for (i in 0 until size) {
            val task = MedFileDownloadTask.Builder(urlList[i], getParentFile())
//                .setHttpCall(HttpCall.OKHTTP)
//                .useCache(true)
//                .retryCount(3)
                .build()


            val taskInfo = TaskItem()
            taskContainer.addView(taskInfo.init(task))
            mDownloadTasks.add(taskInfo)

            MedDownloadUtil.log("创建taskInfo file=${task.getFile()?.name}")
        }
    }

    private fun start() {
        mDownloadTasks?.forEach { item ->
            item.start()
        }
    }

    private fun cancel() {
        mDownloadTasks?.forEach { item ->
            item.cancel()
        }
    }

    private inner class TaskItem {
        private var downloadTask: MedFileDownloadTask? = null
        private var progressBar: ProgressBar? = null
        private var progressTextView: TextView? = null
        private var retryView: View? = null
        private var isDownload = false

        fun init(task: MedFileDownloadTask): View {
            downloadTask = task
            val view = mInflater?.inflate(R.layout.activity_download_item, null)!!
            progressBar = view.findViewById(R.id.progressBar)
            progressTextView = view.findViewById(R.id.tv_progress)
            retryView = view.findViewById(R.id.tv_retry)
            retryView?.setOnClickListener {
                if (!NetworkUtils.isConnected()) {
                    ToastUtils.showShort("网络无链接")
                    return@setOnClickListener
                }
                retryView?.visibility = View.GONE
                progressTextView?.visibility = View.VISIBLE
                start()
            }
            return view
        }

        fun setProgressValue(offset: Long, total: Long) {
            val percent = offset * 1.0f / total
            val max = 100f
            val progress = (percent * max).toInt()
            progressBar?.progress = progress
            progressTextView?.text = "$progress%"
        }

        fun start() {
            if (isDownload) {
                return
            }
            isDownload = true
            downloadTask?.start(object : FileDownloadCallback {
                override fun onDownloadProgress(progress: Int, offset: Long, total: Long) {
                    setProgressValue(offset, total)
                }

                override fun onDownloadFail() {
                    MedDownloadUtil.log("下载失败,file=${downloadTask?.getFile()?.name} ")
                    isDownload = false
                    retryView?.visibility = View.VISIBLE
                    progressTextView?.visibility = View.GONE
                }

                override fun onDownloadSuccess() {
                    setProgressValue(downloadTask?.getTotal()!!, downloadTask?.getTotal()!!)
                    MedDownloadUtil.log("下载完成 file=${downloadTask?.getFile()?.name}")
                    isDownload = false
                    downloadCompleted()
                }

            })
        }

        fun cancel() {
            isDownload = false
            downloadTask?.cancel()
        }

        fun isCompleted(): Boolean {
            return downloadTask?.isCompleted()!!
        }

    }

    private fun downloadCompleted(): Boolean {
        mDownloadTasks?.forEach { item ->
            if (!item.isCompleted()) {
                return false
            }
        }
        ToastUtils.showShort("下载完成")
        return true
    }


    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}