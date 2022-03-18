package net.medlinker.im.im;

import android.util.Log;


import net.medlinker.im.router.ModuleIMManager;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;


/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description
 * @time 2017/4/1
 */
public enum HeartBeatManager {

    INSTANCE;

    private static final String TAG = HeartBeatManager.class.getName();
    Disposable subscribe;

    private HeartbeatScheduler heartbeatScheduler = new HearbeatSchedulerImp();

    /**
     * 开始心跳
     */
    public void beginHeartBeat() {
        heartbeatScheduler.start(ModuleIMManager.INSTANCE.getIMService().getApplication());
        Log.i(TAG, "beginHeartBeat");
    }

    public void countTimeOut() {
        Log.i(TAG, "发送ping,开始countTimeOut");
        subscribe = Observable.interval(1, TimeUnit.SECONDS)
                .takeWhile(new Predicate<Long>() {
                    @Override
                    public boolean test(Long aLong) throws Exception {
                        return aLong <= HeartbeatScheduler.TIMEOUT;
                    }
                })
                .filter(new Predicate<Long>() {
                    @Override
                    public boolean test(Long aLong) throws Exception {
                        Log.i(TAG, aLong + "");
                        return aLong == HeartbeatScheduler.TIMEOUT;
                    }
                })
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {//如果超时了，下调心跳间隔,断开重连
                        heartbeatScheduler.receiveHeartbeatFailed(ModuleIMManager.INSTANCE.getIMService().getApplication());
                        ImManager.reconnect();
                    }
                });
    }

    /**
     * 收到服务器返回的pong,
     */
    public void receivedPong() {
        Log.i(TAG, "receivedPong");
        heartbeatScheduler.receiveHeartbeatSuccess(ModuleIMManager.INSTANCE.getIMService().getApplication());//成功获得心跳，调整稳定心跳值
        if (subscribe != null) {
            Log.i(TAG,"unsubscribe");
            subscribe.dispose();
        }
    }

    /**
     * 如果有消息再传输，则取消上一次的心跳，由于上行没有通过socket所以这里暂时没有添加上行
     */
    public void cancleLastHeartBeat() {
        Log.i(TAG, "cancleLastHeartBeat");
        heartbeatScheduler.stop(ModuleIMManager.INSTANCE.getIMService().getApplication());//如果有消息在socket发送，则取消上一个心跳延迟
        heartbeatScheduler.start(ModuleIMManager.INSTANCE.getIMService().getApplication());//重新开始心跳逻辑
    }

    /**
     * 断开连接停止心跳
     */
    public void stopHeatBeat() {
        Log.i(TAG, "stopHeatBeat");
        heartbeatScheduler.start(ModuleIMManager.INSTANCE.getIMService().getApplication());
        heartbeatScheduler.clear(ModuleIMManager.INSTANCE.getIMService().getApplication());
    }
}
