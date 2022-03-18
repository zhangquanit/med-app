package com.medlinker.router;

import android.content.Context;

import com.medlinker.router.router.BaseMedRouterMapping;

/**
 * @author hmy
 * @time 2021/1/20 14:15
 */
public abstract class Config {
    public abstract void launchReactActivity(MedRouterHelper.MedRouter medRouter, Context context,
                                             String rnModuleName, String rnRouterName, String extra, int requestCode);

    public abstract void onLostPage(Context context);

    public BaseMedRouterMapping checkLogin(MedRouterHelper.MedRouter medRouter) {
        return null;
    }
}
