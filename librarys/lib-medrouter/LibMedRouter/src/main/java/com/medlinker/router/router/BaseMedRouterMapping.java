package com.medlinker.router.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.medlinker.router.MedRouterHelper;
import com.medlinker.router.router.ext.RouteRecorder;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 6.0
 * @description
 * @time 2018/2/6
 */
public abstract class BaseMedRouterMapping extends Activity{

    public final MedRouterHelper.MedRouter mMedRouter;
    protected final Intent mIntent = new Intent();

    public BaseMedRouterMapping(MedRouterHelper.MedRouter medRouter) {
        this.mMedRouter = medRouter;
    }

    public abstract String getRouterKey();

    public abstract Class getTargetClass();

    protected boolean needInterrupt(Context context, int requestCode) {
        return false;
    }

    /**
     * 装数据
     *
     * @param intent
     */
    protected void packageDataInIntent(Intent intent) {
    }

    /**
     * 如果有直接使用intent自己跳转的地方，注意拦截过后的地方，一般class会返回null,如果抛出android.content.ActivityNotFoundException
     * 请在跳转的地方加一个getcompent==null判断使用navigation方法跳转。
     *
     * @param context
     * @return
     */
    public Intent getIntent(Context context) {
        if (getTargetClass() != null) {
            mIntent.setClass(context, getTargetClass());
            RouteRecorder.add(mMedRouter.getSourceUrl(), getTargetClass());
        }
        mIntent.putExtras(new Bundle());
        packageDataInIntent(mIntent);
        if (!(context instanceof Activity)) {
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return mIntent;
    }
}
