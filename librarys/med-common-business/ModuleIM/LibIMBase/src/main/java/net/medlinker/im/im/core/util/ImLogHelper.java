package net.medlinker.im.im.core.util;

import net.medlinker.im.im.core.PacketType;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description
 * @time 2017/2/17
 */
public class ImLogHelper {


    public static String logByPacketType(@PacketType.ImHeadType int type) {
        switch (type) {
            case PacketType.CONNECT:
                return type + "---socket开始连接";
            case PacketType.CONNECTED:
                return type + "---socket连接成功";
            case PacketType.RECONNECT:
                return type + "---socket重新连接";
            case PacketType.PING:
                return type + "---socket心跳ping";
            case PacketType.PONG:
                return type + "---socket心跳pong";
            case PacketType.MESSAGE:
                return type + "---socket业务消息";
        }
        return type + "---unknow";
    }
}
