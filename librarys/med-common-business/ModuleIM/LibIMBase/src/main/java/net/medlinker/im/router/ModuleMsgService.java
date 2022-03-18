package net.medlinker.im.router;

import com.medlinker.protocol.message.MessageOuterClass;

import net.medlinker.im.entity.UserInfoEntity;
import net.medlinker.im.realm.MsgSessionDbEntity;

import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;

/**
 * @author hmy
 * @time 2020/9/22 14:54
 */
public interface ModuleMsgService {

    void doMsgReceipt(long msgId);

    void onExitGroup(long groupId);

    void doSomeMsgNotify(int notifyType, boolean isHistory);

    void onImInit(final BiConsumer<String, Integer> callBack, final Consumer<Throwable> throwableAction);

    void bindUserTokenForIM(final String token);

    void getOfflineMsg();

    void notifyMsg(final MsgSessionDbEntity entity);

    UserInfoEntity getCurrentUser();

    boolean isInterceptMsg(MessageOuterClass.Message message);

    /**
     * 将IM JSON类型消息转换为文字
     * @param message
     * @return
     */
    String buildJsonMsgContent(MessageOuterClass.Message message);

}
