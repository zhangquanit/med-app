package com.medlinker.common.router.module;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.medlinker.base.common.CommonCallBack;

/**
 * @author hmy
 */
public interface SessionTIMManagerService extends IProvider {

    boolean haveUnread();

    void loadSession();

    void addTIMMsgUnreadListener(CommonCallBack<Integer> callBack);
}
