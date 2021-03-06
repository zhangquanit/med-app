package com.medlinker.lib.push.med;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.google.gson.Gson;
import com.medlinker.lib.utils.MedAppInfo;

import net.medlinker.base.account.AccountUtil;
import net.medlinker.base.account.UserInfo;
import net.medlinker.libhttp.host.HostManager;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PushMsgProcessor {
    private static PushMsgProcessor mInstance;
    private Call mCall;
    private Handler mHandler;
    private PushMsgReceiver mMsgReceiver;
    private int mSource;
    public Class<? extends Activity> mIntentCls;

    private PushMsgProcessor() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static PushMsgProcessor getInstance() {
        if (mInstance == null) {
            mInstance = new PushMsgProcessor();
        }
        return mInstance;
    }

    public void setOptions(PushOptions options) {
        mSource = options.getSource();
        mIntentCls = options.getIntentCls();
        mMsgReceiver = options.getMsgReceiver();
    }

    public void setMsgReceiver(PushMsgReceiver receiver) {
        mMsgReceiver = receiver;
    }

    public void setSource(int source) {
        mSource = source;
    }

    public void setIntentCls(Class<? extends Activity> cls) {
        mIntentCls = cls;
    }

    @SuppressLint("CheckResult")
    public void bindClientId(String clientId) {
        UserInfo userInfo = AccountUtil.INSTANCE.getUserInfo();
        PushLog.log("????????????clientId,????????????????????????-" + (null != userInfo ? "???" : "???"));
        if (null == userInfo) return;
        HashMap map = new HashMap<String, Object>();
        map.put("deviceNum", clientId);
        map.put("source", mSource); //6????????? 7??????
//        map.put("pushType", "getui"); // push?????????(???getui,mi,hw,flyme)
        map.put("platform", "a");//??????(a??????,i??????)

        if (null != mCall && !mCall.isCanceled()) {
            mCall.cancel();
        }

        mCall = HostManager.INSTANCE.getApi(PushMsgApi.class).bindClientId(map);
        mCall.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                PushLog.log("??????clientId??????");
            }

            @Override
            public void onFailure(Call call, Throwable throwable) {
                PushLog.log("??????clientId?????? e=" + throwable.getMessage());
            }
        });
    }

    public void onReceiveClientId(String clientId) {
        if (TextUtils.isEmpty(clientId)) return;
        mHandler.post(() -> bindClientId(clientId));
    }

    public void onReceiveMsg(String msg) {
        if (TextUtils.isEmpty(msg)) return;
        mHandler.post(() -> {
            try {
                PushMsgReceiver msgReceiver = mMsgReceiver;
                if (null != msgReceiver && msgReceiver.onReceiveMsg(msg)) {
                    return;
                }
                PushMsgEntity notice = new Gson().fromJson(msg, PushMsgEntity.class);
                showNotificationPush(MedAppInfo.getAppContext(), notice);
            } catch (Exception e) {
            }
        });
    }

    /**
     * ?????????????????????
     */
    private void showNotificationPush(Context context, PushMsgEntity notice) {
        UserInfo userInfo = AccountUtil.INSTANCE.getUserInfo();
        PushLog.log("????????????,????????????????????????-" + (null != userInfo ? "???" : "???"));
        if (null == userInfo) return;
        long userId = userInfo.getId();
        //???????????????????????????
        long targetUserId = notice.targetUserId;
        if (targetUserId != 0 && targetUserId != userId) {
            return;
        }

        try {
            Class cls = mIntentCls;
            if (null == cls) {
                String launchActivity = ActivityUtils.getLauncherActivity();
                cls = Class.forName(launchActivity);
            }
            Intent mIntent = new Intent(context, cls);
            mIntent.setData(Uri.parse(notice.extra == null ? "/" : notice.extra));

            notify(context, notice.title, notice.content, mIntent);
        } catch (Exception e) {

        }
    }

    private void notify(Context context, String title, String content, Intent intent) {
        final String channelId = AppUtils.getAppName();
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            int iconId = ResourceUtils.getDrawableIdByName("push_small");
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId).setContentTitle(title).setContentText(content);
            builder.setSmallIcon(iconId);
            builder.setAutoCancel(true);
            int requestCode = (int) SystemClock.uptimeMillis();
            PendingIntent updateIntent = PendingIntent.getActivity(context, requestCode,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(updateIntent);
            Notification notification = builder.build();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, title, NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(true); //???????????????icon????????????????????????
                channel.setLightColor(Color.RED); //???????????????
                channel.setShowBadge(true); //??????????????????????????????????????????????????????
                channel.enableVibration(true);
                manager.createNotificationChannel(channel);
            } else {
                notification.defaults |= Notification.DEFAULT_SOUND;
                notification.defaults |= Notification.DEFAULT_VIBRATE;
            }

            final int id = (int) (System.currentTimeMillis() / 1000);
            manager.notify(id, notification);
            PushLog.log(" send a notification  id = " + id);
        }
    }

}
