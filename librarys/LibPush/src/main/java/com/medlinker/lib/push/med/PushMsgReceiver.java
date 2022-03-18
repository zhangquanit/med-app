package com.medlinker.lib.push.med;

public interface PushMsgReceiver {

    /**
     * 接收设备id
     * @param clientId
     */
    void onReceiveClientId(String clientId);

    /**
     * 监听推送消息
     * @param msg
     * @return true 自己处理消息
     */
    boolean onReceiveMsg(String msg);
}
