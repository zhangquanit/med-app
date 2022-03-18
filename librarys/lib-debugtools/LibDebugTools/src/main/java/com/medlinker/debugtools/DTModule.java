package com.medlinker.debugtools;

import android.app.Application;

import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.medlinker.debugtools.config.IDTCustomConfig;
import com.medlinker.debugtools.fun.lane.DTLaneKit;
import com.medlinker.debugtools.fun.capture.DTNetworkCaptureKit;
import com.medlinker.debugtools.fun.router.DTRouterKit;
import com.medlinker.debugtools.log.DTLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/2 9:38 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class DTModule {

    private static final String TAG = "ModuleInit";

    public static Application app() {
        return mApplication;
    }

    private static Application mApplication;
    private static String mProductId;

    public static void init(Application application) {
        init(application,"");
    }

    public static void init(Application application, IDTCustomConfig dtConfig) {
        init(application,"",dtConfig);
    }

    /**
     *
     * @param application
     * @param productId 可以在滴滴dokit平台申请
     */
    public static void init(Application application,String productId) {
       init(application,productId,null);
    }

    public static void init(Application application, String productId, IDTCustomConfig dtConfig) {
        DTLog.d(TAG, "init");
        Objects.requireNonNull(application);
        mProductId = productId;
        mApplication = application;
        initDTConfig(dtConfig);
    }

    private static void initDTConfig(IDTCustomConfig dtConfig){
        DoraemonKit.install(app(), getKits(dtConfig), mProductId);
    }

    private static List<AbstractKit> getKits(IDTCustomConfig dtConfig) {
        List<AbstractKit> kits = new ArrayList<>();
        // 初始化默认kit
        initDefaultKit(kits);
        // 添加自定义kit
        if (null != dtConfig && null != dtConfig.customKit()){
            kits.addAll(dtConfig.customKit());
        }
        return kits;
    }

    private static void initDefaultKit(List<AbstractKit> kits) {
        kits.add(new DTLaneKit());
        kits.add(new DTNetworkCaptureKit(mProductId));
        kits.add(new DTRouterKit());
    }
}
