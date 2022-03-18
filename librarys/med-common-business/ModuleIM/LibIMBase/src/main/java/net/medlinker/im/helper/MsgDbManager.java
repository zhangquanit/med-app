package net.medlinker.im.helper;

import net.medlinker.im.action.IMsgDbDao;
import net.medlinker.im.action.ISessionDbDao;
import net.medlinker.im.actionImp.MsgDbDaoImp;
import net.medlinker.im.actionImp.SessionDaoImp;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description 数据库管理类
 * @time 2017/4/6
 */
public enum MsgDbManager {

    INSTANCE;

    private ISessionDbDao mSessionDbDao;

    private IMsgDbDao mMsgDbDao;

    /**
     *
     */
    public ISessionDbDao getSessionDbDao() {
        if (mSessionDbDao == null) {
            mSessionDbDao = new SessionDaoImp();
        }
        return mSessionDbDao;
    }

    /**
     *
     */
    public IMsgDbDao getMsgDbDao() {
        if (mMsgDbDao == null) {
            mMsgDbDao = new MsgDbDaoImp();
        }
        return mMsgDbDao;
    }

}
