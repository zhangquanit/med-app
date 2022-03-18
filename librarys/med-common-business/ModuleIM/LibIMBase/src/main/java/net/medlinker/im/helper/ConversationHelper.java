package net.medlinker.im.helper;


import net.medlinker.im.realm.MsgSessionDbEntity;

/**
 * 会话帮组类
 *
 * @author jiantao
 * @date 2018/5/24
 */
public class ConversationHelper {
    // 当前回话数据session
    private MsgSessionDbEntity mCurrentSession;
    private String mCurrentSessionId;

    private ConversationHelper() {
    }

    /**
     *
     */
    public static class SingletonHolder {
        private static final ConversationHelper INSTANCE = new ConversationHelper();
    }

    public static ConversationHelper getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public String getCurrentSessionId() {
        return mCurrentSessionId == null ? "" : mCurrentSessionId;
    }

    /**
     * 可见情况下缓存当前回话id
     *
     * @param currentSessionId
     */
    public void setCurrentSessionId(String currentSessionId) {
        this.mCurrentSessionId = currentSessionId;
    }

    /**
     * @param currentSession
     */
    public void setCurrentSession(MsgSessionDbEntity currentSession) {
        this.mCurrentSession = currentSession;
    }

    /**
     *
     */
    public void onSessionDestroy() {
        mCurrentSession = null;
        mCurrentSessionId = null;
    }

}
