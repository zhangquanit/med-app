package net.medlinker.android;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.blankj.utilcode.util.ProcessUtils;
import com.medlinker.base.startup.AppInitializer;
import com.medlinker.baseapp.ApiPath;
import com.medlinker.lib.log.LogUtil;
import com.medlinker.protocol.PrivacyManager;
import com.medlinker.protocol.PrivacyUtil;
import com.tencent.smtt.sdk.QbSdk;

/**
 * @author zhangquan
 */
public class MedlinkerApp extends Application {
    private final static String TAG = MedlinkerApp.class.getSimpleName();
    public static MedlinkerApp mApp;
    public static boolean mColdStart = false;
    public static long startTime = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        startTime = System.currentTimeMillis();
        mColdStart = true;
        mApp = this;

        AppInitializer.getInstance(this).onCreate();
        LogUtil.d(TAG, AppInitializer.getInstance(this).getApps().toString());

        initDebugTools(this);
        initPrivacy();
        //是否同意隐私政策
        boolean privacyGranted = PrivacyUtil.isPrivacyGranted();
        if (privacyGranted) {
            initSensitiveSDK();
        } else {
            QbSdk.disableSensitiveApi();
        }
        //只在主进程中初始化
        if (ProcessUtils.isMainProcess()) {
            if (privacyGranted) {  //涉及收集mac、imei、imsi等SDK的初始化
                initSensitiveSDKOnMainProcess();
            }
        }
        //可以异步初始化的SDK
        new Thread(() -> AppInitializer.getInstance(this).initAsync()).start();

        LogUtil.d(TAG, "App冷启动 onCreate花费：" + (System.currentTimeMillis() - startTime));
    }

    private void initPrivacy() {
        PrivacyManager.INSTANCE.init(BuildConfig.clientName, BuildConfig.clientName,
                ApiPath.APP_PRIVACY_STATEMENT_URL,
                ApiPath.USER_AGREEMENT_URL,
                getResources().getColor(R.color.color_main), R.mipmap.dialog_icon_tip_privacy
           ,"");
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * debugTools工具菜单初始化，会通过字节码操作注入代码，请勿删除
     */
    private void initDebugTools(Application application) {
    }

    /**
     * 初始化含有敏感信息(隐私、设备)的SDK
     */
    public void initSensitiveSDK() {
        AppInitializer.getInstance(this).initSensitiveSDK();
    }

    /**
     * 在主线程中初始化含有敏感信息(隐私、设备)的SDK
     */
    public void initSensitiveSDKOnMainProcess() {
        AppInitializer.getInstance(this).initSensitiveSDKOnMainProcess();
    }
}
