package com.medlinker.reactnative.codepush.util;

import android.app.Application;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.JSBundleLoader;
import com.medlinker.reactnative.codepush.RNCodePush;

import java.lang.reflect.Field;


/**
 * @author hmy
 * @time 2020/6/10 10:52
 */
public class RNReloadUtil {

    private Application mApplication;

    public static RNReloadUtil getInstance() {
        return Singleton.sInstance;
    }

    private static class Singleton {
        private static final RNReloadUtil sInstance = new RNReloadUtil();
    }

    public void reloadBundle(Application application) {
        mApplication = application;
        if (null == mApplication) {
            return;
        }
        final ReactInstanceManager instanceManager;
        try {
            instanceManager = resolveInstanceManager();
            if (instanceManager == null) {
                return;
            }
            String latestJSBundleFile = RNCodePush.getJSBundleFile();
            setJSBundle(instanceManager, latestJSBundleFile);

            if (!instanceManager.hasStartedCreatingInitialContext()) {
                instanceManager.createReactContextInBackground();
            } else {
                instanceManager.recreateReactContextInBackground();
            }
        } catch (Exception e) {
            RNCodePush.getRnCodePushLog().setReloadBundle(e.getMessage());
            e.printStackTrace();
        }
    }

    private ReactInstanceManager resolveInstanceManager() {
        ReactInstanceManager instanceManager;
        instanceManager = ((ReactApplication) mApplication).getReactNativeHost().getReactInstanceManager();
        return instanceManager;
    }

    private void setJSBundle(ReactInstanceManager instanceManager, String latestJSBundleFile) throws IllegalAccessException {
        try {
            JSBundleLoader latestJSBundleLoader;
            if (latestJSBundleFile.toLowerCase().startsWith("assets://")) {
                latestJSBundleLoader = JSBundleLoader.createAssetLoader(mApplication, latestJSBundleFile, false);
            } else {
                latestJSBundleLoader = JSBundleLoader.createFileLoader(latestJSBundleFile);
            }
            Field bundleLoaderField = instanceManager.getClass().getDeclaredField("mBundleLoader");
            bundleLoaderField.setAccessible(true);
            bundleLoaderField.set(instanceManager, latestJSBundleLoader);
            bundleLoaderField.setAccessible(false);
        } catch (Exception e) {
            RNCodePush.getRnCodePushLog().setSetJsBundle(e.getMessage());
            throw new IllegalAccessException("Could not setJSBundle");
        }
    }
}
