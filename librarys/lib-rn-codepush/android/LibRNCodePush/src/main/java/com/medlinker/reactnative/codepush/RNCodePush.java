package com.medlinker.reactnative.codepush;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.doctorwork.android.logreport.LogReport;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.medlinker.reactnative.codepush.entity.MetaInfoEntity;
import com.medlinker.reactnative.codepush.entity.PendingUpdateEntity;
import com.medlinker.reactnative.codepush.logreport.InitCheckFIleLog;
import com.medlinker.reactnative.codepush.logreport.RNCodePushLog;
import com.medlinker.reactnative.codepush.util.FileUtils;
import com.medlinker.reactnative.codepush.util.RNEncryptUtils;
import com.medlinker.reactnative.codepush.util.RNReloadUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author hmy
 */
public class RNCodePush implements ReactPackage {

    private static RNCodePush sInstance;
    private static Application sApp;
    private static boolean sIsDebugMode;
    private static boolean sIsDownloadByVersion;
    private static String sServerUrl;

    private static boolean sIsRunningBinaryVersion = true;
    private static boolean sIsLoadedJSBundle = false;

    private RNCodePushUpdateManager mUpdateManager;
    private RNSettingsManager mSettingsManager;
    private static Config sConfig;
    private static AsyncTask<Void, Void, Void> mCheckFilesTask;
    private static RNCodePushLog rnCodePushLog;
    private static boolean hasInitCheckFile;
    private static boolean hasReportInitCheckFile;

    public static void init(Application context, boolean isDebugMode, boolean isDownloadByVersion, String serverUrl, final Config config) {
        sInstance = new RNCodePush(context, isDebugMode, isDownloadByVersion, serverUrl);
        sConfig = config;
    }

    public static void initCheckFiles(final CallBack<Void> callBack) {
        if (null != mCheckFilesTask && mCheckFilesTask.getStatus() != AsyncTask.Status.FINISHED) {
            return;
        }
        mCheckFilesTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                sInstance.checkDeleteOldVersionCodePushFiles();
                RNEncryptUtils.initEncryptFiles(sApp);
                sInstance.initializeUpdateAfterRestart();
                sInstance.checkRollback();
                return null;
            }

            @Override
            protected void onPostExecute(Void o) {
                if (null != callBack) {
                    callBack.onCallBack(o);
                }
                addCheckFileLog();
                mCheckFilesTask = null;

            }

        };
        mCheckFilesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static void addCheckFileLog() {
        reportLogIfNeeded();
        hasInitCheckFile = true;
    }

    public static void reportLogIfNeeded() {
        if (rnCodePushLog != null) {
            if (rnCodePushLog.isNeedReport()) {
                LogReport.getInstance().reportCustomEvent(rnCodePushLog);
            }
            rnCodePushLog.clear();
        }
    }

    private static void reportNotCheckFileLogIfNeeded() {
        if (!hasInitCheckFile && !hasReportInitCheckFile) {
            hasReportInitCheckFile = true;
            InitCheckFIleLog initCheckFileLog = new InitCheckFIleLog(true);
            LogReport.getInstance().reportCustomEvent(initCheckFileLog);
        }
    }

    public static RNCodePush getInstance() {
        if (sInstance == null) {
            sInstance = new RNCodePush(sApp, sIsDebugMode, sIsDownloadByVersion, sServerUrl);
        }
        return sInstance;
    }

    public RNCodePush(Application context, boolean isDebugMode, boolean isDownloadByVersion, String serverUrl) {
        sApp = context;
        sIsDebugMode = isDebugMode;
        sIsDownloadByVersion = isDownloadByVersion;
        sServerUrl = serverUrl;
        sIsRunningBinaryVersion = true;
        sIsLoadedJSBundle = false;
        mSettingsManager = new RNSettingsManager(context);
        rnCodePushLog = new RNCodePushLog();
    }

    public void setServerUrl(String serverUrl) {
        sServerUrl = serverUrl;
        mUpdateManager = null;
    }

    public String getServerUrl() {
        return sServerUrl;
    }

    public RNCodePushUpdateManager getUpdateManager() {
        if (mUpdateManager == null) {
            String rootFolderName = RNCodePushConstants.CODE_PUSH_ROOT_FOLDER + File.separator + FileUtils.getAppVersionName(sApp);
            mUpdateManager = new RNCodePushUpdateManager(sApp.getFilesDir().getAbsolutePath(),
                    FileUtils.appendPathComponent(sApp.getFilesDir().getAbsolutePath(), rootFolderName), getServerUrl());
        }
        return mUpdateManager;
    }

    public RNSettingsManager getSettingsManager() {
        if (mSettingsManager == null) {
            mSettingsManager = new RNSettingsManager(sApp);
        }
        return mSettingsManager;
    }

    public void checkDeleteOldVersionCodePushFiles() {
        File rootFile = new File(FileUtils.appendPathComponent(sApp.getFilesDir().getAbsolutePath(),
                RNCodePushConstants.CODE_PUSH_ROOT_FOLDER));
        if (!rootFile.exists()) {
            return;
        }
        File[] files = rootFile.listFiles();
        if (files == null) {
            return;
        }
        String currentAppVersion = FileUtils.getAppVersionName(sApp);
        for (File file : files) {
            if (file != null) {
                if (!TextUtils.equals(file.getName(), currentAppVersion)) {
                    FileUtils.deleteDirectoryAtPath(file.getPath());
                }
            }
        }
    }

    public static String getJSBundleFile() {
        sIsLoadedJSBundle = true;
        String jsBundleFile;
        if (sInstance != null) {
            jsBundleFile = sInstance.getJSBundleFileInternal();
        } else {
            jsBundleFile = getDefaultJsBundleFile();
        }
        return jsBundleFile;
    }

    private static String getDefaultJsBundleFile() {
//        RNEncryptUtils.initEncryptFiles(sApp);
        String decryptJsBundleFilePath = FileUtils.appendPathComponent(getInstance().getUpdateManager().getBaseBundleFilePath(),
                RNCodePushConstants.DEFAULT_JS_BUNDLE_FILE_NAME);
        if (BuildConfig.DEBUG && !FileUtils.fileAtPathExists(decryptJsBundleFilePath)) {
            return RNCodePushConstants.ASSETS_BUNDLE_PREFIX + RNCodePushConstants.DEFAULT_JS_BUNDLE_FILE_NAME;
        }
        return decryptJsBundleFilePath;
    }

    private String getJSBundleFileInternal() {
        String binaryJsBundleUrl = getDefaultJsBundleFile();

        String packageFilePath = getUpdateManager().getCurrentPackageBundlePath();
        if (packageFilePath == null) {
            // There has not been any downloaded updates.
            sIsRunningBinaryVersion = true;
            return binaryJsBundleUrl;
        }

        MetaInfoEntity metaInfo = getUpdateManager().getCurrentPackageMetaInfo();
        if (isLastBundlePackage(metaInfo)) {
            sIsRunningBinaryVersion = false;
            if (getSettingsManager().isUnloadUpdate(metaInfo)) { // 有未加载的更新，才需要做回滚逻辑
                getSettingsManager().saveFailedUpdate(metaInfo.toJSONObject());
                getSettingsManager().removeUnloadUpdate();
            }
            return packageFilePath;
        } else {
            if (!sIsDebugMode) {
                clearUpdates();
            }
            sIsRunningBinaryVersion = true;
            return binaryJsBundleUrl;
        }
    }

    private boolean isRunningBinaryVersion() {
        return sIsRunningBinaryVersion;
    }

    public MetaInfoEntity getCurrentLoadPackageMetaInfo() {
        if (isRunningBinaryVersion()) {
            return getUpdateManager().getAssetsMetaInfo();
        }
        return getUpdateManager().getCurrentPackageMetaInfo();
    }

    private boolean isLastBundlePackage(MetaInfoEntity metaInfo) {
        if (metaInfo == null || !metaInfo.isExist()) {
            return false;
        }
        return getUpdateManager().isUpdate(metaInfo, getAssetsMetaInfo());
    }

    private MetaInfoEntity getAssetsMetaInfo() {
        try {
            String metaJson = FileUtils.getJsonStringFromFile(new File(FileUtils.appendPathComponent(
                    getUpdateManager().getBaseBundleFilePath(), RNCodePushConstants.STATUS_FILE_NAME)));
            JSONObject jsonObject = new JSONObject(metaJson);
            return new MetaInfoEntity(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new RuntimeException("Error in assets://android.meta", e);
        }
    }

    public void clearUpdates() {
        try {
            getUpdateManager().clearUpdates();
            getUpdateManager().removePendingUpdate();
            getSettingsManager().removeFailedUpdates();
            getSettingsManager().removePendingUpdate();
        } catch (Exception e) {
            RNCodePush.getRnCodePushLog().setClearUpdateError(e.getMessage());
            e.printStackTrace();
        }
    }

    public void removeCurrentFailedUpdates() {
        MetaInfoEntity infoEntity = getCurrentLoadPackageMetaInfo();
        getSettingsManager().removeFailedUpdates(infoEntity.getHash());
    }

    public static boolean isLoadedJSBundle() {
        return sIsLoadedJSBundle;
    }

    private void initializeUpdateAfterRestart() {
        try {
            PendingUpdateEntity pendingUpdateEntity = getSettingsManager().getPendingUpdate();
            if (pendingUpdateEntity.isExist() && pendingUpdateEntity.isLoading()) {
                MetaInfoEntity metaInfoEntity = getUpdateManager().getCurrentPackageMetaInfo();
                boolean isNeedUpdate = false;
                if (metaInfoEntity.isExist()) {
                    if (getUpdateManager().isUpdate(pendingUpdateEntity, metaInfoEntity)) {
                        isNeedUpdate = true;
                    }
                } else {
                    MetaInfoEntity assetsMetaInfo = getAssetsMetaInfo();
                    if ((getUpdateManager().isUpdate(pendingUpdateEntity, assetsMetaInfo))
                            && getUpdateManager().isSameBaseVersion(pendingUpdateEntity, assetsMetaInfo)) {
                        isNeedUpdate = true;
                    }
                }

                if (isNeedUpdate) {
                    getUpdateManager().checkChangeCodePushPath(sApp);
                }
                getSettingsManager().removePendingUpdate();
            }
        } catch (Exception e) {
            RNCodePush.getRnCodePushLog().setInitUpdateAfterRestart(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 检查回滚
     */
    private void checkRollback() {
        MetaInfoEntity metaInfoEntity = getUpdateManager().getCurrentPackageMetaInfo();
        if (getSettingsManager().isFailedHash(metaInfoEntity.getHash())) {
            getUpdateManager().rollbackPackage();
        }
    }

    public void checkUpdate(Context context, boolean isDebug, boolean auto) {
        checkUpdate(context, isDebug, auto, false, null);
    }

    /**
     * 从远程下载meta文件，用于检查更新
     *
     * @param isDebug             是否是开发环境
     * @param auto                true:自动后台更新， false:显示弹窗，用户手动确认更新
     * @param isFunctionCheck     是否功能检测（在加载界面不存在时，需要检查更新，并给用户提示）
     * @param checkUpdateCallBack 是否有更新回调
     */
    public void checkUpdate(Context context, boolean isDebug, boolean auto, boolean isFunctionCheck, CallBack<Boolean> checkUpdateCallBack) {
        if (!isDebug) {
            getUpdateManager().checkUpdate(context, auto, checkUpdateCallBack, isFunctionCheck);
        }
    }

    public synchronized void checkReload(boolean isDebug) {
        if (!isDebug) {
            reportNotCheckFileLogIfNeeded();
            PendingUpdateEntity pendingUpdateEntity = getSettingsManager().getPendingUpdate();
            if (pendingUpdateEntity.isExist() && pendingUpdateEntity.isLoading()) {
                sInstance = new RNCodePush(sApp, sIsDebugMode, sIsDownloadByVersion, sServerUrl);
                rnCodePushLog.setReload(true);
                initializeUpdateAfterRestart();
                checkRollback();
                RNReloadUtil.getInstance().reloadBundle(sApp);
                reportLogIfNeeded();
            }
        }
    }

    public static Config getConfig() {
        return sConfig;
    }

    public static boolean isDebugMode() {
        return sIsDebugMode;
    }

    public static boolean isDownloadByVersion() {
        return sIsDownloadByVersion;
    }

    public static void setIsDownloadByVersion(boolean isDownloadByVersion) {
        sIsDownloadByVersion = isDownloadByVersion;
    }

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        modules.add(new MedHotUpdateModule(reactContext));
        return modules;
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        return Arrays.asList();
    }

    public static RNCodePushLog getRnCodePushLog(){
        return rnCodePushLog;
    }
}
