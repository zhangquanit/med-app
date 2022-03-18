package net.medlinker.imbusiness;

import android.annotation.SuppressLint;
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

import androidx.lifecycle.MutableLiveData;

import com.medlinker.lib.utils.MedToastUtil;

import net.medlinker.base.network.HttpResultFunc;
import net.medlinker.base.network.SchedulersCompat;
import net.medlinker.base.storage.KVUtil;
import net.medlinker.im.helper.EntityConvertHelper;
import net.medlinker.im.im.ImManager;
import net.medlinker.im.im.ImMsgRecivedManager;
import net.medlinker.im.realm.ImUserDbEntity;
import net.medlinker.im.realm.MsgSessionDbEntity;
import net.medlinker.im.router.ModuleIMManager;
import net.medlinker.im.router.ModuleIMService;
import net.medlinker.imbusiness.entity.ImConfigEntity;
import net.medlinker.imbusiness.entity.MapEntity;
import net.medlinker.imbusiness.eventbus.IMEventMsg;
import net.medlinker.imbusiness.network.ApiManager;
import net.medlinker.imbusiness.router.ModuleIMBusinessManager;
import net.medlinker.imbusiness.router.MsgGroupChatRouter;
import net.medlinker.libhttp.BaseEntity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * @author hmy
 * @time 2020/9/22 16:39
 */
public enum IMGlobalManager {

    INSTANCE;

    private ImConfigEntity mImConfigEntity = new ImConfigEntity();

    @SuppressLint("CheckResult")
    public void loadImConfigInfo() {
        if (ModuleIMManager.INSTANCE.getIMService().isVisitor()) {
            return;
        }
        ImManager.init();
        ApiManager.getImConfigApi().getImConfig("im-config")
                .compose(SchedulersCompat.<BaseEntity<ImConfigEntity>>applyIoSchedulers())
                .map(new HttpResultFunc<ImConfigEntity>())
                .subscribe(new Consumer<ImConfigEntity>() {
                    @Override
                    public void accept(ImConfigEntity imConfigEntity) throws Exception {
                        setImConfigEntity(imConfigEntity);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }


    private Disposable mGetHistorySessionDisposable;
    private List<MutableLiveData<Boolean>> mGetHistorySessionCallBacks = new ArrayList<>();

    public void removeHistorySessionCallBack(MutableLiveData<Boolean> callBack) {
        mGetHistorySessionCallBacks.remove(callBack);
    }

    /**
     * 获取历史会话
     */
    public void getHistorySession(final MutableLiveData<Boolean> callBack) {
        if (callBack != null && !mGetHistorySessionCallBacks.contains(callBack)) {
            mGetHistorySessionCallBacks.add(callBack);
        }
        if (mGetHistorySessionDisposable != null && !mGetHistorySessionDisposable.isDisposed()) {
            updateHistorySessionCallBackState(false);
            return;
        }
        /*
         * 是否已经拉取过历史会话记录，key为版本号加上userId，保证版本升级和更换设备能够拉取到历史消息
         */
        final String ifGetHistorySessionKey = BuildConfig.VERSION_NAME
                + ModuleIMBusinessManager.INSTANCE.getBusinessService().getCurrentUserId();
        if (KVUtil.getBoolean(ifGetHistorySessionKey, false)) {
            updateHistorySessionCallBackState(true);
        } else {
            updateHistorySessionCallBackState(false);
            mGetHistorySessionDisposable = ApiManager.getMsgApi().getHistorySession()
                    .map(new Function<MapEntity, Map<String, String>>() {
                        @Override
                        public Map<String, String> apply(MapEntity mapEntity) {
                            if (mapEntity.getCode() != 0) {
                                MedToastUtil.showMessage(mapEntity.getMsg());
                                throw new RuntimeException();
                            }
                            return mapEntity.getData();
                        }
                    })
                    .doOnNext(new Consumer<Map<String, String>>() {
                        @Override
                        public void accept(Map<String, String> stringStringMap) {
                            Collection<String> values = stringStringMap.values();
                            Iterator<String> iterator = values.iterator();
                            while (iterator.hasNext()) {
                                String next = iterator.next();
                                if (TextUtils.isEmpty(next)) {
                                    iterator.remove();
                                }
                            }
                            ImMsgRecivedManager.INSTANCE.msg2Session(values);
                        }
                    })
                    .compose(SchedulersCompat.<Map<String, String>>applyIoSchedulers())
                    .subscribe(new Consumer<Map<String, String>>() {
                        @Override
                        public void accept(Map<String, String> stringStringMap) {
                            if (mGetHistorySessionDisposable != null) {
                                mGetHistorySessionDisposable.dispose();
                            }
                            updateHistorySessionCallBackState(true);
                            KVUtil.set(ifGetHistorySessionKey, true);
                            EventBus.getDefault().post(new IMEventMsg(IMEventMsg.REALM_UNREAD_COUNT_CHANGED));
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) {
                            if (mGetHistorySessionDisposable != null) {
                                mGetHistorySessionDisposable.dispose();
                            }
                            updateHistorySessionCallBackState(true);
                            EventBus.getDefault().post(new IMEventMsg(IMEventMsg.REALM_UNREAD_COUNT_CHANGED));
                        }
                    });
        }
    }

    private void updateHistorySessionCallBackState(boolean isLoadOk) {
        for (MutableLiveData<Boolean> callBack : mGetHistorySessionCallBacks) {
            callBack.setValue(isLoadOk);
        }
    }

    /**
     *
     */
    public void notifyMsg(final MsgSessionDbEntity entity) {
        if (ModuleIMManager.INSTANCE.getIMService().isAppForeground()) {
            return;
        }
        if (checkIfCanPush(entity)) {
            return;
        }
        String title = entity.isGroup() ? entity.getFromGroup().getTeamName() : entity.getFromUser().getName();
        ImUserDbEntity lastMsgUser = entity.getLastMsgFromUser();
        if (lastMsgUser != null) {
            title = lastMsgUser.getName();
        }
//        String title = entity.getFromUser().getName();
        int num = entity.getUnreadMsgCount();
        if (num > 1) {
            title = title + "(" + num + "条)";
        }
        String content = entity.getContent();
        NotificationManager notificationManager = (NotificationManager) ModuleIMManager.INSTANCE.getIMService()
                .getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder notification = new Notification.Builder(ModuleIMManager.INSTANCE.getIMService()
                .getApplication());
        ModuleIMService imService = ModuleIMManager.INSTANCE.getIMService();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String appName = imService.getAppName();
            NotificationChannel channel = new NotificationChannel(appName,
                    title, NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setShowBadge(true);
            notificationManager.createNotificationChannel(channel);
            notification.setChannelId(appName);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification.setSmallIcon(imService.getNotificationLollipopSmallIcon());
        } else {
            notification.setSmallIcon(imService.getNotificationSmallIcon());
        }
        notification.setContentTitle(title);
        notification.setContentText(content);
        notification.setAutoCancel(true);
//        notification.setNumber(entity.getUnreadMsgCount());手机兼容屏蔽掉
        notification.setDefaults(Notification.DEFAULT_ALL);
        Intent rintent = new Intent(imService.getApplication(), imService.getStartAppActivity());
        rintent.setData(getUri(entity));
        PendingIntent contentIntent = PendingIntent.getActivity(imService.getApplication(), 0, rintent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(contentIntent);
        notificationManager.notify((int) (entity.isGroup() ? entity.getFromGroup().getId() : entity.getFromUser().getId()), notification.build());
    }

    /**
     * 判断是否能够push
     *
     * @param entity
     * @return
     */
    private boolean checkIfCanPush(final MsgSessionDbEntity entity) {
        if (checkPushTimeNotValid()) {
            if (!entity.isTimeValid()) {
                return true;
            }
            entity.setTimeValid(false);
        } else {
            if (!entity.isTimeValid()) {
                entity.setTimeValid(true);
            }
        }
        return false;
    }

    /**
     * 判断当前时间是否在免打扰区间内
     *
     * @return
     */
    public boolean checkPushTimeNotValid() {
        boolean isValid = false;
        ImConfigEntity imConfigEntity = getImConfigEntity();
        for (ArrayList<Integer> integers : imConfigEntity.getGroupSilencePeriod()) {
            //开始时间 0s 距离00.00
            int start = integers.get(0);
            //结束时间 xxS 距离00.00
            int end = integers.get(1);
            isValid = isValid || rangeInDefined(beyondZeroSecond(), start, end);
        }
        return isValid;
    }

    private Uri getUri(MsgSessionDbEntity entity) {
        Uri uri;
        if (entity.getSessionId().equals(EntityConvertHelper.SESSION_ID_NEW_FRIEND)) {
            return ModuleIMBusinessManager.INSTANCE.getBusinessService().getNewFriendJumpUri(entity);
        } else {
            if (!ModuleIMBusinessManager.INSTANCE.getBusinessService().isJumpToChat(entity)) {
                return ModuleIMBusinessManager.INSTANCE.getBusinessService().getNotJumpToChatUri(entity);
            } else {
                if (entity.isGroup()) {
                    uri = ModuleIMBusinessManager.INSTANCE.getBusinessService().getJumpToGroupChatUri(entity);
                    if (uri == null) {
                        uri = new Uri.Builder()
                                .path(MsgGroupChatRouter.ROUTER)
                                .appendQueryParameter("groupId", String.valueOf(entity.getFromGroup().getId()))
                                .appendQueryParameter("type", String.valueOf(entity.getFromGroup().getBusinessType()))
                                .appendQueryParameter("name", entity.getFromGroup().getName())
                                .appendQueryParameter("avatar", entity.getFromGroup().getSingleAvatar())
                                .appendQueryParameter("membersNum", String.valueOf(entity.getFromGroup().getAmount()))
                                .appendQueryParameter("businessId", String.valueOf(entity.getFromGroup().getBusinessId()))
                                .build();
                    }
                } else {
                    return ModuleIMBusinessManager.INSTANCE.getBusinessService().getJumpToSingleChatUri(entity);
                }
            }
        }
        return uri;
    }

    public void setImConfigEntity(ImConfigEntity imConfigEntity) {
        this.mImConfigEntity = imConfigEntity;
    }

    public ImConfigEntity getImConfigEntity() {
        return mImConfigEntity;
    }

    private boolean rangeInDefined(int current, int min, int max) {
        return Math.max(min, current) == Math.min(current, max);
    }

    /**
     * 当前时间距离当天凌晨的秒
     *
     * @return
     */
    private int beyondZeroSecond() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY) * 60 * 60;
        int minute = calendar.get(Calendar.MINUTE) * 60;
        int second = calendar.get(Calendar.SECOND);
        return hour + minute + second;
    }
}
