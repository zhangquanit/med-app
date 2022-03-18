package com.medlinker.router.router;

import android.content.Context;

import com.medlinker.router.Config;
import com.medlinker.router.MedRouterHelper;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 6.0
 * @description 如果mapping里面没有找到的情况下
 * @time 2018/2/7
 */
public class LostPageRouter extends BaseMedRouterMapping {

    public LostPageRouter(MedRouterHelper.MedRouter medRouter) {
        super(medRouter);
    }

    @Override
    public String getRouterKey() {
        return null;
    }

    @Override
    public Class getTargetClass() {
        return null;
    }

    public void navigation(Context context) {
        onLostPage(context);
    }

    public void navigation(Context context, int requestCode) {
        onLostPage(context);
    }

    private void onLostPage(Context context) {
        Config config = MedRouterHelper.getConfig();
        if (null != config) {
            config.onLostPage(context);
        }
    }
}
