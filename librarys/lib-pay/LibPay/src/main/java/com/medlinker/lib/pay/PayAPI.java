package com.medlinker.lib.pay;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.medlinker.lib.pay.core.IResetable;
import com.medlinker.lib.pay.core.ITaskManager;
import com.medlinker.lib.pay.core.internal.TaskManagerImpl;
import com.medlinker.lib.utils.MedAppInfo;

/**
 * @author guojianming
 * @description 支付的API
 * @date 2021/11/22 11:05 上午
 */
public class PayAPI implements IResetable {


    private static final Object mLock = new Object();
    private static PayAPI mInstance;

    private boolean isDebugMode = false;
    private WxPayer wxPayer;
    private ITaskManager mTaskManager = new TaskManagerImpl();

    public static PayAPI getInstance() {
        if (mInstance == null) {
            synchronized (mLock) {
                if (mInstance == null) {
                    mInstance = new PayAPI();
                    mInstance.setDebugMode(MedAppInfo.isDebug());
                }
            }
        }
        return mInstance;
    }

    public void setDebugMode(boolean debugMode) {
        isDebugMode = debugMode;
    }

    public boolean isDebugMode() {
        return isDebugMode;
    }

    public WxPayer getWxPayer() {
        return wxPayer;
    }

    public ITaskManager getTaskManager() {
        return mTaskManager;
    }

    /**
     * 支付宝支付请求
     *
     * @param aliPayRe
     */
    public void sendPayRequest(AliPayReq aliPayRe) {
        new AliPayer().sendPayReq(aliPayRe);
    }


    /**
     * 微信支付请求
     *
     * @param wxPayReq
     */
    public void sendPayRequest(WxPayReq wxPayReq) {
        wxPayer = new WxPayer();
        wxPayer.send(wxPayReq);
    }

    public boolean handleIntent(Intent intent) {
        if (wxPayer != null) {
            return wxPayer.onHandleIntent(intent);
        }
        return false;
    }


    @Override
    public void reset() {
        if (wxPayer != null) wxPayer.reset();
        mTaskManager.reset();
    }

    public String getWxPayAppId(Context context) {
        if (context != null) {
            try {
                ApplicationInfo info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                return info.metaData.getCharSequence("WECHAT_APPID").toString();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                Log.e("WXEntryActivity","请在app module的AndroidManifest中配置微信【WECHAT_APPID】");
            }
        }
        return null;
    }
}

