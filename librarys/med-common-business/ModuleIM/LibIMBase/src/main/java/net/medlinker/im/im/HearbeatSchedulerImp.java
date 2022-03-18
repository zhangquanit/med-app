package net.medlinker.im.im;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import net.medlinker.im.router.ModuleIMManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description
 * @time 2017/4/1
 */
public class HearbeatSchedulerImp extends HeartbeatScheduler {

    private static final String TAG = HearbeatSchedulerImp.class.getName();


    private class Heartbeat {

        AtomicInteger heartbeatStabledSuccessCount = new AtomicInteger(0); // 心跳连续成功次数

        AtomicInteger heartbeatFailedCount = new AtomicInteger(0); // 心跳连续失败次数

        int successHeart;

        int failedHeart;

        int curHeart = 180;

        AtomicBoolean stabled = new AtomicBoolean(false);

    }

    private int curMaxHeart = maxHeart;

    private int curMinHeart = minHeart;

    private int maxFailedCount = 5;

    private int maxSuccessCount = 10;

    private volatile String networkTag;

    private int requestCode = 700;

    private Map<String, Heartbeat> heartbeatMap = new HashMap<>();

    private List<Integer> successHeartList = new CopyOnWriteArrayList<>();

    public HearbeatSchedulerImp() {

    }

    @Override
    public void start(Context context) {
        started = true;
        networkTag = ModuleIMManager.INSTANCE.getIMService().getCurrentNetStateCodeStr(context);
        alarm(context);
        Log.i(TAG, "start heartbeat,networkTag:" + networkTag);
    }

    @Override
    public void stop(Context context) {
        heartbeatSuccessTime = 0;
        started = false;
        currentHeartType = UNKNOWN_HEART;
        for (Map.Entry<String, Heartbeat> entry : heartbeatMap.entrySet()) {
            Heartbeat heartbeat = entry.getValue();
            heartbeat.heartbeatStabledSuccessCount.set(0);
        }
        cancel(context);
        Log.i(TAG, " stop heartbeat...");
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void alarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Heartbeat heartbeat = getHeartbeat();
        boolean stabled = heartbeat.stabled.get();
        int heart;
        if (stabled) {
            heart = heartbeat.curHeart - 10;
            if (heart < minHeart) {
                heart = minHeart;
            }
            heart = heart * 1000;
        } else {
            heart = heartbeat.curHeart * 1000;
        }
        int heartType = stabled ? STABLE_HEART : PROBE_HEART;
        PendingIntent pendingIntent = createPendingIntent(context, requestCode, heartType);
        int sdk = Build.VERSION.SDK_INT;
        if (sdk >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + heart, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + heart, pendingIntent);
        }
        Log.i(TAG, "start heartbeat,curHeart [" + heartbeat.curHeart + "],heart [" + heart + "],requestCode:" + requestCode + ",stabled:" + stabled);
    }

    private void cancel(Context context) {
        Heartbeat heartbeat = getHeartbeat();
        int heartType = heartbeat.stabled.get() ? STABLE_HEART : PROBE_HEART;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = createPendingIntent(context, requestCode, heartType);
        alarmManager.cancel(pendingIntent);
        Log.d(TAG, "cancel heartbeat,requestCode:" + requestCode);
    }

    private void addSuccessHeart(Integer successHeart) {
        if (!successHeartList.contains(successHeart)) {
            if (successHeartList.size() > 10) {
                successHeartList.remove(0);
            }
            successHeartList.add(successHeart);
            Log.i(TAG, "add successHeart:" + successHeart);
        }
        Log.i(TAG, "successHeartList:" + successHeartList);
    }

    private void removeSuccessHeart(Integer successHeart) {
        successHeartList.remove(Integer.valueOf(successHeart));
        Log.i(TAG, "successHeartList:" + successHeartList);
    }

    @Override
    protected void adjustHeart(Context context, boolean success) {
        if (currentHeartType == REDUNDANCY_HEART) {
            Log.d(TAG, "redundancy heart,do not adjustHeart...");
            return;
        }
        Heartbeat heartbeat = getHeartbeat();
        if (success) {
            onSuccess(heartbeat);
        } else {
            onFailed(heartbeat);
        }
        Log.i(TAG, "after success is [" + success + "] adjusted,heartbeat.curHeart:" + heartbeat.curHeart + ",networkTag:" + networkTag);
    }

    private void onSuccess(Heartbeat heartbeat) {
        heartbeat.successHeart = heartbeat.curHeart;
        curMinHeart = heartbeat.curHeart;
        addSuccessHeart(heartbeat.successHeart);
        heartbeat.heartbeatFailedCount.set(0);
        if (heartbeat.stabled.get()) {
            int count = heartbeat.heartbeatStabledSuccessCount.incrementAndGet();
            Log.i(TAG, "heartbeatStabledSuccessCount:" + heartbeat.heartbeatStabledSuccessCount.get());
            if (count >= maxSuccessCount) {
                maxSuccessCount += 10;
                Log.i(TAG, "maxSuccessCount:" + maxSuccessCount);
                Integer successHeart = selectMinSuccessHeart(heartbeat.curHeart);
                if (successHeart != null) {
                    heartbeat.curHeart = successHeart;
                } else {
                    heartbeat.stabled.set(false);
                    curMaxHeart = maxHeart;
                    heartbeat.curHeart = (curMinHeart + curMaxHeart) / 2;
                    Log.i(TAG, "curHeart = (" + curMinHeart + " + " + curMaxHeart + ") / 2 = " + heartbeat.curHeart);
                }
            }
        } else {
            heartbeat.curHeart = (curMinHeart + curMaxHeart) / 2;
            Log.i(TAG, "curHeart = (" + curMinHeart + " + " + curMaxHeart + ") / 2 = " + heartbeat.curHeart);
        }
        if (heartbeat.curHeart >= maxHeart) {
            heartbeat.curHeart = maxHeart;
            heartbeat.stabled.set(true);
            Log.i(TAG, "探测达到最大心跳adjust stabled:" + heartbeat.stabled.get());
        } else if (curMaxHeart - curMinHeart < 10) {
            if (!heartbeat.stabled.get()) {
                heartbeat.curHeart = curMinHeart;
            }
            heartbeat.stabled.set(true);
            Log.i(TAG, "二分法探测尽头adjust stabled:" + heartbeat.stabled.get());
        }
        Log.i(TAG, "curHeart:" + heartbeat.curHeart + ",curMinHeart:" + curMinHeart + ",curMaxHeart:" + curMaxHeart);
    }

    private void onFailed(Heartbeat heartbeat) {
        removeSuccessHeart(heartbeat.curHeart);
        heartbeat.failedHeart = heartbeat.curHeart;
        heartbeat.heartbeatStabledSuccessCount.set(0);
        int count = heartbeat.heartbeatFailedCount.incrementAndGet();
        Log.i(TAG, "heartbeatFailedCount:" + count);
        if (maxSuccessCount > 10) {
            maxSuccessCount -= 10;
        }
        if (count > maxFailedCount) {
            curMaxHeart = heartbeat.curHeart;
        }
        if (heartbeat.stabled.get()) {
            if (count > maxFailedCount) {
                Integer successHeart = selectMaxSuccessHeart(heartbeat.curHeart);
                if (successHeart != null) {
                    heartbeat.curHeart = successHeart;
                } else {
                    heartbeat.stabled.set(false);
                    curMinHeart = minHeart;
                    heartbeat.curHeart = (curMinHeart + curMaxHeart) / 2;
                    Log.i(TAG, "curHeart = (" + curMaxHeart + " + " + curMinHeart + ") / 2 = " + heartbeat.curHeart);
                }
            } else {
                Log.i(TAG, "continue retry heartbeat.curHeart:" + heartbeat.curHeart + ",stabled:" + heartbeat.stabled.get());
            }
        } else {
            if (count > maxFailedCount) {
                heartbeat.curHeart = (curMinHeart + curMaxHeart) / 2;
                Log.i(TAG, "curHeart = (" + curMaxHeart + " + " + curMinHeart + ") / 2 = " + heartbeat.curHeart);
            } else {
                Log.i(TAG, "continue retry heartbeat.curHeart:" + heartbeat.curHeart + ",stabled:" + heartbeat.stabled.get());
            }
        }
        if (curMaxHeart - curMinHeart < 10) {
            if (!heartbeat.stabled.get()) {
                curMinHeart = minHeart;
            }
            Log.i(TAG, "二分法探测达到瓶颈" + ",curHeart:" + heartbeat.curHeart);
            Log.i(TAG, "curMinHeart:" + curMinHeart + ",curMaxHeart:" + curMaxHeart);
        }
        Log.i(TAG, "curHeart:" + heartbeat.curHeart + ",curMinHeart:" + curMinHeart + ",curMaxHeart:" + curMaxHeart);
    }

    private Integer selectMaxSuccessHeart(int curHeart) {
        ArrayList<Integer> temp = new ArrayList<>(successHeartList);
        Collections.sort(temp, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs.compareTo(lhs);
            }
        });
        successHeartList.clear();
        successHeartList.addAll(temp);
        Log.i(TAG, "successHeartList:" + successHeartList);
        for (Integer heart : successHeartList) {
            if (curHeart >= heart) {
                continue;
            } else {
                return heart;
            }
        }
        return null;
    }

    private Integer selectMinSuccessHeart(int curHeart) {
        ArrayList<Integer> temp = new ArrayList<>(successHeartList);
        Collections.sort(temp, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return lhs.compareTo(rhs);
            }
        });
        successHeartList.clear();
        successHeartList.addAll(temp);
        Log.i(TAG, "successHeartList:" + successHeartList);
        for (Integer heart : successHeartList) {
            if (curHeart >= heart) {
                continue;
            } else {
                return heart;
            }
        }
        return null;
    }

    private Heartbeat getHeartbeat() {
        Heartbeat heartbeat = heartbeatMap.get(networkTag);
        if (heartbeat == null) {
            heartbeat = new Heartbeat();
            heartbeatMap.put(networkTag, heartbeat);
        }
        return heartbeat;
    }

    @Override
    public void receiveHeartbeatFailed(Context context) {
        adjustHeart(context, false);
    }

    @Override
    public void receiveHeartbeatSuccess(Context context) {
        adjustHeart(context, true);
        alarm(context);
    }

    @Override
    public void clear(Context context) {
        stop(context);
        heartbeatMap.clear();
        successHeartList.clear();
        curMinHeart = minHeart;
        curMaxHeart = maxHeart;
        networkTag = null;
        Log.d(TAG, "clear heartbeat...");
    }
}
