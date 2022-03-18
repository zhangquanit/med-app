package net.medlinker.im.action;

import net.medlinker.im.realm.MsgDbEntity;
import net.medlinker.im.realm.MsgSessionDbEntity;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @author hmy
 * @time 2020/9/22 15:49
 */
public interface ISessionDbDao {
    /**
     * 通过ID查询会话对象
     *
     * @param sessionId
     * @return
     */
    MsgSessionDbEntity getSessionById(String sessionId, Realm realm);

    void insertSessionDraft(String sessionId, CharSequence text);

    /**
     * 通过会话获取消息数量，聊天详情
     *
     * @param sessionId
     * @return
     */
    RealmResults<MsgDbEntity> getChatMsgData(Realm realm, String sessionId);

    void deleteMsgBySessionId(String sessionId, boolean resetContent);

    /**
     * 清空未读消息数量
     */
    void clearUnreadCount(MsgSessionDbEntity sessionDbEntity, Realm realm);
}
