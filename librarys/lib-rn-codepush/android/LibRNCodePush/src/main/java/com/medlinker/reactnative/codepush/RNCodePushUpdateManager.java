package com.medlinker.reactnative.codepush;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.medlinker.reactnative.codepush.dialog.CodePushProgressDialog;
import com.medlinker.reactnative.codepush.dialog.LoadingDialogFragment;
import com.medlinker.reactnative.codepush.dialog.RestartRNDialog;
import com.medlinker.reactnative.codepush.entity.MetaInfoEntity;
import com.medlinker.reactnative.codepush.util.DownLoadUtil;
import com.medlinker.reactnative.codepush.util.FileUtils;
import com.medlinker.reactnative.codepush.util.RNCodePushUpdateUtils;
import com.medlinker.reactnative.codepush.util.RNLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author hmy
 */
public class RNCodePushUpdateManager {

    private String mRootDirectory;
    private String mDocumentsDirectory;
    private static String mServerUrl;

    private AsyncTask<Void, DownloadProgress, Boolean> mDownloadPackageAsyncTask;
    private RNSettingsManager mSettingsManager;

    public RNCodePushUpdateManager(String rootDirectory, String documentsDirectory, String serverUrl) {
        mRootDirectory = rootDirectory;
        mDocumentsDirectory = documentsDirectory;
        mServerUrl = serverUrl;
    }

    private String getDocumentsDirectory() {
        return mDocumentsDirectory;
    }

    /**
     * 获取热更的Bundle文件路径
     *
     * @return
     */
    public String getCurrentPackageBundlePath() {
        MetaInfoEntity metaInfo = getCurrentPackageMetaInfo();
        if (metaInfo == null || TextUtils.isEmpty(metaInfo.getBundleFile())) {
            return null;
        }
        String bundleFilePath = FileUtils.appendPathComponent(getCodePushPath(), metaInfo.getBundleFile());
        if (FileUtils.fileAtPathExists(bundleFilePath)) {
            return bundleFilePath;
        }
        return null;
    }

    public MetaInfoEntity getCurrentPackageMetaInfo() {
        return getMetaInfoByPath(getStatusMetaFilePath());
    }

    public MetaInfoEntity getMetaInfoByPath(String path) {
        if (!FileUtils.fileAtPathExists(path)) {
            return new MetaInfoEntity();
        }
        try {
            JSONObject jsonObject = FileUtils.getJsonObjectFromFile(path);
            if (jsonObject == null) {
                return new MetaInfoEntity();
            }
            return new MetaInfoEntity(jsonObject);
        } catch (IOException e) {
            RNCodePush.getRnCodePushLog().setMetaInfoByPath(e.getMessage() + ",path = " + path);
            e.printStackTrace();
            return new MetaInfoEntity();
        }
    }

    public MetaInfoEntity getAssetsMetaInfo() {
        try {
            String metaJson = FileUtils.getJsonStringFromFile(new File(FileUtils.appendPathComponent(
                    getBaseBundleFilePath(), RNCodePushConstants.STATUS_FILE_NAME)));
            JSONObject jsonObject = new JSONObject(metaJson);
            return new MetaInfoEntity(jsonObject);
        } catch (JSONException e) {
            //出错Log太多，先去掉
            //RNCodePush.getRnCodePushLog().setAssetsMetaInfo(e.getMessage());
            e.printStackTrace();
            return new MetaInfoEntity();
        }
    }

    /**
     * @return 获取比对更新的meta文件
     */
    private String getStatusMetaFilePath() {
        return FileUtils.appendPathComponent(getCodePushPath(), RNCodePushConstants.STATUS_FILE_NAME);
    }

    private String getCompareMetaFilePath() {
        return FileUtils.appendPathComponent(getCompareMetaPath(), RNCodePushConstants.STATUS_FILE_NAME);
    }

    private String getDownloadMetaFilePath() {
        return FileUtils.appendPathComponent(getCodePushDownloadPath(), RNCodePushConstants.STATUS_FILE_NAME);
    }

    public String getCodePushManifestFilePath() {
        return FileUtils.appendPathComponent(getCodePushPath(), RNCodePushConstants.DEFAULT_MANIFEST_FILE_NAME);
    }

    public String getBaseBundleFilePath() {
        return FileUtils.appendPathComponent(mRootDirectory, RNCodePushConstants.BASE_BUNDLE_FOLDER);
    }

    /**
     * @return 用于加载热更文件的存储目录
     */
    public String getCodePushPath() {
        return FileUtils.appendPathComponent(getDocumentsDirectory(), RNCodePushConstants.CODE_PUSH_FOLDER);
    }

    /**
     * @return 热更文件下载存储目录
     */
    public String getCodePushDownloadPath() {
        return FileUtils.appendPathComponent(getDocumentsDirectory(), RNCodePushConstants.CODE_PUSH_DOWNLOAD_FOLDER);
    }

    public String getDownloadJsBundleFilePath() {
        return FileUtils.appendPathComponent(getCodePushDownloadPath(), RNCodePushConstants.DEFAULT_JS_BUNDLE_FILE_NAME);
    }

    /**
     * @return 备份上次可用的RN文件的目录
     */
    public String getCodePushBackupPath() {
        return FileUtils.appendPathComponent(getDocumentsDirectory(), RNCodePushConstants.CODE_PUSH_BACKUP_FOLDER);
    }

    /**
     * @return 用于对比是否热更新的meta文件的目录
     */
    public String getCompareMetaPath() {
        return FileUtils.appendPathComponent(getDocumentsDirectory(), RNCodePushConstants.CODE_PUSH_META_FOLDER);
    }

    /**
     * @return tagListJson文件路径
     */
    public String getTagListJsonFilePath() {
        return FileUtils.appendPathComponent(getDocumentsDirectory(), RNCodePushConstants.TAG_LIST_JSON_FILE_NAME);
    }

    public void clearUpdates() {
        FileUtils.deleteDirectoryAtPath(getCodePushPath());
    }

    private String getDownloadFolder(MetaInfoEntity meta) {
        return meta.getBaseVersion();
    }

    //是否正在更新
    private boolean mIsCheckUpdating = false;
    //是否正在下载更新包
    private boolean mIsDownLoadPackaging = false;

    public void checkUpdate(final Context context, final boolean auto, final CallBack<Boolean> checkUpdateCallBack,
                            final boolean isFunctionCheck) {
        if (mIsCheckUpdating || mIsDownLoadPackaging || getSettingsManager(context).getPendingUpdate().isLoading()) {
            return;
        }
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Integer, Boolean> asyncTask = new AsyncTask<Void, Integer, Boolean>() {

            @Override
            protected void onPreExecute() {
                mIsCheckUpdating = true;
                if (isFunctionCheck) {
                    showLoading(context);
                }
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                boolean isCompleted = false;
                try {
                    RNCodePush.getRnCodePushLog().setCheckUpdate(true);
                    //删除用于比较的meta文件
                    FileUtils.deleteFileOrFolderSilently(new File(getCompareMetaFilePath()));
                    MetaInfoEntity currentMetaInfo = getCurrentPackageMetaInfo();
                    if (!currentMetaInfo.isExist()) {
                        currentMetaInfo = getAssetsMetaInfo();
                    }
                    isCompleted = DownLoadUtil.download(mServerUrl, getDownloadFolder(currentMetaInfo),
                            RNCodePushConstants.STATUS_FILE_NAME,
                            getCompareMetaPath(), new DownloadProgressCallback() {
                            });
                } catch (IOException e) {
                    RNCodePush.getRnCodePushLog().setDownloadError(e.getMessage());
                    e.printStackTrace();
                }
                return isCompleted;
            }

            @Override
            protected void onPostExecute(Boolean isCompleted) {
                if (isFunctionCheck) {
                    hideLoading();
                }
                mIsCheckUpdating = false;
                if (isCompleted) {
                    compareUpdate(context, auto, checkUpdateCallBack, isFunctionCheck);
                }
                RNCodePush.reportLogIfNeeded();
            }
        };

        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 检查是否更新
     *
     * @param auto true:自动后台更新， false：显示弹窗，用户手动确认更新
     */
    private void compareUpdate(final Context context, boolean auto, CallBack<Boolean> checkUpdateCallBack,
                               final boolean isFunctionCheck) {
        MetaInfoEntity codePushMetaInfo = getCurrentPackageMetaInfo();
        String compareMetaFilePath = getCompareMetaFilePath();
        final MetaInfoEntity compareMetaInfo = getMetaInfoByPath(compareMetaFilePath);
        if (!compareMetaInfo.isExist()
                || getSettingsManager(context).isFailedHash(compareMetaInfo.getHash())) {
            if (checkUpdateCallBack != null) {
                checkUpdateCallBack.onCallBack(false);
            }
            return;
        }
        boolean haveUpdate = false;
        if (codePushMetaInfo.isExist()) {
            if (isUpdate(compareMetaInfo, codePushMetaInfo)) {
                haveUpdate = true;
            }
        } else {
            MetaInfoEntity assetsMetaInfo = getAssetsMetaInfo();
            if (isUpdate(compareMetaInfo, assetsMetaInfo)) {
                haveUpdate = true;
            }
            codePushMetaInfo = assetsMetaInfo;
        }

        FileUtils.deleteFileOrFolderSilently(new File(compareMetaFilePath));
        if (!haveUpdate) {
            if (checkUpdateCallBack != null) {
                checkUpdateCallBack.onCallBack(false);
            }
            return;
        }
        if (auto) {
            if (checkUpdateCallBack != null) {
                checkUpdateCallBack.onCallBack(true);
            }
            downloadPackage(context, compareMetaInfo, false, isFunctionCheck);
        } else {
            showUpdateDialog(context, compareMetaInfo, codePushMetaInfo, isFunctionCheck);
        }
    }

    public void showUpdateDialog(final Context context, final MetaInfoEntity compareMetaInfo,
                                 MetaInfoEntity localMetaInfo, final boolean isFunctionCheck) {
        if (mUpdateDialog != null && mUpdateDialog.isShowing()) {
            mUpdateDialog.dismiss();
        }
        String updateText = compareMetaInfo.getMetaInfo();
        updateText += String.format("\n\n本地信息：\n%s", localMetaInfo.getMetaInfo());
        mUpdateDialog = new AlertDialog.Builder(context)
                .setTitle("RN" + context.getString(R.string.txt_update))
                .setMessage(updateText)
                .setPositiveButton(context.getString(R.string.txt_update), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mUpdateDialog = null;
                        downloadPackage(context, compareMetaInfo, true, isFunctionCheck);
                    }
                })
                .setNegativeButton(context.getString(R.string.txt_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mUpdateDialog = null;
                    }
                })
                .create();

        if (context instanceof Activity && !((Activity) context).isFinishing()) {
            mUpdateDialog.show();
        }
    }

    private AlertDialog mUpdateDialog;

    public boolean isUpdate(MetaInfoEntity compareMetaInfo, MetaInfoEntity localMetaInfo) {
        boolean isUpdate = !TextUtils.equals(compareMetaInfo.getHash(), localMetaInfo.getHash())
                && compareMetaInfo.getBuildTime() > localMetaInfo.getBuildTime();
        if (RNCodePush.isDebugMode() && !isUpdate
                && !TextUtils.equals(compareMetaInfo.getTagId(), localMetaInfo.getTagId())) {
            isUpdate = true;
        }
        return isUpdate;
    }

    public boolean isSameBaseVersion(MetaInfoEntity metaInfo1, MetaInfoEntity metaInfo2) {
        return TextUtils.equals(metaInfo1.getBaseVersion(), metaInfo2.getBaseVersion());
    }

    /**
     * 下载热更包
     *
     * @param updateMetaInfo       要更新的文件信息
     * @param isShowProgressDialog 是否显示进度弹窗
     */
    @SuppressLint("StaticFieldLeak")
    private void downloadPackage(final Context context, final MetaInfoEntity updateMetaInfo,
                                 final boolean isShowProgressDialog, final boolean isFunctionCheck) {
        if (mIsDownLoadPackaging || updateMetaInfo == null || TextUtils.isEmpty(updateMetaInfo.getDownloadFile())) {
            return;
        }
        removePendingUpdate();
        mDownloadPackageAsyncTask = new AsyncTask<Void, DownloadProgress, Boolean>() {
            private CodePushProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                mIsDownLoadPackaging = true;
                if (isShowProgressDialog) {
                    progressDialog = new CodePushProgressDialog(context);
                    progressDialog.setMessage(context.getString(R.string.txt_update_and_wait));
                    progressDialog.setMax(100);
                    progressDialog.setButton(ProgressDialog.BUTTON_POSITIVE, context.getString(R.string.txt_confirm),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressDialog.dismiss();
                                }
                            });
                    progressDialog.show();
                    progressDialog.getButton(ProgressDialog.BUTTON_POSITIVE).setVisibility(View.GONE);
                }
            }

            @Override
            protected Boolean doInBackground(Void... voids) {
                boolean isComplete = false;
                try {
                    //删除RN下载目录下的文件
                    FileUtils.deleteFileOrFolderSilently(new File(getCodePushDownloadPath()));
                    RNLog.d("download rn codepush file start");
                    //下载热更文件到"下载目录"并解压
                    isComplete = DownLoadUtil.download(mServerUrl, getDownloadFolder(updateMetaInfo), updateMetaInfo.getDownloadFile(),
                            getCodePushDownloadPath(), new DownloadProgressCallback() {
                                @Override
                                public void call(DownloadProgress downloadProgress) {
                                    publishProgress(downloadProgress);
                                }
                            });
                    RNLog.d("download rn codepush file end");
                    if (!isComplete) {
                        return false;
                    }
                    //更新下载包的updateTime
                    MetaInfoEntity downloadMetaInfo = getMetaInfoByPath(getDownloadMetaFilePath());
                    if (downloadMetaInfo.isExist()) {
                        RNCodePushUpdateUtils.verifyHash(getDownloadJsBundleFilePath(), downloadMetaInfo.getHash());
                        downloadMetaInfo.setUpdateTime(System.currentTimeMillis());
                        FileUtils.writeStringToFile(downloadMetaInfo.toJSONObject().toString(), getDownloadMetaFilePath());

                        if (!RNCodePush.isLoadedJSBundle()) {
                            checkChangeCodePushPath(context);
                            RNLog.d("isLoadedJSBundle = false；checkChangeCodePushPath");
                        } else {
                            RNLog.d("isLoadedJSBundle = true；savePendingUpdate");
                            getSettingsManager(context).savePendingUpdate(downloadMetaInfo, true);
                        }
                        getSettingsManager(context).saveUnloadUpdate(downloadMetaInfo);
                    }
                } catch (RNCodePushInvalidUpdateException e) {
                    e.printStackTrace();
                    getSettingsManager(context).saveFailedUpdate(updateMetaInfo.toJSONObject());
                    isComplete = false;
                    RNCodePush.getConfig().sendFeedback(String.format("RN热更新失败 hash: %s; buildTime: %s",
                            updateMetaInfo.getHash(), updateMetaInfo.getBuildTime()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    isComplete = false;
                } catch (IOException e) {
                    e.printStackTrace();
                    isComplete = false;
                }
                return isComplete;
            }

            @Override
            protected void onProgressUpdate(DownloadProgress... values) {
                super.onProgressUpdate(values);
                if (isShowProgressDialog) {
                    DownloadProgress progress = values[0];
                    progressDialog.setProgress((int) (progress.getReceivedBytes() * 100 / progress.getTotalBytes()));
                    progressDialog.setNumberText(progress.getReceivedBytes(), progress.getTotalBytes());
                    if (progress.isCompleted()) {
                        progressDialog.setMessage(context.getString(R.string.txt_download_success));
                        progressDialog.getButton(ProgressDialog.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            protected void onPostExecute(Boolean isComplete) {
                mIsDownLoadPackaging = false;
                if (isComplete) {
                    // 加载过jsBundle后，需要重启后才生效；反之，马上更新路由
                    if (!RNCodePush.isLoadedJSBundle()) {
//                        RNRouteUtils.getInstance().init(context); 不再需要处理路由
                        if (isFunctionCheck) {
                            Toast.makeText(context, context.getString(R.string.txt_update_success_retry), Toast.LENGTH_SHORT).show();
                        }
                    } else if (updateMetaInfo.isConfirm() || isFunctionCheck) { // 更新完毕，是否提示，根据返回的meta文件配置控制
                        showRestartRNDialog(context, isFunctionCheck);
                    }
                }
            }
        };
        mDownloadPackageAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public AsyncTask downloadTagList(final CallBack<String> callBack) {
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Integer, String> asyncTask = new AsyncTask<Void, Integer, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    String tagListJsonFilePath = getTagListJsonFilePath();
                    FileUtils.deleteFileOrFolderSilently(new File(tagListJsonFilePath));
                    boolean isCompleted = DownLoadUtil.download(mServerUrl, null,
                            RNCodePushConstants.TAG_LIST_JSON_FILE_NAME,
                            getDocumentsDirectory(), new DownloadProgressCallback() {
                            });
                    if (!isCompleted) {
                        return null;
                    }

                    return FileUtils.getJsonStringFromFile(new File(tagListJsonFilePath));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String json) {
                if (null != callBack) {
                    callBack.onCallBack(json);
                }
            }
        };
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return asyncTask;
    }

    private void showRestartRNDialog(Context context, boolean isForceRestart) {
        if (context instanceof Activity && !((Activity) context).isFinishing()) {
            RestartRNDialog dialog = new RestartRNDialog(context, isForceRestart);
            dialog.show();
        }
    }

    public void checkChangeCodePushPath(Context context) {
        //"热更新目录"为空，将assets中的rn文件拷贝过来
        if (FileUtils.isEmptyFolder(getCodePushPath())) {
            String baseJsBundleRootPath = getBaseBundleFilePath();
            FileUtils.copyFile(FileUtils.appendPathComponent(baseJsBundleRootPath, RNCodePushConstants.DEFAULT_JS_BUNDLE_FILE_NAME),
                    FileUtils.appendPathComponent(getCodePushPath(), RNCodePushConstants.DEFAULT_JS_BUNDLE_FILE_NAME));
            FileUtils.copyFile(FileUtils.appendPathComponent(baseJsBundleRootPath, RNCodePushConstants.DEFAULT_MANIFEST_FILE_NAME),
                    FileUtils.appendPathComponent(getCodePushPath(), RNCodePushConstants.DEFAULT_MANIFEST_FILE_NAME));
            FileUtils.copyFile(FileUtils.appendPathComponent(baseJsBundleRootPath, RNCodePushConstants.STATUS_FILE_NAME),
                    FileUtils.appendPathComponent(getCodePushPath(), RNCodePushConstants.STATUS_FILE_NAME));
        }
        //删除"备份目录"
        FileUtils.deleteFileOrFolderSilently(new File(getCodePushBackupPath()));
        //备份最近可用的RN文件到备份目录
        FileUtils.renameFile(getCodePushPath(), getCodePushBackupPath());
        //将"下载目录"中的文件，拷贝到"用于加载"的目录中
        FileUtils.renameFile(getCodePushDownloadPath(), getCodePushPath());
    }


    public void removePendingUpdate() {
        if (mDownloadPackageAsyncTask != null && mIsDownLoadPackaging) {
            mDownloadPackageAsyncTask.cancel(true);
        }
    }

    /**
     * 回滚
     */
    public void rollbackPackage() {
        FileUtils.deleteDirectoryAtPath(getCodePushPath());
        if (!FileUtils.isEmptyFolder(getCodePushBackupPath())) {
            FileUtils.renameFile(getCodePushBackupPath(), getCodePushPath());
        }
    }

    public RNSettingsManager getSettingsManager(Context context) {
        if (mSettingsManager == null) {
            mSettingsManager = new RNSettingsManager(context);
        }
        return mSettingsManager;
    }

    private LoadingDialogFragment mLoadingDialog;

    private void showLoading(Context context) {
        if (mLoadingDialog != null) {
            hideLoading();
        }
        mLoadingDialog = new LoadingDialogFragment();
        if (context instanceof FragmentActivity) {
            FragmentActivity activity = (FragmentActivity) context;
            if (activity.isDestroyed() || mLoadingDialog.isAdded() || mLoadingDialog.isVisible()) {
                return;
            }
            mLoadingDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "rn_loading_dialog");
        }
    }

    private void hideLoading() {
        if (mLoadingDialog != null && (mLoadingDialog.isAdded() || mLoadingDialog.isVisible())) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }
}
