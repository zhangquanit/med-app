package net.medlinker.im.router;

import android.app.Application;
import android.content.Context;

import androidx.annotation.DrawableRes;


/**
 * @author hmy
 * @time 2020/9/22 13:58
 */
public interface ModuleIMService {

    String getCurrentNetStateCodeStr(Context context);

    Application getApplication();

    long getCurrentUserId();

    String getOrigSession();

    boolean isVisitor();

    boolean isAppForeground();

    String getAppName();

    @DrawableRes
    int getNotificationLollipopSmallIcon();

    @DrawableRes
    int getNotificationSmallIcon();

    Class<?> getStartAppActivity();

    void dealOffline(String msg);

    void onMsgUnreadCountChanged();
}
