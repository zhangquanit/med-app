package com.medlinker.login.sy;

import android.app.Activity;
import android.content.Context;

import com.blankj.utilcode.util.MetaDataUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.chuanglan.shanyan_sdk.OneKeyLoginManager;
import com.chuanglan.shanyan_sdk.listener.GetPhoneInfoListener;
import com.chuanglan.shanyan_sdk.listener.InitListener;
import com.chuanglan.shanyan_sdk.listener.OneKeyLoginListener;
import com.chuanglan.shanyan_sdk.listener.OpenLoginAuthListener;
import com.chuanglan.shanyan_sdk.tool.ShanYanUIConfig;
import com.medlinker.lib.log.LogUtil;
import com.medlinker.lib.utils.MedAppInfo;
import com.medlinker.lib.utils.MedPackageUtil;
import com.medlinker.lib.utils.MedToastUtil;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author: pengdaosong CreateTime:  2019-08-12 14:59 Email：pengdaosong@medlinker.com Description:
 */
public class SYManager {

    private static final String TAG = "SYManager";

    /**
     * 闪验初始化成功
     */
    private static final int SY_INIT_SUCCESS = 1022;
    /**
     * 预取号成功
     */
    public static final int SY_GET_PHONE_INFO_SUCCESS = 1022;

    /**
     * 拉起授权页成功
     */
    private static final int SY_OPEN_AUTH_SUCCESS = 1000;

    /**
     * 预取号成功时间戳
     */
    private static volatile long mSyPrefetchNumberTimestamp;

    /**
     * 闪验初始化状态
     */
    private static volatile int mSyInitStatus;

    /**
     * 预取状态
     */
    private static volatile int mSyPrePhoneStatus;

    private static OnSyLoginResult mLoginResult;
    private static boolean mIsFinish;
    private static boolean mShowToast;

    private static String getAppId() {
        return MetaDataUtils.getMetaDataInApp("sy_appId");
    }

    /**
     * 闪验初始化
     *
     * @param context
     */
    public static void init(Context context) {
        init(context, new InitListener() {
            @Override
            public void getInitStatus(int code, String result) {
                LogUtil.d(TAG, "init:code = " + code + "result =" + result);
                mSyInitStatus = code;
            }
        });
    }

    public static void init(Context context, InitListener listener) {
        LogUtil.d(TAG, "sy start init");
        if (!canInit()) {
            return;
        }
        OneKeyLoginManager.getInstance().init(context, getAppId(), listener);
    }

    public static void startLogin(final OnSyLoginResult onSyLoginResult) {
        startLogin(true, onSyLoginResult);
    }

    public static void startLogin(boolean isFinish, final OnSyLoginResult onSyLoginResult) {
        mLoginResult = onSyLoginResult;
        mIsFinish = isFinish;
        mShowToast = true;
        if (isSyPrefetchNumberSuccess()) {
            openLoginAuth();
        } else {
            syPrefetchNumber(true);
        }
    }

    /**
     * 初始化sy
     */
    public static void syPrefetchNumber(boolean toLogin) {
        LogUtil.d(TAG, "sy start init");
        if (isSyInitSuccess()) {
            getPhoneInfo(toLogin);
            return;
        }
        // 闪验
        OneKeyLoginManager.getInstance().init(MedAppInfo.getAppContext(), getAppId(), new InitListener() {
            @Override
            public void getInitStatus(int code, String result) {
                LogUtil.d(TAG, "init:code = " + code + "result =" + result);
                mSyInitStatus = code;
                if (isSyInitSuccess()) {
                    getPhoneInfo(toLogin);
                } else {
                    sendResult(null);
                }
            }
        });
    }

    /**
     * 预取号
     * 预取号有效期：60s(电信)/30min(联通)/60min(移动)。
     * 此方法需要1~2s的时间取得临时凭证，不建议和拉起授权页方法一起串行调用
     * 请勿频繁的多次调用和在拉起授权页后调用
     */
    public static void getPhoneInfo(boolean toLogin) {
        LogUtil.d(TAG, "sy getPhoneInfo start");
        long currentTime = System.currentTimeMillis();
        long cap = currentTime - mSyPrefetchNumberTimestamp;
        if (cap < 60 * 1000) {
            LogUtil.d(TAG, "sy getPhoneInfo success cap < 60s");
            sendResult(null);
            return;
        }
        OneKeyLoginManager.getInstance().getPhoneInfo(new GetPhoneInfoListener() {
            @Override
            public void getPhoneInfoStatus(int code, String result) {
                LogUtil.d(TAG, "getPhoneInfo:code = " + code + ",result =" + result);
                mSyPrePhoneStatus = code;
                if (isSyPrefetchNumberSuccess()) {
                    mSyPrefetchNumberTimestamp = System.currentTimeMillis();
                    if (toLogin) {
                        openLoginAuth();
                    }
                } else {
                    showToast(SYCodeUtils.code2Str(code));
                    sendResult(null);
                }
            }
        });
    }

    private static void sendResult(String result) {
        mShowToast=false;
        if (null != mLoginResult) {
            mLoginResult.onLoginResult(result);
        }
    }

    private static void openLoginAuth() {
        Activity activity = mLoginResult.getActivity();
        openLoginAuth(activity, mIsFinish, new OpenLoginAuthListener() {
            @Override
            public void getOpenLoginAuthStatus(int code, String s) {
                if (code != 1000) {
                    if (NetworkUtils.getMobileDataEnabled()) {
                        showToast(SYCodeUtils.code2Str(1006));
                    } else {
                        showToast(SYCodeUtils.code2Str(code));
                    }
                    sendResult(null);
                }
            }
        }, new OneKeyLoginListener() {
            @Override
            public void getOneKeyLoginStatus(int code, String result) {
                if (1011 == code) {
                    LogUtil.d(TAG, "一键登录返回");
                    sendResult(null);
                } else if (1000 != code) {
                    if (NetworkUtils.getMobileDataEnabled()) {
                        showToast(SYCodeUtils.code2Str(1006));
                    } else {
                        showToast(SYCodeUtils.code2Str(code));
                    }
                    sendResult(null);
                } else {
                    try {
                        JSONObject jobj = new JSONObject(result);
                        sendResult(jobj.getString("token"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        sendResult(null);
                    }
                }
            }
        });
    }

    /**
     * 拉起授权页
     * 在预取号回调成功之后调用，可以在多个需要登录的页面中调用。该方法调用成功会拉起登录界面，已登录状态请勿调用 。注意：每次调用拉起授权页方法前必须先调用授权页配置方法
     *
     * @param isFinish            点击授权页一键登录按钮有回调时是否自动销毁授权页：true：自动销毁 false:不自动销毁，开发者需主动调用销毁授权页方法进行授权页销毁操作
     * @param oneKeyLoginListener 一键登录监听
     */
    public static void openLoginAuth(final Activity activity, final boolean isFinish,
                                     final OpenLoginAuthListener openLoginAuthListener, final OneKeyLoginListener oneKeyLoginListener) {

        if (isSyPrefetchNumberFail() && !NetworkUtils.getMobileDataEnabled()) {
            showToast("请打开移动数据!");
            sendResult(null);
            return;
        }

        //自定义运营商授权页界面
        OneKeyLoginManager.getInstance().setAuthThemeConfig(mLoginResult.getConfig(activity));
        //开始拉取授权页
        OneKeyLoginManager.getInstance().openLoginAuth(isFinish, openLoginAuthListener, oneKeyLoginListener);
    }

    /**
     * 闪验必须开启移动数据，必须在主进程初始化，部分OPPO手机只能在开启移动数据情况下才能正常使用闪验
     *
     * @return
     */
    private static boolean canInit() {
        if (!isMainProcess()) {
            LogUtil.i(TAG, "sy not init because not main process");
            return false;
        }
        return true;
    }

    /**
     * 闪验需要在主进程进行初始化
     *
     * @return
     */
    private static boolean isMainProcess() {
        return MedPackageUtil.isMainProcess(MedAppInfo.getAppContext());
    }

    /**
     * 是否预取号成功
     *
     * @return
     */
    public static boolean isSyPrefetchNumberSuccess() {
        return SY_GET_PHONE_INFO_SUCCESS == mSyPrePhoneStatus;
    }

    public static void setSyPrePhoneStatus(int mSyPrePhoneStatus) {
        SYManager.mSyPrePhoneStatus = mSyPrePhoneStatus;
    }

    /**
     * 是否预取号失败
     *
     * @return
     */
    public static boolean isSyPrefetchNumberFail() {
        return 0 != mSyPrePhoneStatus && SY_GET_PHONE_INFO_SUCCESS != mSyPrePhoneStatus;
    }

    public static boolean isSyInitSuccess() {
        return mSyInitStatus == SY_INIT_SUCCESS;
    }

    private static void showToast(String msg) {
        if (mShowToast) {
            MedToastUtil.showMessage(msg);
        }
    }

    public interface OnSyLoginResult {
        void onLoginResult(String result);

        Activity getActivity();

        ShanYanUIConfig getConfig(Activity activity);

    }
}
