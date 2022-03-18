package net.medlinker.router;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.medlinker.router.Config;
import com.medlinker.router.MedRouterHelper;
import com.medlinker.router.router.BaseMedRouterMapping;

import net.medlinker.router.router.Page1Router;
import net.medlinker.router.router.RoutePath;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangquan
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ARouter.openLog();     // 打印日志
        ARouter.openDebug();
        ARouter.init(this);

        //手动注册
        Map<String, Class<? extends BaseMedRouterMapping>> routerMap = new HashMap<>();
        routerMap.put(RoutePath.MED_PATH1, Page1Router.class);

        MedRouterHelper.init(routerMap, new Config() {
            @Override
            public void launchReactActivity(MedRouterHelper.MedRouter medRouter, Context context, String rnModuleName, String rnRouterName, String extra, int requestCode) {

            }

            @Override
            public void onLostPage(Context context) {

            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
