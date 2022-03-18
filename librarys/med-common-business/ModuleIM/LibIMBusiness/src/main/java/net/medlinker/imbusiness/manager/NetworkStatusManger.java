package net.medlinker.imbusiness.manager;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import net.medlinker.im.im.util.LogUtil;
import net.medlinker.imbusiness.router.ModuleIMBusinessManager;
import net.medlinker.imbusiness.util.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: pengdaosong. CreateTime:  2018/12/12 1:50 PM Email：pengdaosong@medlinker.com. Description:
 */
public class NetworkStatusManger {

    private static final String TAG = "NetworkStatusManger";
    private static final NetworkStatusManger mNetworkStatusManger = new NetworkStatusManger();

    private List<NetworkStatusListener> mListeners = new ArrayList<>(5);

    private boolean mIsInited = false;
    private Application mApplication;

    public static final NetworkStatusManger instance() {
        if (!mNetworkStatusManger.mIsInited) {
            mNetworkStatusManger.init(ModuleIMBusinessManager.INSTANCE.getApplication());
        }
        return mNetworkStatusManger;
    }

    private void init(Application application) {
        mApplication = application;
        mIsInited = true;
        IntentFilter mFilter = new IntentFilter();
        //添加接收网络连接状态改变的action
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        application.registerReceiver(mReceiver, mFilter);
    }

    public void destroy() {
        if (null != mApplication && mIsInited) {
            mApplication.unregisterReceiver(mReceiver);
        }
    }

    /**
     * 监听网络变化
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //网络未链接
            if (!NetworkUtil.isConnected(context)) {
                LogUtil.e(TAG, "network disconnect");
                for (NetworkStatusListener listener : mListeners) {
                    listener.disconnect();
                }
            } else {
                NetworkUtil.NetState netState = NetworkUtil.getCurrentNetStateCode(context);
                for (NetworkStatusListener listener : mListeners) {
                    listener.connect(netState);
                }
            }
        }
    };

    public void register(NetworkStatusListener listener) {
        if (mListeners.contains(listener)) {
            return;
        }
        mListeners.add(listener);
    }

    public void unregister(NetworkStatusListener listener) {
        if (mListeners.contains(listener)) {
            mListeners.remove(listener);
        }
    }

    public interface NetworkStatusListener {
        void connect(NetworkUtil.NetState netState);

        void disconnect();
    }

}
