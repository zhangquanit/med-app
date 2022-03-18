package com.medlinker.base.router.module;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * 路由服务
 *
 * @author zhangquan
 */
public interface RouterService extends IProvider {
    /**
     * 跳转到RN页面
     *
     * @param moduleName
     * @param routeName
     * @param extraJson
     */
    void jumpToRN(String moduleName, String routeName, @Nullable String extraJson);
}
