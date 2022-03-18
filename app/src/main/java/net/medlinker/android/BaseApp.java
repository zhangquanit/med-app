package net.medlinker.android;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ProcessUtils;
import com.doctorwork.android.logreport.LogReport;
import com.doctorwork.android.logreport.okhttp.OkHttpCallAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medlinker.analytics.MedAnalytics;
import com.medlinker.analytics.User;
import com.medlinker.base.startup.ApplicationLike;
import com.medlinker.baseapp.AppClient;
import com.medlinker.baseapp.EventType;
import com.medlinker.baseapp.route.RouterInit;
import com.medlinker.lib.log.LogUtil;
import com.medlinker.lib.permission.ext.MedPermissionConfig;
import com.medlinker.lib.push.med.PushClient;
import com.medlinker.lib.push.med.PushOptions;
import com.medlinker.lib.utils.MedAppInfo;
import com.medlinker.lib.utils.MedDeviceUtil;
import com.medlinker.lib.utils.MedToastUtil;
import com.medlinker.network.retrofit.BusinessCodeEntity;
import com.medlinker.network.retrofit.INetworkConfig;
import com.medlinker.network.retrofit.RetrofitProvider;
import com.medlinker.network.retrofit.logreport.LogPerformanceInterceptor;
import com.medlinker.network.retrofit.logreport.LogReportInterceptor;
import com.meituan.android.walle.WalleChannelReader;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mmkv.MMKV;
import com.tencent.mmkv.MMKVLogLevel;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.umcrash.UMCrash;

import net.medlinker.base.account.AccountUtil;
import net.medlinker.base.account.LoginInfo;
import net.medlinker.base.account.OnUserInfoChangeListener;
import net.medlinker.base.account.UserInfo;
import net.medlinker.base.entity.DataEntity;
import net.medlinker.base.event.EventBusUtils;
import net.medlinker.base.event.EventMsg;
import net.medlinker.base.manager.ActivityStashManager;
import net.medlinker.base.network.interceptor.OkHttpLogInterceptor;
import net.medlinker.libhttp.BaseEntity;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.exceptions.CompositeException;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.Interceptor;

/**
 * @author zhangquan
 */
public class BaseApp implements ApplicationLike {
    private String mChannelId;
    private Application mCtx;
    private static final int API_TYPE_ONLINE = 3;

    @Override
    public void onCreate(Application ctx) {
        mCtx = ctx;
        initAppInfo();
        AppClient.init(BuildConfig.clientName);
        LogUtil.setSetting(new LogUtil.Setting().setDebugMode(BuildConfig.LOG_DEBUG).setDefaultTag("MedLinker"));
        MMKV.initialize(ctx, MMKVLogLevel.LevelDebug); //微信MMKV
        initLogReport();
        initRetrofit();
        RouterInit.init(ctx);
        initUmengApm(true);
        registerUserInfoListener();
        handleRxJavaException();
        if (ProcessUtils.isMainProcess()) {
            initRefreshHeader();
        }
        initPermissionConfig();
    }

    @Override
    public void initSensitiveSDK(Application ctx) {
        initMedAnalytics();
        initBugly();
        initUmengApm(false);
        initPush();
    }

    @Override
    public void initSensitiveSDKOnMainProcess(Application ctx) {

    }

    @Override
    public void initAsync(Application ctx) {

    }

    @Override
    public int getPriority() {
        return 10;
    }

    private void initAppInfo() {
        MedAppInfo.setAppContext(mCtx);
        MedAppInfo.setApplicationId(BuildConfig.APPLICATION_ID);
        MedAppInfo.setDebug(isDebug());
        MedAppInfo.setEnvType(BuildConfig.API_URL_TYPE);
        MedAppInfo.setVersionCode(BuildConfig.VERSION_CODE);
        MedAppInfo.setVersionName(BuildConfig.VERSION_NAME);
    }

    private void initPermissionConfig() {
        MedPermissionConfig.addExplain(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, "修改用户头像、聊天发图片时访问存储权限");
        MedPermissionConfig.addExplain(android.Manifest.permission.CAMERA, "视频聊天、发送图片消息、扫一扫时访问相机权限");
        MedPermissionConfig.addExplain(android.Manifest.permission.RECORD_AUDIO, "视频聊天、发送语音消息、语音识别时访问麦克风权限");
        MedPermissionConfig.addExplain(Manifest.permission.ACCESS_FINE_LOCATION, "添加收货地址，需要获取设备的定位信息");
    }

    private void initBugly() {
        if (!BuildConfig.buglyEnabled) {
            return;
        }
        //在开发测试阶段，可以在初始化Bugly之前通过以下接口把调试设备设置成“开发设备”。
        if (isDebug()) {
            CrashReport.setIsDevelopmentDevice(mCtx, true);
        }
        //初始化配置
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(mCtx);
        String processName = ProcessUtils.getCurrentProcessName();
        strategy.setUploadProcess(processName == null || processName.equals(mCtx.getPackageName()));
        strategy.setAppChannel(getAppChannel()); //渠道
        //3.4.4及之后版本使用guid替换Android id，建议App设置业务自己的deviceId给bugly。
//            strategy.setDeviceID(getDeviceId());
        strategy.setCrashHandleCallback(new CrashReport.CrashHandleCallback() {
            /**
             * Crash处理.
             * @param crashType 错误类型：CRASHTYPE_JAVA，CRASHTYPE_NATIVE，CRASHTYPE_U3D ,CRASHTYPE_ANR
             * @param errorType 错误的类型名
             * @param errorMessage 错误的消息
             * @param errorStack 错误的堆栈
             * @return 返回额外的自定义信息上报
             */
            @Override
            public synchronized Map<String, String> onCrashHandleStart(int crashType, String errorType,
                                                                       String errorMessage, String errorStack) {
                LinkedHashMap<String, String> map = new LinkedHashMap();
                //这里返回额外的信息 可在官网crash详情页面 点击extraMessage.txt查看
                try {
                    LoginInfo loginInfo = AccountUtil.INSTANCE.getLoginInfo();
                    if (loginInfo != null) {
                        map.put("userId", String.valueOf(loginInfo.getUserId()));
                        map.put("sess", loginInfo.getSessionId());
                    }
                    UserInfo userInfo = AccountUtil.INSTANCE.getUserInfo();
                    if (null != userInfo) {
                        map.put("userName", userInfo.getUsername());
                        map.put("phone", userInfo.getCellphone());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return map;
            }
        });

        String appId = isDebug() ? BuildConfig.bugly_debug_appId : BuildConfig.bugly_release_appId;
        CrashReport.initCrashReport(mCtx, appId, isDebug(), strategy);
    }

    private void initRefreshHeader() {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                ClassicsHeader header = new ClassicsHeader(context);
                header.setBackgroundColor(Color.TRANSPARENT);
                return header;
            }
        });
    }

    private void registerUserInfoListener() {
        //设置用户信息
        AccountUtil.INSTANCE.addUserInfoChangeListener(new OnUserInfoChangeListener() {
            @Override
            public void onLoginInfoChanged(@NotNull LoginInfo loginInfo) {
                if (BuildConfig.buglyEnabled) {
                    CrashReport.setUserId(String.valueOf(loginInfo.getUserId()));
                }
            }

            @Override
            public void onUserInfoChanged(@NotNull UserInfo userInfo) {
                if (BuildConfig.buglyEnabled) {
                    CrashReport.setUserId(String.valueOf(userInfo.getId()));
                }
                MedAnalytics.setUser(new User(Objects.requireNonNull(userInfo.getId()).toString()));
            }
        });
    }

    private void initUmengApm(boolean isPreInit) {
        if (!BuildConfig.umengApmEnabled) return;
        if (isPreInit) {
            /**
             * 应umeng要求调用preinit，umeng官方声明不会导致工信部隐私权限校验问题
             */
            UMConfigure.preInit(mCtx, BuildConfig.umeng_apm_appkey, getAppChannel());
            UMConfigure.setLogEnabled(isDebug());
        } else {
            UMConfigure.init(mCtx, BuildConfig.umeng_apm_appkey, getAppChannel(), UMConfigure.DEVICE_TYPE_PHONE, "");
            UMCrash.registerUMCrashCallback(() -> {
                UserInfo userInfo = AccountUtil.INSTANCE.getUserInfo();
                if (null != userInfo) {
                    return "userId:" + userInfo.getId();
                } else {
                    return "";
                }
            });
        }

    }

    private void initMedAnalytics() {
        MedAnalytics.init(mCtx, new com.medlinker.analytics.Configuration()
                .channel(getAppChannel())
                .debug(isDebug())
                .platformName(BuildConfig.analyticsPlatforName)//@see https://wiki.medlinker.com/pages/viewpage.action?pageId=45551586
                .userType(BuildConfig.analyticsUserType) //角色，患者端可以填写患者
                .trackType(MedAnalytics.TRACK_SENSORS_DATA));
        MedAnalytics.trackAppInstall();
    }

    private void initPush() {
        PushOptions options = new PushOptions.Builder().setSourse(BuildConfig.pushSource).build();
        PushClient.init(mCtx, options);
    }

    private String getAppChannel() {
        if (TextUtils.isEmpty(mChannelId)) {
            mChannelId = WalleChannelReader.getChannel(mCtx,
                    "medlinker");
        }
        return mChannelId;
    }

    private boolean isDebug() {
        return BuildConfig.API_URL_TYPE != API_TYPE_ONLINE;
    }

    private void handleRxJavaException() {
        //处理RxJava  没有设置onError回调  io.reactivex.exceptions.OnErrorNotImplementedException
        RxJavaPlugins.setErrorHandler(throwable -> {
            if (throwable instanceof CompositeException) { //打印异常
                CompositeException exception = (CompositeException) throwable;
                List<Throwable> exceptions = exception.getExceptions();
                for (Throwable item : exceptions) {
                    item.printStackTrace();
                }
            } else {
                throwable.printStackTrace();
            }
        });
    }

    private void initLogReport() {
        if (BuildConfig.logReportEnabled) {
            LogReport.init(mCtx,
                    isDebug() ? BuildConfig.logreport_debug_appkey : BuildConfig.logreport_release_appkey,
                    getAppChannel(), LogReport.URL_CUSTOM)
                    .setReportNetWorkPerformance(true)
                    .setNetworkElapsedTimeExceed(4000)
                    .addCallAdapterFactory(OkHttpCallAdapterFactory.create())
                    .setBaseUrl(isDebug() ? "https://web-monitor-qa.medlinker.com/" : "https://web-monitor.medlinker.com/")
                    .build();
        }
    }

    private void initRetrofit() {
        RetrofitProvider.init(new INetworkConfig() {
            @Override
            public File getNetCacheDir() {
                File dir = new File(mCtx.getCacheDir(), "network_cache");
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                return dir;
            }

            @Override
            public boolean isDebug() {
                return BuildConfig.DEBUG;
            }

            @Override
            public boolean isOnlineEnv() {
                return MedAppInfo.getEnvType() == API_TYPE_ONLINE;
            }

            @Override
            public BusinessCodeEntity getBusinessCode(String responseJson) {
                BaseEntity entity = new Gson().fromJson(responseJson, new TypeToken<BaseEntity<DataEntity>>() {
                }.getType());
                return new BusinessCodeEntity(entity.isSuccess(), entity.errcode);
            }

            @Override
            public String transErrorMsg(int code, String msg) {
                //领导要求客户端把server返回的英文错误转成中文。。。。。。
                String pattern = "(?:sql|system|error|server)";
                Pattern r = Pattern.compile(pattern);
                Matcher m = r.matcher(msg);
                if (m.find()) {
                    return mCtx.getString(R.string.server_error);
                } else if (TextUtils.isEmpty(msg)) {
                    return mCtx.getString(R.string.server_error);
                }
                return msg;
            }

            @Override
            public void onResponseError(int code, String msg, Object response) {
                if (code == 101 || code == 102 || code == 401 || code == 20004) {
                    //重新登录
                    EventMsg eventMsg = new EventMsg(EventType.LOGIN_SESSION_OUT);
                    eventMsg.arg1 = code;
                    eventMsg.obj = msg;
                    EventBusUtils.post(eventMsg);
                }
            }

            @Override
            public HashMap<String, String> getQueryParam() {
                HashMap<String, String> map = new HashMap<>();
                //通用参数
                map.put("sys_vc", String.valueOf(Build.VERSION.SDK_INT));
                map.put("sys_p", "a");
                map.put("sys_m", Build.MODEL);
                map.put("sys_v", Build.VERSION.RELEASE);
                map.put("sys_pkg", "app");
                map.put("sys_d", getDeviceId()); //clientId
                map.put("cli_c", getAppChannel());
                map.put("cli_v", BuildConfig.VERSION_NAME);
                map.put("sys_dc", getDeviceId());
                map.put("x_platform", AppClient.clientName);
                map.put("mplatform", AppClient.clientName);//解决文件上传
                LoginInfo loginInfo = AccountUtil.INSTANCE.getLoginInfo();
                if (null != loginInfo) {
                    map.put("sess", loginInfo.getSessionId());
                }
                return map;
            }

            @Override
            public HashMap<String, String> getHeaderParam() {
                HashMap<String, String> map = new HashMap<>();
                //通用参数
                map.put("X-Analyse-Osv", String.valueOf(Build.VERSION.SDK_INT));
                map.put("X-Analyse-Sp", "a");
                map.put("X-Analyse-Sm", Build.MODEL);
                map.put("X-Analyse-Sv", Build.VERSION.RELEASE);
                map.put("X-Analyse-Spk", "app");
                map.put("X-Analyse-Cd", getDeviceId());//clientId
                map.put("X-Analyse-C", getAppChannel());
                map.put("X-Analyse-Av", BuildConfig.VERSION_NAME);
                map.put("X-Analyse-Cd-Const", getDeviceId());
                map.put("X-Sess-Xp", AppClient.clientName);
                LoginInfo loginInfo = AccountUtil.INSTANCE.getLoginInfo();
                if (null != loginInfo) {
                    map.put("sessName", loginInfo.getSessionId());
                }
                return map;
            }

            @Override
            public List<Interceptor> getInterceptor() {
                List<Interceptor> list = new ArrayList<>();
                if (isDebug()) {
                    list.add(new OkHttpLogInterceptor(true));
                }
                if (BuildConfig.logReportEnabled) {
                    list.add(new LogReportInterceptor(this));
                }
                return list;
            }

            @Override
            public List<Interceptor> getNetworkInterceptor() {
                List<Interceptor> list = new ArrayList<>();
                if (BuildConfig.logReportEnabled) {
                    list.add(new LogPerformanceInterceptor());
                }
                return list;
            }

            @Override
            public boolean isNetworkUnconnected() {
                return !NetworkUtils.isConnected();
            }

            @Override
            public void showToast(String msg) {
                MedToastUtil.showMessage(msg);
            }

            @Override
            public String getPage() {
                Activity activity = ActivityStashManager.getCurrentActivity();
                return activity != null ? activity.getComponentName().getClassName() : "";
            }
        });
    }

    private String getDeviceId() {
        return MedDeviceUtil.getDeviceId(mCtx);
    }
}
