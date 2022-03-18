package com.medlinker.reactnative.codepush.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.medlinker.reactnative.codepush.RNCodePush;
import com.medlinker.reactnative.codepush.entity.RNModuleEntity;
import com.medlinker.reactnative.codepush.entity.RNRouteEntity;

import java.io.File;
import java.util.ArrayList;

/**
 * @author hmy
 */
public class RNRouteUtils {

    private static RNRouteUtils mInstance;
    private static RNRouteEntity mRNRouteEntity;

    private boolean mIsInitLoad = false;

    public static synchronized RNRouteUtils getInstance() {
        if (mInstance == null) {
            mInstance = new RNRouteUtils();
        }
        return mInstance;
    }

    /**
     * 初始化 rn路由配置文件
     *
     * @param context
     */
    @SuppressLint("StaticFieldLeak")
    public synchronized void init(final Context context) {
        if (mIsInitLoad) {
            return;
        }
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                mIsInitLoad = true;
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
//                    RNEncryptUtils.initEncryptFiles(context); //启动页预加载RN，避免重复，此处屏蔽
                    RNCodePush.getInstance().checkDeleteOldVersionCodePushFiles();
//                    File currentManifestFile = getCurrentCodePushManifest();
//                    String manifestJson = null;
//                    if (currentManifestFile.exists() && currentManifestFile.isFile()
//                            && currentManifestFile.canRead()) {
//                        manifestJson = FileUtils.getJsonStringFromFile(currentManifestFile);
//                    }
//                    if (TextUtils.isEmpty(manifestJson)) {
//                        File defaultManifestFile = new File(FileUtils.appendPathComponent(
//                                RNCodePush.getInstance().getUpdateManager().getBaseBundleFilePath(),
//                                RNCodePushConstants.DEFAULT_MANIFEST_FILE_NAME));
//                        manifestJson = FileUtils.getJsonStringFromFile(defaultManifestFile);
//                    }
//
//                    mRNRouteEntity = new RNRouteEntity(new JSONObject(manifestJson));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                mIsInitLoad = false;
            }
        };
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 获取当前包对应的manifest配置文件
     *
     * @return
     */
    private File getCurrentCodePushManifest() {
        String manifestFilePath = RNCodePush.getInstance().getUpdateManager().getCodePushManifestFilePath();
        return new File(manifestFilePath);
    }


    /**
     * 判断RN Page 合法
     *
     * @param moduleName
     * @param routeName
     * @return
     */
    public synchronized boolean containsRoute(String moduleName, String routeName) {
        if (mRNRouteEntity == null || TextUtils.isEmpty(routeName)) {
            return false;
        }
        RNModuleEntity moudle = findModule(moduleName);
        if (moudle == null) return false;
        ArrayList<String> routes = moudle.getRoutes();
        if (routes == null || routes.size() == 0) return false;
        for (String route : routes) {
            if (routeName.equals(route)) {
                return true;
            }
        }
        return false;
    }

    private RNModuleEntity findModule(String moduleName) {
        if (mRNRouteEntity == null || TextUtils.isEmpty(moduleName)) return null;
        ArrayList<RNModuleEntity> modules = mRNRouteEntity.getModules();
        if (modules == null || modules.size() == 0) return null;
        for (RNModuleEntity entity : modules) {
            if (moduleName.equals(entity.getName())) {
                return entity;
            }
        }
        return null;
    }

    public synchronized boolean isDeleted(String routeName) {
        if (mRNRouteEntity == null || TextUtils.isEmpty(routeName)) {
            return false;
        }
        ArrayList<RNModuleEntity> modules = mRNRouteEntity.getDeletedModules();
        if (modules == null || modules.size() == 0) {
            return false;
        }
        for (RNModuleEntity module : modules) {
            ArrayList<String> routes = module.getRoutes();
            if (routes == null || routes.size() == 0) {
                continue;
            }
            for (String route : routes) {
                if (TextUtils.equals(route, routeName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public synchronized boolean isDeleted(String moduleName, String routeName) {
        if (mRNRouteEntity == null || TextUtils.isEmpty(routeName)) {
            return false;
        }
        RNModuleEntity module = findDeletedModule(moduleName);
        if (module == null) {
            return false;
        }
        ArrayList<String> routes = module.getRoutes();
        if (routes == null || routes.size() == 0) {
            return false;
        }
        for (String route : routes) {
            if (routeName.equals(route)) {
                return true;
            }
        }
        return false;
    }

    private RNModuleEntity findDeletedModule(String moduleName) {
        if (mRNRouteEntity == null || TextUtils.isEmpty(moduleName)) {
            return null;
        }
        ArrayList<RNModuleEntity> modules = mRNRouteEntity.getDeletedModules();
        if (modules == null || modules.size() == 0) {
            return null;
        }
        for (RNModuleEntity entity : modules) {
            if (moduleName.equals(entity.getName())) {
                return entity;
            }
        }
        return null;
    }
}
