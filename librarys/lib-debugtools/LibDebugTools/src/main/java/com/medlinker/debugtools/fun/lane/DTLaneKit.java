package com.medlinker.debugtools.fun.lane;

import android.content.Context;
import android.content.Intent;

import com.didichuxing.doraemonkit.aop.OkHttpHook;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.medlinker.debugtools.R;
import com.medlinker.debugtools.fun.lane.DTEnvLaneActivity;
import com.medlinker.debugtools.fun.lane.DTLaneUrlInterceptor;

/**
 * @author: pengdaosong
 * @CreateTime: 2021/3/3 5:30 PM
 * @Email: pengdaosong@medlinker.com
 * @Description: 泳道设置
 */
public class DTLaneKit extends AbstractKit {

    private final DTLaneUrlInterceptor mInterceptor;

    public DTLaneKit(){
        mInterceptor = new DTLaneUrlInterceptor();
    }

    @Override
    public int getIcon() {
        return R.mipmap.lane;
    }

    @Override
    public int getName() {
        return R.string.lane;
    }

    @Override
    public void onAppInit(Context context) {
        OkHttpHook.globalInterceptors.add(mInterceptor);
    }

    @Override
    public void onClick(Context context) {
        Intent intent = new Intent(context, DTEnvLaneActivity.class);
        context.startActivity(intent);
    }
}
