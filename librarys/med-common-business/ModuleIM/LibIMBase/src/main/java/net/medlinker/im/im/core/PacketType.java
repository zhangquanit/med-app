package net.medlinker.im.im.core;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description im协议头type
 * @time 2017/2/16
 */
public interface PacketType {

    int CONNECT = 0x00; // 建立连接，由server发起
    int CONNECTED = 0x01; // 连接已建立，由server发起
    int RECONNECT = 0x02; // 重建连接，由server发起
    int PING = 0x03; // 心跳包，client与server都可发起
    int PONG = 0x04; // 心跳响应包，client与server都可发起
    int MESSAGE = 0x05; // 业务消息，由server发起
    int CLOSE = 0x07; // 被踢掉，不需要重连

    @IntDef({CONNECT,
            CONNECTED,
            RECONNECT,
            PING,
            PONG,
            MESSAGE,
            CLOSE,
    })

    @Retention(RetentionPolicy.SOURCE)
    @interface ImHeadType {

    }

}
