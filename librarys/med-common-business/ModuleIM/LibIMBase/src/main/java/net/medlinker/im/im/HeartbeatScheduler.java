package net.medlinker.im.im;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description
 * @time 2017/4/1
 */
public abstract class HeartbeatScheduler {

    public static final int TIMEOUT = 20;

    protected int minHeart = 60;

    protected int maxHeart = 300;

    protected volatile boolean started = false;

    protected volatile long heartbeatSuccessTime;

    protected volatile int currentHeartType;

    public static final String HEART_TYPE_TAG = "heart_type";

    public static final String HEART_BEAT_ACTION = "heart_beat";

    public static final int UNKNOWN_HEART = 0, SHORT_HEART = 1, PROBE_HEART = 2, STABLE_HEART = 3, REDUNDANCY_HEART = 4;

    protected PendingIntent createPendingIntent(Context context, int requestCode, int heartType) {
        Intent intent = new Intent(context, MedImReceiver.class);
        intent.setPackage(context.getPackageName());
        intent.setAction(HEART_BEAT_ACTION);
        intent.putExtra(HEART_TYPE_TAG, heartType);
        return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public abstract void start(Context context);

    public abstract void stop(Context context);

    public abstract void clear(Context context);

    protected abstract void adjustHeart(Context context, boolean success);

    public abstract void receiveHeartbeatFailed(Context context);

    public abstract void receiveHeartbeatSuccess(Context context);

}
