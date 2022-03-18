package net.medlinker.im.im.core;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description 发送的回调接口
 * @time 2017/2/16
 */
public interface ISendCallBack {
    void onSuccess();
    void onFailed(Exception e);

}
