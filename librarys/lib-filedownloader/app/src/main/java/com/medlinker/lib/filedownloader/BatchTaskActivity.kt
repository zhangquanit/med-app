package com.medlinker.lib.filedownloader

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import com.medlinker.filedownloader.task.FileDownloadCallback
import com.medlinker.filedownloader.task.MedFileBatchDownloadTask
import com.medlinker.filedownloader.task.MedFileDownloadTask
import com.medlinker.filedownloader.util.MedDownloadUtil
import kotlinx.android.synthetic.main.activity_batchtask.*
import kotlinx.android.synthetic.main.activity_download_item.*
import kotlinx.android.synthetic.main.activity_singletask.btn_clear
import kotlinx.android.synthetic.main.activity_singletask.btn_start
import kotlinx.android.synthetic.main.activity_singletask.btn_stop
import java.io.File

class BatchTaskActivity : AppCompatActivity() {
    private var mTask: MedFileBatchDownloadTask? = null
    private var isDownloading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_batchtask)

        btn_start.setOnClickListener {
            start()
        }
        btn_stop.setOnClickListener {
            cancel()
        }
        btn_clear.setOnClickListener {
            if (!isDownloading || mTask?.isCompleted()!!) {
                mTask?.getTasks()?.forEach { task ->
                    FileUtils.delete(task.getInfo().file)
                }
                setProgressValue(0, mTask!!.getTasks().size.toLong())
            }
        }
        tv_retry.setOnClickListener {
            if (!NetworkUtils.isConnected()) {
                ToastUtils.showShort("网络无链接")
                return@setOnClickListener
            }
            tv_retry?.visibility = View.GONE
            tv_progress?.visibility = View.VISIBLE
            start()
        }

        initTasks()
        setProgressValue(0, mTask!!.getTasks().size.toLong())
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

        val builder = MedFileBatchDownloadTask.Builder()
        for (i in 0 until size) {
            val task = MedFileDownloadTask.Builder(urlList[i], getParentFile())
//                .setHttpCall(HttpCall.OKHTTP)
//                .useCache(true)
//                .retryCount(3)
                .build()
            builder.withTask(task)
        }
        mTask = builder.build()
    }

    private fun start() {
        if (isDownloading) return
        isDownloading = true
        progress.visibility=View.VISIBLE
        mTask?.start(object : FileDownloadCallback {
            override fun onDownloadProgress(progress: Int, offset: Long, total: Long) {
                setProgressValue(offset, total)
            }

            override fun onDownloadFail() {
                MedDownloadUtil.log("文件集 下载失败")
                isDownloading = false
                tv_retry?.visibility = View.VISIBLE
                tv_progress?.visibility = View.GONE
                progress.visibility=View.GONE
            }

            override fun onDownloadSuccess() {
                setProgressValue(mTask!!.getInfo().total, mTask!!.getInfo().total)
                progress.visibility=View.GONE
                MedDownloadUtil.log("文件集  下载完成")
                isDownloading = false
                ToastUtils.showShort("下载完成")
            }

        })
    }

    private fun cancel() {
        isDownloading = false
        mTask?.cancel()
        progress.visibility=View.GONE
    }

    private fun setProgressValue(offset: Long, total: Long) {
        val percent = offset * 1.0f / total
        val max = 100f
        val progress = (percent * max).toInt()
        progressBar?.progress = progress
        tv_progress?.text = "$progress%"

        textView.text = "已下载${offset}个文件，总共${total}个文件"
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}