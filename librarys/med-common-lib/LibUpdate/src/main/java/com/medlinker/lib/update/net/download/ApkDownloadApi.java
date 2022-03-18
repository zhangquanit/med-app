package com.medlinker.lib.update.net.download;

import android.app.Application;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadHelper;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * 文件下载
 * Created by heaven7 on 2016/2/24.
 */
public class ApkDownloadApi {
    private static boolean inited = false;

    public static void init(Application app) {
        if (inited) return;
        try {
            final OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(60_000, TimeUnit.MILLISECONDS);
            builder.readTimeout(60_000, TimeUnit.MILLISECONDS);
            builder.writeTimeout(60_000, TimeUnit.MILLISECONDS);

            FileDownloader.init(app, new FileDownloadHelper.OkHttpClientCustomMaker() {
                @Override
                public OkHttpClient customMake() {
                    return builder.build();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        inited = true;
    }

    public static void pause(int downloadId) {
        FileDownloader.getImpl().pause(downloadId);
    }

    /**
     * single task download .
     *
     * @return download id
     */
    public static int download(String url, String savePath, FileDownloadListener l) {
        return FileDownloader.getImpl()
                .create(url)
                .setPath(savePath)
                .setListener(l)
                .setAutoRetryTimes(3)
                .start();
    }

    /**
     * BaseDownloadTask.pause()
     */
    public static void downloadMultiTask(List<BaseDownloadTask> tasks, boolean parallel,
                                         int retryTimes,
                                         FileDownloadListener l) {
        final FileDownloadQueueSet queue = new FileDownloadQueueSet(l);
        queue.setAutoRetryTimes(retryTimes);
        queue.disableCallbackProgressTimes();
        if (parallel) {
            queue.downloadTogether(tasks);
        } else {
            queue.downloadSequentially(tasks);
        }
        queue.start();
    }


    public static void pauseAll() {
        FileDownloader.getImpl().pauseAll();
    }


    public static boolean isDownloading(String url, String path) {
        return FileDownloadStatus.isIng(FileDownloader.getImpl().getStatus(url, path));
    }

    public static class DefaultDownLoadListner extends FileDownloadListener {

        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

        }

        @Override
        protected void blockComplete(BaseDownloadTask task) {

        }

        @Override
        protected void completed(BaseDownloadTask task) {

        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {

        }

        @Override
        protected void warn(BaseDownloadTask task) {

        }
    }


}
