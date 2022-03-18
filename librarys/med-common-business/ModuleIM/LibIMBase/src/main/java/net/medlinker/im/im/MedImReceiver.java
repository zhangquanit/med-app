package net.medlinker.im.im;

import android.content.Context;

import net.medlinker.im.im.core.Constants;
import net.medlinker.im.im.core.ImBaseReceiver;
import net.medlinker.im.im.core.ImServiceHelper;
import net.medlinker.im.im.core.PacketType;
import net.medlinker.im.im.core.util.ImLogHelper;
import net.medlinker.im.im.util.LogUtil;
import net.medlinker.im.router.ModuleIMManager;

import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

/**
 * @author jiantao
 * @date 2017/3/7
 */

public class MedImReceiver extends ImBaseReceiver {

    private static final String TAG = Constants.getIMCoreLogTag(MedImReceiver.class.getSimpleName());

    @Override
    protected void onReceiveSocketLocalMsg(Context context, int msgWhat) {
        switch (msgWhat) {
            case Constants.LOCAL_MSG_CONNECT:
                // socket连接完成，发送版本号
                final String imVersion = "  V1";
                ImServiceHelper.getInstance(context).sendMessage(imVersion.getBytes());
                break;
            case Constants.LOCAL_MSG_DISCONNECTED:
            case Constants.LOCAL_MSG_CONNECT_FAILED:
                //内部连接失败，或者通知断开连接，这里统一重连
                //延迟5s重连 , 这里不判断网络状态，就不用监听网络变化。
                ImManager.reconnect();
                break;
            default:
        }
    }

    @Override
    protected void onReceiveSocketRemoteMsg(Context context, int msgType, final byte[] byteArrayExtra) {
        LogUtil.i(TAG, ImLogHelper.logByPacketType(msgType));
        switch (msgType) {
            case PacketType.CONNECT:
                ModuleIMManager.INSTANCE.getMsgService().bindUserTokenForIM(new String(byteArrayExtra));
                break;
            case PacketType.CONNECTED:
                ImManager.getOfflineMsg();
                HeartBeatManager.INSTANCE.beginHeartBeat();//连接成功，开始心跳
                break;
            case PacketType.CLOSE:
                ModuleIMManager.INSTANCE.getIMService().dealOffline(new String(byteArrayExtra));
                break;
            case PacketType.RECONNECT:
                ImManager.disConnect();
                ImManager.init();
                break;
            case PacketType.PING:
                ImManager.sendPong();
                HeartBeatManager.INSTANCE.cancleLastHeartBeat();
                break;
            case PacketType.PONG:
                HeartBeatManager.INSTANCE.receivedPong();
                break;
            case PacketType.MESSAGE:
                Observable.just(byteArrayExtra).observeOn(Schedulers.io())
                        .subscribe(new Consumer<Object>() {
                            @Override
                            public void accept(final Object o) throws Exception {
                                try (Realm realm = Realm.getDefaultInstance()) {
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            ImMsgRecivedManager.INSTANCE.saveMessageData((byte[]) o, realm);
                                        }
                                    });
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                throwable.printStackTrace();
                            }
                        }, new Action() {
                            @Override
                            public void run() throws Exception {
                                ModuleIMManager.INSTANCE.getIMService().onMsgUnreadCountChanged();
                            }
                        });
                HeartBeatManager.INSTANCE.cancleLastHeartBeat();
                break;
            default:
        }

    }
}
