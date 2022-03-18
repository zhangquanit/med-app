package com.medlinker.common.router.module;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.medlinker.base.common.CommonCallBack;

public interface ThirdIMManagerService extends IProvider {
    void init();

    void checkInit();

    void checkLoginIm();

    void autoLoginIm();

    void unInit();

    boolean isLoginIM();

    void logoutIM(CommonCallBack<Boolean> callBack);

    void loginIM(long userId, final CommonCallBack<Boolean> callBack);
}
