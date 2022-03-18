package com.medlinker.base.router.module;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * 埋点路由
 *
 * @author hmy
 */
public interface TrackManagerService extends IProvider {

    void onPausePage(String pageName);

    void onResumePage(String pageName);

    void onEvent(String action);

    void setFrom(String from);

    void onJump(String from, String to);

    void onJump(String to);

    void onJumpPage(String from, String to);
}
