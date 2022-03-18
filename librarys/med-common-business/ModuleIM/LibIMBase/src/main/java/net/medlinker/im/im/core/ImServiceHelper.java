package net.medlinker.im.im.core;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;


/**
 * TODO 上行走socket
 * 1. 加一个requestId,区分每次请求，对应callback，ImBaseReceiver处理相关逻辑。这里需要服务器支持。
 * 1.1 本地生成reqId，随msg一起传给服务器，server处理后返回携带reqId的响应消息。
 * 1.2 区分普通消息和request响应消息。
 * 1.3
 * <p>
 * 2. 收到消息后，判断主进程存活状态，若挂掉，则发送通知栏消息将其唤醒，正常情况则发广播消息。
 * Created by jiantao on 2017/3/4.
 */

public class ImServiceHelper {

    private static ImServiceHelper mInstance;
    private Context mAppContext;

    private ActivityManager mActivityManager;

    public ActivityManager getActivityManager() {
        return mActivityManager;
    }

    private ImServiceHelper(Context context) {
        this.mAppContext = context.getApplicationContext();
        mActivityManager = (ActivityManager) mAppContext.getSystemService(Context.ACTIVITY_SERVICE);
    }

    /**
     *
     */
    public static synchronized ImServiceHelper getInstance(@NonNull Context appContext) {
        if (mInstance == null) {
            mInstance = new ImServiceHelper(appContext);
        }
        return mInstance;
    }

    /**
     *
     */
    public void connect(long startSocketId, String ip, int port) {
        Intent intent = createImServiceIntent(Constants.OP_TYPE_CONNECT);
        intent.putExtra(Constants.KEY_IP, ip);
        intent.putExtra(Constants.KEY_PORT, port);
        intent.putExtra(Constants.KEY_START_SOCKET_ID, startSocketId);
        this.mAppContext.startService(intent);
    }

    /**
     *
     */
    public void disConnect() {
        Intent intent = createImServiceIntent(Constants.OP_TYPE_DISCONNECT);
        this.mAppContext.startService(intent);
    }

    /**
     * 重连
     */
    public void reConnect() {
        Intent intent = createImServiceIntent(Constants.OP_TYPE_RECONNECT);
        this.mAppContext.startService(intent);
    }

    /**
     * @param msg
     */
    public void sendMessage(byte[] msg) {
        Intent intent = createImServiceIntent(Constants.OP_TYPE_SEND);
        intent.putExtra(Constants.KEY_REMOTE_SOCKET_MSG_DATA, msg);
        try {
            //由于coloros的OPPO手机自动熄屏一段时间后，会启用系统自带的电量优化管理，禁止一切自启动的APP（用户设置的自启动白名单除外），需要try catch
            mAppContext.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Intent createImServiceIntent(int opType) {
        Intent i = new Intent(mAppContext, ImService.class);
        i.putExtra(Constants.KEY_OP_TYPE, opType);
        return i;
    }
}
