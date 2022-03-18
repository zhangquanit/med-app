package com.medlinker.debugtools.fun.router;

import android.content.Context;
import android.content.Intent;
import com.didichuxing.doraemonkit.kit.AbstractKit;
import com.medlinker.debugtools.R;
import com.medlinker.debugtools.fun.router.DTRouterActivity;

/**
 * 内部路由跳转
 */
public class DTRouterKit extends AbstractKit {

    public DTRouterKit() { }

    @Override
    public int getIcon() {
        return R.mipmap.router;
    }

    @Override
    public int getName() {
        return R.string.arouter;
    }

    @Override
    public void onAppInit(Context context) {

    }

    @Override
    public void onClick(Context context) {
        Intent intent = new Intent(context, DTRouterActivity.class);
        context.startActivity(intent);
    }
}
