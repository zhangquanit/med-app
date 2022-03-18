package net.medlinker.imbusiness.eventbus;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author hmy
 * @time 2020/9/24 16:09
 */
public class IMEventMsg<T> {

    public static final int MSG_SEND_STATUS_SUCCESS = 1;
    public static final int REALM_UNREAD_COUNT_CHANGED = 2;

    @IntDef({
            MSG_SEND_STATUS_SUCCESS,
            REALM_UNREAD_COUNT_CHANGED,
    })

    @Retention(RetentionPolicy.SOURCE)
    public @interface EventMsgType {
    }

    /**
     * 消息类型
     */
    private int mType;
    /**
     * 消息传递实体
     */
    private T mMessage;
    /**
     * 标识参数
     */
    private int mArg1;

    public IMEventMsg(@IMEventMsg.EventMsgType int type, T message) {
        this(type, message, 0);
    }

    public IMEventMsg(@IMEventMsg.EventMsgType int type, T message, int arg1) {
        this.mType = type;
        this.mMessage = message;
        this.mArg1 = arg1;
    }

    public IMEventMsg(@IMEventMsg.EventMsgType int type) {
        this(type, null);
    }

    @IMEventMsg.EventMsgType
    public int getType() {
        return mType;
    }

    public void setType(@IMEventMsg.EventMsgType int type) {
        this.mType = type;
    }

    public T getMessage() {
        return mMessage;
    }

    public void setMessage(T message) {
        this.mMessage = message;
    }

    public int getArg1() {
        return mArg1;
    }

    public void setArg1(int arg1) {
        this.mArg1 = arg1;
    }
}
