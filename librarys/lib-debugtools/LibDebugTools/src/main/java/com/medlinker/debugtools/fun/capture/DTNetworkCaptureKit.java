package com.medlinker.debugtools.fun.capture;

import android.content.Context;
import android.content.Intent;

import com.didichuxing.doraemonkit.aop.OkHttpHook;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.didichuxing.doraemonkit.kit.Category;
import com.medlinker.debugtools.R;
import com.medlinker.debugtools.fun.capture.DTNetworkCaptureInterceptor;
import com.medlinker.debugtools.fun.capture.DTNetworkCaptureActivity;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/4 2:54 PM
 * @Email: pengdaosong@medlinker.com
 * @Description: 抓包
 */
public class DTNetworkCaptureKit extends AbstractKit {

    private DTNetworkCaptureInterceptor mInterceptor;

    public DTNetworkCaptureKit(String kitId) {
        mInterceptor = new DTNetworkCaptureInterceptor();
    }

    @Override
    public int getCategory() {
        return Category.BIZ;
    }

    @Override
    public int getIcon() {
        return R.mipmap.api;
    }

    @Override
    public int getName() {
        return R.string.med_api_name;
    }

    @Override
    public String innerKitId() {
        return "med_api";
    }

    @Override
    public void onAppInit(Context context) {
        OkHttpHook.globalInterceptors.add(mInterceptor);
    }

    @Override
    public void onClick(Context context) {
        Intent intent = new Intent(context, DTNetworkCaptureActivity.class);
        context.startActivity(intent);
    }
}
