package com.medlinker.video.utils;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import com.medlinker.video.VideoCallActivity;
import com.medlinker.video.VideoCallManager;
import com.medlinker.video.entity.VideoRoomEntity;

/**
 * @author hmy
 */
public class NotificationUtil {

    public static final int NOTIFICATION_FLAG = 20190926;

    public static Notification createCallKeepAliveNotification(Application application, VideoRoomEntity entity) {
        Intent intent = new Intent(application, VideoCallActivity.class);
        return VideoCallManager.INSTANCE.getModuleService()
                .createKeepAliveNotification(entity.getUserInfo(), "视频通话中，轻击以继续",
                        intent, null, NOTIFICATION_FLAG);
    }

    public static void cancelCallKeepAliveNotification(Application application) {
        NotificationManager notificationManager = (NotificationManager) application
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.cancel(NOTIFICATION_FLAG);
        }
    }
}
