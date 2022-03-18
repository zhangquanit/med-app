package net.medlinker.im.action;

import io.realm.Realm;

/**
 * @author hmy
 * @time 2020/9/22 15:50
 */
public interface IMsgDbDao {

    void setHasReadAll(String sessionId, Realm realm);

    void deleteMsgByTimeStamp(long timestamp);
}
