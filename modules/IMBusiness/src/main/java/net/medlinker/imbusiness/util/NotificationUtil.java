package net.medlinker.imbusiness.util;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;

import com.blankj.utilcode.util.AppUtils;
import com.medlinker.lib.utils.MedResourceUtils;

import net.medlinker.im.router.ModuleIMManager;
import net.medlinker.imbusiness.R;
import net.medlinker.imbusiness.router.ModuleIMBusinessManager;


/**
 * 消息通知
 *
 * @author hmy
 */
public class NotificationUtil {
    public static void createNotification(String title, String content, String jumpRouter, String tag, int id) {
        createNotification(title, content, null, jumpRouter, tag, id);
    }

    public static void createNotification(String title, String content, Intent intent, String tag, int id) {
        createNotification(title, content, intent, null, tag, id);
    }

    public static void createNotification(String title, String content, Intent intent, String jumpRouter, String tag, int id) {
        if (intent == null && TextUtils.isEmpty(jumpRouter)) {
            return;
        }
        Application application = ModuleIMBusinessManager.INSTANCE.getApplication();
        NotificationManager notificationManager = (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }
        Notification.Builder notification = new Notification.Builder(application);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(AppUtils.getAppName(), title, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setShowBadge(true);
            notificationManager.createNotificationChannel(channel);
            notification.setChannelId(MedResourceUtils.getString(R.string.app_name));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification.setSmallIcon(R.mipmap.ic_lollipop_notify);
        } else {
            notification.setSmallIcon(AppUtils.getAppIconId());
        }
        notification.setContentTitle(title);
        notification.setContentText(content);
        notification.setAutoCancel(true);
//        notification.setNumber(entity.getUnreadMsgCount());手机兼容屏蔽掉
        notification.setDefaults(Notification.DEFAULT_ALL);
        if (intent == null) {
            intent = new Intent(application, ModuleIMManager.INSTANCE.getIMService().getStartAppActivity());
            intent.setData(Uri.parse(jumpRouter));
        }
        PendingIntent contentIntent = PendingIntent.getActivity(application, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(contentIntent);
        notificationManager.notify(tag, id, notification.build());
    }

    /**
     * 用于保活的通知
     *
     * @param title
     * @param content
     * @param intent
     * @param tag
     * @param id
     */
    public static Notification createKeepAliveNotification(String title, String content, Intent intent, String tag, int id) {
        if (intent == null) {
            return null;
        }
        Application application = ModuleIMBusinessManager.INSTANCE.getApplication();
        NotificationManager notificationManager = (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return null;
        }
        Notification.Builder builder = new Notification.Builder(application);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(MedResourceUtils.getString(R.string.app_name),
                    title, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setShowBadge(true);
            notificationManager.createNotificationChannel(channel);
            builder.setChannelId(MedResourceUtils.getString(R.string.app_name));
        }
        builder.setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.mipmap.ic_lollipop_notify);
        } else {
            builder.setSmallIcon(AppUtils.getAppIconId());
        }
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setPriority(Notification.PRIORITY_HIGH);   //优先级高
        PendingIntent contentIntent = PendingIntent.getActivity(application,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT; //将此通知放到通知栏的"Ongoing"即"正在运行"组中
        notification.flags |= Notification.FLAG_NO_CLEAR; //表明在点击了通知栏中的"清除通知"后，此通知不清除，常与FLAG_ONGOING_EVENT一起使用

        notificationManager.notify(tag, id, notification);
        return notification;
    }
}
