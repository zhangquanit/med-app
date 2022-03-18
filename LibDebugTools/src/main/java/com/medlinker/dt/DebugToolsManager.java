package com.medlinker.dt;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.medlinker.baseapp.EventType;
import com.medlinker.baseapp.route.RoutePath;
import com.medlinker.debugtools.DTModule;
import com.medlinker.debugtools.config.DTConfig;
import com.medlinker.debugtools.config.DTLaneConfig;
import com.medlinker.debugtools.config.DTNetworkCaptureConfig;
import com.medlinker.debugtools.config.DTRouterConfig;
import com.medlinker.debugtools.config.IDTCustomConfig;
import com.medlinker.debugtools.fun.lane.DTLaneStorage;
import com.medlinker.dt.dokit.MockAppKit;

import net.medlinker.base.event.EventBusUtils;
import net.medlinker.base.event.EventMsg;
import net.medlinker.base.storage.KVUtil;
import net.medlinker.libhttp.host.Host;
import net.medlinker.libhttp.host.HostManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DebugToolsManager {

    private static final String TAG = "DebugToolsManager";
    private static final String AUTHORIZATION = "Basic bWVkbGlua2VyOm1lZDEyMzQ1Ng==";//请求泳道列表携带的header
    private static Application mApplication;

    private static List<HostMapping> sHostList = new ArrayList<>();
    private static List<String> sDefaultQADomains = new ArrayList<>();
    private static Map<String, String> sDefaultOnlineDomains = new HashMap<>();
    private static Map<String, String> sDefaultPreDomains = new HashMap<>();
    private static List<String> sNotCaptureDomains = new ArrayList<>();


    private static IDTCustomConfig CUSTOM_CONFIG = () -> {
        List<AbstractKit> list = new ArrayList<>();
        list.add(new MockAppKit());
        return list;
    };

    public static void initDT(Application application) {
        Log.d(TAG, "dt init start");
        mApplication = application;
        changeUrlType(getEnvType());
        DTModule.init(application, CUSTOM_CONFIG);
        initLaneConfig(application);
        initGlobalOkHttpInterceptors();
        Toast.makeText(application, "DebugTools初始化成功", Toast.LENGTH_LONG).show();
        DTLaneStorage.LaneData landData = DTLaneStorage.getLaneData();
        if (landData != null) {
            Log.d(TAG, "name=" + landData.getLaneName() + "，domains = " + landData.getLaneDomains());
        }
        Log.d(TAG, "dt init end");
    }

    private static void initLaneConfig(Application application) {
        initHost();
        initNotCaptureDomains();
        DTConfig.instance()
                .initLaneConfig(new DTLaneConfig()
                        .authorization(AUTHORIZATION)
                        .laneAuthority(sDefaultQADomains) //所有需要替换的QA域名
                        .onlineDomains(sDefaultOnlineDomains)
                        .preDomains(sDefaultPreDomains)
                        .callBack(laneData -> doLaneConfigSuccess(laneData)))
                .initRouterConfig(new DTRouterConfig().routerConfig(s -> doRouter(s))) //使用MedRouterHelper跳转  跳转失败回调
                .initNetworkCaptureConfig(new DTNetworkCaptureConfig().setNotCaptureDomains(sNotCaptureDomains));//白名单，不拦截
    }

    private static void initGlobalOkHttpInterceptors() {

    }

    /**
     * 切换泳道成功回调
     * 只有泳道才会切换域名，qa/pre/online不会切换
     *
     * @param laneData
     */
    private static void doLaneConfigSuccess(DTLaneStorage.LaneData laneData) {
        int type = 4;
        String laneName = DTLaneConfig.QA;
        if (null != laneData) {
            if (DTLaneConfig.ONLINE.equals(laneData.getLaneName())) {
                type = 3;
            } else if (DTLaneConfig.PRE.equals(laneData.getLaneName())) {
                type = 5;
            }
            laneName = laneData.getLaneName();
            Log.d(TAG, "name=" + laneName + "，domains = " + laneData.getLaneDomains());
        }

        Log.d(TAG, "env type=" + type);
        Log.d(TAG, "lane name=" + laneName);

        int preEnvType = getEnvType();
        KVUtil.set("medlinker_env_type", type);
        KVUtil.set("medlinker_env_lane", laneName);
        changeUrlType(type);
        HostManager.INSTANCE.clear();//

        String loginTip = null;
        if (preEnvType == 3 && (type == 4 || type == 5)) { //线上切换到QA或预发
            loginTip = "线上切换到测试，需要重新登录";
        } else if (type == 3) { //切换到线上
            loginTip = "切换到正式环境，需要重新登录";
        }
//        if (!TextUtils.isEmpty(loginTip)) {
//        EventBusUtils.post(new EventMsg(EventType.LOGIN_SESSION_OUT, loginTip));
//    }

        boolean hasMainPage = false;
        List<Activity> activityList = ActivityUtils.getActivityList();
        for (Activity item : activityList) {
            String clsName = item.getClass().getName();
            String clsPkg = clsName.substring(0, clsName.lastIndexOf("."));
            if (TextUtils.equals(item.getPackageName(), clsPkg) && item.getClass().getSimpleName().startsWith("Main")) {
                hasMainPage = true;
                break;
            }
        }
        EventBusUtils.post(new EventMsg(EventType.LOGIN_SESSION_OUT, loginTip));
        if (!hasMainPage) { //没有打开过主页面
            ToastUtils.showShort("请重新登录");
            clearAccountCache();
            RoutePath.INSTANCE.startLoginActivity();
        }

    }

    private static void clearAccountCache() {
        try {
            Class<?> c = Class.forName("net.medlinker.base.account.AccountUtil");
            Method method = c.getDeclaredMethod("clear");
            method.setAccessible(true);
            method.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void changeUrlType(int type) {
        Log.d(TAG, "changeUrlType  type=" + type);
        try {
            Class<?> c = Class.forName("com.medlinker.lib.utils.MedAppInfo");
            Field f = c.getDeclaredField("envType");
            f.setAccessible(true);
            f.setInt(null, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getH5Host() {
        int envType = getEnvType();
        if (envType == 3) {
            return "https://web.medlinker.com";
        }
        if (envType == 5) {
            return "https://web-pre.medlinker.com";
        }
        DTLaneStorage.LaneData landData = DTLaneStorage.getLaneData();
        if (landData != null) {
            Log.d(TAG, "getH5Host()");
            Log.d(TAG, "name=" + landData.getLaneName() + "，domains = " + landData.getLaneDomains());
            if (!TextUtils.isEmpty(landData.getLaneName())) {
                return String.format("https://web-%s.medlinker.com", landData.getLaneName());
            }
        }
        return "http://web-qa.medlinker.com";
    }

    public static int getEnvType() {
        return KVUtil.getInt("medlinker_env_type", 4);
    }

    private static void doRouter(String router) {
        Log.d(TAG, "url = " + router);
        try {
            Class clazz = Class.forName("com.medlinker.router.MedRouterHelper");
            Method method = clazz.getMethod("navigation", String.class, Context.class);
            method.setAccessible(true);
            method.invoke(clazz, router, mApplication);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initHost() {
        Map<String, Host> map = HostManager.INSTANCE.getHosts();
        for (Map.Entry<String, Host> entry : map.entrySet()) {
            Host host = entry.getValue();
            HostMapping hostMapping = new HostMapping(parseHost(host.getMQa()), "@" + parseHost(host.getMOnLine()), "@" + parseHost(host.getMPre()));
            sHostList.add(hostMapping);
        }

        for (HostMapping host : sHostList) {
            //QA
            sDefaultQADomains.add(host.qaHost);
            //Online
            sDefaultOnlineDomains.put(host.qaHost, host.onlineHost);
            //Pre
            sDefaultPreDomains.put(host.qaHost, host.preHost);
        }
    }

    private static String parseHost(String host) {
        return Uri.parse(host).getHost();
    }

    /**
     * 设置不拦截的域名
     * HttpUrlConnection请求通过HttpUrlConnectionProxyUtil拦截
     * OKhttp请求通过OkHttpHook添加拦截器，最终通过在OKhttpClient中插入拦截器实现
     */
    private static void initNotCaptureDomains() {
        //第三方
        sNotCaptureDomains.add("api.growingio.com");
        sNotCaptureDomains.add("tags.growingio.com");
        sNotCaptureDomains.add("t.growingio.com");
        sNotCaptureDomains.add("android.bugly.qq.com");
        sNotCaptureDomains.add("up-z0.qiniu.com");
        sNotCaptureDomains.add("upload.qiniup.com");
        sNotCaptureDomains.add("amap.com");
        sNotCaptureDomains.add("47.110.164.227");
        sNotCaptureDomains.add("up-z0.qiniu.com");
        sNotCaptureDomains.add("b-gtc.gepush.com");
        sNotCaptureDomains.add("errlog.umeng.com");
        sNotCaptureDomains.add("ulogs.umengcloud.com");
        sNotCaptureDomains.add("ulogs.umeng.com");
        //业务
        sNotCaptureDomains.add("disease-resource-qa.medlinker.com");
        sNotCaptureDomains.add("disease-resource.medlinker.com");
        sNotCaptureDomains.add("casem-file.qa.medlinker.com");
        sNotCaptureDomains.add("pub-med-casem.medlinker.com");


        String[] hosts = sNotCaptureDomains.toArray(new String[sNotCaptureDomains.size()]);
        try {
            Class<?> c = Class.forName("com.didichuxing.doraemonkit.aop.urlconnection.HttpUrlConnectionProxyUtil");
            Field f = c.getDeclaredField("hosts");
            f.setAccessible(true);
            f.set(null, hosts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class HostMapping {
        private String qaHost;
        private String onlineHost;
        private String preHost;

        HostMapping(String qaHost, String onlineHost, String preHost) {
            this.qaHost = qaHost;
            this.onlineHost = onlineHost;
            this.preHost = preHost;
        }

        @Override
        public String toString() {
            return "HostMapping{" +
                    "qaHost='" + qaHost + '\'' +
                    ", onlineHost='" + onlineHost + '\'' +
                    ", preHost='" + preHost + '\'' +
                    '}';
        }
    }
}
