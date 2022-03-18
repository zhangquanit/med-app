package net.medlinker.im.im;

import android.app.ActivityManager;
import android.app.Application;
import android.util.Log;

import net.medlinker.im.im.core.ImServiceHelper;
import net.medlinker.im.router.ModuleIMManager;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;

/**
 * 外部调用封装。初始化、重连、断开连接、发送消息。
 * Created by jiantao on 2017/3/9.
 */

public class ImManager {

    /**
     * 初始化，创建socket连接。
     */
    public static void init() {
        ModuleIMManager.INSTANCE.getMsgService().onImInit(new BiConsumer<String, Integer>() {
            @Override
            public void accept(String s, Integer integer) throws Exception {
                ImServiceHelper.getInstance(getApplication())
                        .connect(ModuleIMManager.INSTANCE.getIMService().getCurrentUserId(), s, integer);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                reconnect();
            }
        });
    }

    /**
     * 发送ping给服务器
     */
    public static void sendPing() {
        HeartBeatManager.INSTANCE.countTimeOut();
        ByteBuffer byteBuffer = ByteBuffer.allocate(3);
        byte[] ping = {3, 0, 0};
        byteBuffer.put(ping);
        sendMessage(byteBuffer.array());
    }

    /**
     * 发送pong给服务器
     */
    public static void sendPong() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(3);
        byte[] pong = {4, 0, 0};
        byteBuffer.put(pong);
        sendMessage(byteBuffer.array());
    }

    /**
     * 请求gate服务器失败后重连
     */
    public static void reconnect() {
        Observable.timer(5, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        ImManager.init();
                    }
                });
    }

    /**
     *
     */
    public static void connect(long startSocketId, String ip, int port) {
        ImServiceHelper.getInstance(getApplication()).connect(startSocketId, ip, port);
    }

    /**
     *
     */
    public static void disConnect() {
        ImServiceHelper.getInstance(getApplication()).disConnect();
        HeartBeatManager.INSTANCE.stopHeatBeat();
    }

    /**
     * @param msg
     */
    public static void sendMessage(byte[] msg) {
        ImServiceHelper.getInstance(getApplication()).sendMessage(msg);
    }


    /**
     * 检测消息进程是否还存活，不存活重新连接
     * net.medlinker.medlinker:imremote
     */
    public static void checkImRomoteProcessAlive() {
        List<ActivityManager.RunningAppProcessInfo> infos = ImServiceHelper.getInstance(getApplication()).getActivityManager().getRunningAppProcesses();
        boolean isAlive = false;
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            isAlive = info.processName.equals(getApplication().getPackageName() + ":imremote");
            if (isAlive) {
                break;
            }
        }
        Log.i("ImManager", isAlive + "--进程存活状态");
        if (!isAlive) {
            init(); //如果发消息的时候，进程没有在运行，就重新初始化IM
        }
    }

    /**
     * 获取离线消息，并且存入数据库
     */
    public static void getOfflineMsg() {
        ModuleIMManager.INSTANCE.getMsgService().getOfflineMsg();
    }


    private static Application getApplication() {
        return ModuleIMManager.INSTANCE.getIMService().getApplication();
    }
}
