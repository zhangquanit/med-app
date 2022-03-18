package com.medlinker.lib.update.net.download;


import com.blankj.utilcode.util.FileUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.medlinker.lib.log.LogUtil;
import com.medlinker.lib.update.UpgradeUtil;
import com.medlinker.lib.update.bean.AppVersionEntity;


import java.io.File;

/**
 * 安装包文件下载
 *
 * @author <a href="mailto:yangjiantao@medlinker.net">Jiantao.Yang</a>
 *         2016/6/2 20:19
 * @version 1.0
 */
public class ApkDownloader extends CommonFileDownloader {

    private String requestVersionName;
    private static ApkDownloader INSTANCE = new ApkDownloader();

    private ApkDownloader() {
    }

    public static ApkDownloader getInstance() {
        return INSTANCE;
    }

    @Override //params[0] must be UpgradeInfoResponseEntity
    protected void executeDownloadImpl(String url, Object... params) {
        AppVersionEntity entity = ((AppVersionEntity) params[0]);
        requestVersionName = entity.appVersion;
        ApkDownloadApi.download(url, UpgradeUtil.INSTANCE.getDownloadTempApkFile(entity.downUrl, entity.appVersion).getAbsolutePath(), this);
    }

    @Override
    protected void onPending(BaseDownloadTask task, int soFarBytes, int totalBytes, Object... params) {
        LogUtil.i("onDownloadPending onPending");
        callbackOnDownloadPending(task);
    }

    @Override
    protected void onProgress(BaseDownloadTask task, int soFarBytes, int totalBytes, int progress) {
        callbackOnProgress(task, soFarBytes, totalBytes, progress);
    }

    @Override
    protected void onCompleted(BaseDownloadTask task) {
        rename(task);
        callbackOnCompleted(task);
    }

    @Override
    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
        LogUtil.i("onDownloadPending connected");
    }

    @Override
    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        super.paused(task, soFarBytes, totalBytes);
        callbackOnDownloadError(task);
    }

    @Override
    protected void error(BaseDownloadTask task, Throwable e) {
        super.error(task, e);
        callbackOnDownloadError(task);
    }

    private void rename(BaseDownloadTask task) {
        File tmpFile = new File(task.getPath());
        File downloadedFile = UpgradeUtil.INSTANCE.getDownloadedApkFile(task.getUrl(), requestVersionName);
        if (downloadedFile.exists()) {
            FileUtils.delete(downloadedFile);
        }
        if (tmpFile.renameTo(downloadedFile)) {
            task.setPath(downloadedFile.getPath());
        }
    }

    /**
     * 回调通知栏
     */
//    public static class NotificationDownloadCallback extends CommonFileDownloader.IDownloadCallback {
//
//        private Context mContext;
//        private NotificationCompat.Builder mBuilder;
//        private NotificationManager manager;
//
//        public NotificationDownloadCallback(Context context) {
//            mContext = context;
//            String title = context.getString(R.string.UMUpdateTitle);
//            String desc = context.getString(R.string.umeng_common_action_info_exist);
//            final String channelId = context.getString(R.string.app_name)+"apk_download";
//            manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//            mBuilder = new NotificationCompat.Builder(context, channelId);
//            if (BuildVersionUtils.hasO()) {
//                NotificationChannel channel = new NotificationChannel(channelId, title, NotificationManager.IMPORTANCE_LOW);
//                channel.enableLights(true); //是否在桌面icon右上角展示小红点
//                channel.setLightColor(Color.RED); //小红点颜色
//                channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
//                channel.setDescription(desc);
//                channel.setSound(null, null);
//                manager.createNotificationChannel(channel);
//            }
//            mBuilder.setContentTitle(title)
//                    .setSound(null)
//                    .setContentText(desc);
//            if (BuildVersionUtils.hasLollipop()) {
//                mBuilder.setSmallIcon(R.drawable.ic_lollipop_notify);
//            } else {
//                mBuilder.setSmallIcon(PackageUtil.getAppIcon());
//            }
//        }
//
//        @Override
//        public void onDownloadDone(BaseDownloadTask task) {
//            LogUtil.i(String.format("onDownloadDone taskId=%d", task.getId()));
//            // download complete
//            // When the loop is finished, updates the notification
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            final File downloadedFile = new File(task.getPath());
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                Uri uri = FileProvider.getUriForFile(MedlinkerApp.getApplication().getApplicationContext(), BuildConfig.APPLICATION_ID.concat(".provider"), downloadedFile);
//                intent.setDataAndType(uri, "application/vnd.android.package-archive");
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            } else {
//                intent.setDataAndType(Uri.fromFile(downloadedFile), "application/vnd.android.package-archive");
//            }
//            // Creates the PendingIntent
//            PendingIntent notifyPendingIntent =
//                    PendingIntent.getActivity(
//                            MedlinkerApp.getApplication().getApplicationContext(),
//                            0,
//                            intent,
//                            PendingIntent.FLAG_CANCEL_CURRENT
//                    );
//
//            // Puts the PendingIntent into the notification builder
//            mBuilder.setContentIntent(notifyPendingIntent);
//            mBuilder.setContentText(mContext.getString(R.string.umeng_common_download_finish))
//                    // Removes the progress bar
//                    .setProgress(0, 0, false);
//            mBuilder.setAutoCancel(true);
//            manager.notify(task.getId(), mBuilder.build());
//            MainWorker.postDelay(200, new Runnable() {
//                @Override
//                public void run() {
//                    UpgradeUtil.getInstance().installBySystem(mContext, downloadedFile);
//                }
//            });
//        }
//
//        @Override
//        public void onDownloadProgressUpdate(BaseDownloadTask task, int soFarBytes, int totalBytes, int progress) {
//            LogUtil.i("onDownloadProgressUpdate progress=" + progress);
//            // Sets the progress indicator to a max value, the
//            // current completion percentage, and "determinate"
//            // state
//            mBuilder.setProgress(100, progress, false);
//            mBuilder.setContentText(mContext.getString(R.string.umeng_common_action_info_exist));
//            // Displays the progress bar for the first time.
//            manager.notify(task.getId(), mBuilder.build());
//        }
//
//        @Override
//        public void onDownloadPending(BaseDownloadTask task) {
//            MedToastUtil.showMessage(MedlinkerApp.getApplication(), R.string.umeng_common_download_backgroud_task);
//            LogUtil.i("onDownloadPending taskId=" + task.getId());
//            mBuilder.setContentText(mContext.getString(R.string.umeng_common_download_connecting))
//                    // Removes the progress bar
//                    .setProgress(0, 0, false);
//            mBuilder.setTicker(mContext.getString(R.string.umeng_common_download_start));
//            manager.notify(task.getId(), mBuilder.build());
//        }
//
//        @Override
//        public void warnIsAlreadyDownloading(String url) {
//
//        }
//
//        @Override
//        public void onDownloadError(String url, String path) {
//            LogUtil.i("upgrade app downloadError !!! path= " + path);
//            FileUtil.deleteFile(new File(path));
//        }
//    }

}
