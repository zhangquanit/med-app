package net.medlinker.im.realm;

import android.app.Application;
import android.content.Context;

import net.medlinker.im.router.ModuleIMManager;
import net.medlinker.im.router.ModuleRealmService;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmHelper {

    /**
     * realm是否初始化
     */
    public static boolean isInit = false;

    /**
     * 初始化realm
     */
    public static void init(Context context) {
        if (!(context instanceof Application)) {
            context = context.getApplicationContext();
        }
        Realm.init(context);
        isInit = true;
    }

    /**
     * 更新realm配置
     */
    public static void updateConfig() {
        ModuleRealmService realmService = ModuleIMManager.INSTANCE.getRealmService();
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(realmService.getRealmName())
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(realmService.getSchemaVersion())
                .modules(new RealmIMModule(), realmService.getRealmModule())
                .build();
        Realm.setDefaultConfiguration(config);
    }

}
