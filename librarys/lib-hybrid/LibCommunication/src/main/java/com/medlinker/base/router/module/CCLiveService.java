package com.medlinker.base.router.module;

import android.content.Context;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * 场景直播服务
 *
 * @author zhangquan
 */
public interface CCLiveService extends IProvider {
    /**
     * 获取手机号
     */
    String getPhoneNo();

    /**
     * 获取api
     */
    <T> T getDoctorAppApi(Class<T> api);

    /**
     * Router路由页面
     */
    void startRouterPage(Context context,String url);

    /**
     * 加载圆角图片
     */
    void loadRoundImage(Context context, String url, ImageView imageView, int radius);
}
