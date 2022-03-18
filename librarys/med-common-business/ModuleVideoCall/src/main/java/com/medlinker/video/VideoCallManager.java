package com.medlinker.video;

import android.app.Notification;
import android.content.Intent;

/**
 * @author hmy
 * @time 11/22/21 13:13
 */
public enum VideoCallManager {

    INSTANCE;
    private IModuleService mIModuleService;

    public void setModuleService(IModuleService service) {
        mIModuleService = service;
    }

    public IModuleService getModuleService() {
        return mIModuleService;
    }

    public interface IModuleService {

        Notification createKeepAliveNotification(String title, String content, Intent intent, String tag, int id);

        void onVideoCallFinished();
    }
}
