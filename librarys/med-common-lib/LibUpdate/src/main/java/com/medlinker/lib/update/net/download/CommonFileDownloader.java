package com.medlinker.lib.update.net.download;

import android.os.AsyncTask;

import androidx.collection.SparseArrayCompat;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.medlinker.lib.log.LogUtil;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by heaven7 on 2016/4/15.
 */
public abstract class CommonFileDownloader extends FileDownloadListener {

    protected final List<IDownloadCallback> mCallbacks;
    protected final SparseArrayCompat<Integer> mDownloadIdMap;
    private final List<AsyncTask> mTasks;
    private final AtomicBoolean mDestroied = new AtomicBoolean(false);

    private final SparseArrayCompat<Object[]> mParamsMap;

    public static abstract class IDownloadCallback{
        /**
         * called on download done
         */
        public abstract void onDownloadDone(BaseDownloadTask task);

        /**
         * called on download update
         * @param progress 0-100
         */
        public abstract void onDownloadProgressUpdate(BaseDownloadTask task, int soFarBytes, int totalBytes, int progress);

        /** already downloading */
        public abstract void warnIsAlreadyDownloading(String url);

        public abstract void onDownloadError(String url, String path);

        public void onDownloadError(BaseDownloadTask task){
            onDownloadError(task.getUrl(), task.getPath());
        }

        public void onDownloadPending(BaseDownloadTask task){

        }

    }

    public CommonFileDownloader() {
        this.mCallbacks = new ArrayList<>(3);
        this.mDownloadIdMap = new SparseArrayCompat<>(3);
        this.mTasks = new ArrayList<>(5);
        this.mParamsMap = new SparseArrayCompat<>(3);
    }

    public void addTask2Cache(AsyncTask task){
        mTasks.add(task);
    }
    public void removeTaskFromCache(AsyncTask task){
        mTasks.remove(task);
    }
    public void shutDownTasks(boolean mayInterruptIfRunning){
        for(AsyncTask  task: mTasks){
            task.cancel(mayInterruptIfRunning);
        }
        mTasks.clear();
    }

    public void registCallback(IDownloadCallback callback){
        mCallbacks.add(callback);
    }

    public void unregistCallback(IDownloadCallback callback){
        mCallbacks.remove(callback);
    }
    public void clearCallbacks(){
        mCallbacks.clear();
    }

    public void executeDownload(String url,String path,Object...params){
        final int key = url.hashCode();
        if(isDownloading(url,path)){
            for(IDownloadCallback callback : mCallbacks){
                callback.warnIsAlreadyDownloading(url);
            }
            return  ;
        }
        if(params!=null && params.length!=0){
            mParamsMap.put(key, params);
        }
        if(isDestroied()){
            mDestroied.set(false);
        }
        executeDownloadImpl(url,params);
    }

    /**
     * do download, if it is down
     * @param params the extra data to carry
     */
    protected abstract void executeDownloadImpl( String url, Object...params);

    /**
     * cancel the download
     * @param url the target url
     */
    public void cancelDownload(String url){
        Integer donwloadId =  mDownloadIdMap.get(url.hashCode());
        if(donwloadId != null) {
            ApkDownloadApi.pause(donwloadId);
        }
    }

    public boolean isDownloading(String url, String path){
        Integer downloadId  = mDownloadIdMap.get(url.hashCode());
        return  downloadId != null && ApkDownloadApi.isDownloading(url,path);
    }


    /** cancel all download */
    public void cancelAllDownload(){
        ApkDownloadApi.pauseAll();
        mDownloadIdMap.clear();
    }

    /** completed destroy this downloader */
    public void destroy(){
        mDestroied.set(true);
        mCallbacks.clear();
        mDownloadIdMap.clear();
        shutDownTasks(true);
    }

    public boolean isDestroied(){
        return mDestroied.get();
    }

    @Override
    protected final void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
         Object[] params = mParamsMap.get(task.getUrl().hashCode());
         if (params==null||params.length==0){
             return;
         }
         mParamsMap.remove(task.getUrl().hashCode());
         onPending(task, soFarBytes, totalBytes, params);
    }

    /***
     * @param params may be null
     */
    protected abstract void onPending(BaseDownloadTask task, int soFarBytes, int totalBytes, Object... params);

    @Override
    protected final void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        int progress = (int) (soFarBytes * 1f / totalBytes * 100);
        onProgress( task,soFarBytes,totalBytes, progress);
       // callbackOnProgress(task, soFarBytes, totalBytes, progress);
    }

    /** called in onProgress */
    protected void callbackOnProgress(BaseDownloadTask task, int soFarBytes, int totalBytes, int progress) {
        for(IDownloadCallback callback : mCallbacks){
            callback.onDownloadProgressUpdate(task, soFarBytes, totalBytes, progress);
        }
    }

    /** often do db task */
    protected abstract void onProgress(BaseDownloadTask task, int soFarBytes, int totalBytes, int progress);

    @Override
    protected final void completed(BaseDownloadTask task) {
        int key = task.getUrl().hashCode();
        mDownloadIdMap.remove(key);
        onCompleted(task);
    }

    protected abstract void onCompleted(BaseDownloadTask task);


    protected void callbackOnCompleted(BaseDownloadTask task) {
        for(IDownloadCallback callback : mCallbacks){
            callback.onDownloadDone(task);
        }
    }

    @Override
    protected void blockComplete(BaseDownloadTask task) {
        LogUtil.i(" download blockCompleted . url = " + task.getUrl());
    }

    @Override
    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        LogUtil.i(" download paused . url = " + task.getUrl());
        mDownloadIdMap.remove(task.getUrl().hashCode());
    }

    @Override
    protected void error(BaseDownloadTask task, Throwable e) {
        if (null != e) {
            LogUtil.e(e.getMessage() + " download error occoured. url = " + task.getUrl());
        }
        mDownloadIdMap.remove(task.getUrl().hashCode());
    }

    protected void callbackOnDownloadError(BaseDownloadTask task) {
        for(IDownloadCallback callback : mCallbacks){
            callback.onDownloadError(task);
        }
    }

    protected void callbackOnDownloadPending(BaseDownloadTask task){
        for(IDownloadCallback callback:mCallbacks){
            callback.onDownloadPending(task);
        }
    }

    @Override
    protected void warn(BaseDownloadTask task) {
        LogUtil.w(" download warning---> occoured. url = " + task.getUrl());
    }

}
