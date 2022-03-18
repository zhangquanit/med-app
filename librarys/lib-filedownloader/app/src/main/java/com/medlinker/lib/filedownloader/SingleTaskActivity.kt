package com.medlinker.lib.filedownloader

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import com.medlinker.filedownloader.task.FileDownloadCallback
import com.medlinker.filedownloader.task.MedFileDownloadTask
import com.medlinker.filedownloader.util.MedDownloadUtil
import kotlinx.android.synthetic.main.activity_download_item.*
import kotlinx.android.synthetic.main.activity_singletask.*
import java.io.File

class SingleTaskActivity : AppCompatActivity() {
    private var mTask: MedFileDownloadTask? = null
    private var isDownloading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singletask)

        btn_start.setOnClickListener {
            start()
        }
        btn_stop.setOnClickListener {
            isDownloading = false
            cancel()
        }
        btn_clear.setOnClickListener {
            if (!isDownloading || mTask?.isCompleted()!!) {
                FileUtils.delete(mTask?.getFile())
                setProgressValue(0, 0)
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
    }

    private fun getParentFile(): File {
        val parentFile = File(externalCacheDir, "download")
        FileUtils.createOrExistsDir(parentFile)
        return parentFile
    }

    private fun initTasks() {
        val url =
            "http://disease-resource-qa.medlinker.com/v1/%E7%83%AD%E8%BA%AB-%E7%AE%80%E7%9F%AD%E7%89%88.mov"
        mTask = MedFileDownloadTask.Builder(url, getParentFile())
//                .setHttpCall(HttpCall.OKHTTP)
//                .useCache(true)
//                .retryCount(3)
            .build()
    }

    private fun start() {
        if (isDownloading) return
        isDownloading = true
        mTask?.start(object : FileDownloadCallback {
            override fun onDownloadProgress(progress: Int, offset: Long, total: Long) {
                setProgressValue(offset, total)
            }

            override fun onDownloadFail() {
                MedDownloadUtil.log("下载失败,file=${mTask?.getFile()?.name} ")
                isDownloading = false
                tv_retry?.visibility = View.VISIBLE
                tv_progress?.visibility = View.GONE
            }

            override fun onDownloadSuccess() {
                setProgressValue(mTask?.getTotal()!!, mTask?.getTotal()!!)
                MedDownloadUtil.log("下载完成 file=${mTask?.getFile()?.name}")
                isDownloading = false
                ToastUtils.showShort("下载完成")
            }

        })
    }

    private fun cancel() {
        mTask?.cancel()
    }

    private fun setProgressValue(offset: Long, total: Long) {
        val percent = offset * 1.0f / total
        val max = 100f
        val progress = (percent * max).toInt()
        progressBar?.progress = progress
        tv_progress?.text = "$progress%"
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}