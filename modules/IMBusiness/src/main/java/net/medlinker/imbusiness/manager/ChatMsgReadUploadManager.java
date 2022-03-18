package net.medlinker.imbusiness.manager;

import android.annotation.SuppressLint;

import net.medlinker.base.entity.DataEntity;
import net.medlinker.base.network.HttpResultFunc;
import net.medlinker.im.helper.ConversationHelper;
import net.medlinker.im.im.util.LogUtil;
import net.medlinker.im.realm.MsgDbEntity;
import net.medlinker.im.realm.MsgSessionDbEntity;
import net.medlinker.im.util.SchedulersCompat;
import net.medlinker.imbusiness.network.ApiManager;
import net.medlinker.imbusiness.router.ModuleIMBusinessManager;
import net.medlinker.imbusiness.util.NetworkUtil;
import net.medlinker.libhttp.BaseEntity;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author: pengdaosong CreateTime:  2020-07-21 15:38 Email：pengdaosong@medlinker.com Description:
 * 7.8.6新需求，当用户阅读消息后，需要上报服务器
 */
public class ChatMsgReadUploadManager {

    private static final String TAG = "MsgReadUploadManager";
    /**
     * 当前IM会话，如果没有进入IM聊天界面，这mSession = null
     */

    private boolean mIsGroup;
    private long mCurrentConversationGroupId;

    /**
     * IM聊天页面是否处于可见状态
     */
    private boolean mPageVisible = false;

    /**
     * 最后一条需要上传服务器的消息id，最近一条消息标记为已读，那么该条消息之前的消息自然是已读 -1：当前
     */
    private long mLastNeedUploadMsgId = -1;

    /**
     * 最后一条需要上传服务器的消息,发送者的user id
     */
    private long mLastMsgFromId = 1;

    private boolean mIsConnectInternet = true;

    private static ChatMsgReadUploadManager mChatMsgReadUploadManager;

    private ChatMsgReadUploadManager() {
    }

    public static ChatMsgReadUploadManager instance() {
        if (null == mChatMsgReadUploadManager) {
            mChatMsgReadUploadManager = new ChatMsgReadUploadManager();
        }
        return mChatMsgReadUploadManager;
    }

    private NetworkStatusManger.NetworkStatusListener mStatusListener = new NetworkStatusManger.NetworkStatusListener() {
        @Override
        public void connect(NetworkUtil.NetState netState) {
            mIsConnectInternet = true;
            checkUpload();
        }

        @Override
        public void disconnect() {
            mIsConnectInternet = false;
        }
    };

    public void init(MsgSessionDbEntity mSession) {
        mIsGroup = mSession.isGroup();
        mCurrentConversationGroupId =
                null == mSession.getFromGroup() ? -1 : mSession.getFromGroup().getId();
        mIsConnectInternet = NetworkUtil.isConnected(ModuleIMBusinessManager.INSTANCE.getApplication());
        NetworkStatusManger.instance().register(mStatusListener);
    }

    public boolean checkNotSameConversation(long id, long fromUserId) {
        return null == getCurrentConversationSessionId() || id <= 0 || fromUserId <= 0;
    }

    public void doMsgUpload(MsgDbEntity dbEntity) {
        if (null == dbEntity || null == dbEntity.getFromUser()) {
            return;
        }
        long msgId = dbEntity.getId();
        long formId;
        if (mIsGroup) {
            formId = mCurrentConversationGroupId;
            if (formId <= 0) {
                formId = null == dbEntity.getToGroup() ? -1 : dbEntity.getToGroup().getId();
            }
        } else {
            formId = dbEntity.getFromUser().getId();
        }
        if (checkNotSameConversation(msgId, formId)) {
            return;
        }
        mLastNeedUploadMsgId = msgId;
        mLastMsgFromId = formId;
        if (!mPageVisible || !mIsConnectInternet) {
            return;
        }

        LogUtil.i(TAG, " msgId:" + msgId);
        mChatMsgReadUploadManager.doReadUpload(mLastNeedUploadMsgId, formId);
    }

    private String getCurrentConversationSessionId() {
        return ConversationHelper.getInstance().getCurrentSessionId();
    }

    private Disposable mUploadDisposable;

    @SuppressLint("CheckResult")
    private void doReadUpload(final long uploadMsgId, final long fromId) {
        if (null != mUploadDisposable && !mUploadDisposable.isDisposed()) {
            return;
        }
        LogUtil.i(TAG,
                "chat_sessionId: " + getCurrentConversationSessionId() + " uploadMsgId:" + uploadMsgId);
        mUploadDisposable = ApiManager
                .getMsgApi()
                .markMsgRead(String.valueOf(fromId), String.valueOf(uploadMsgId),
                        mIsGroup ? "group" : "single")
                .compose(SchedulersCompat.<BaseEntity<DataEntity>>applyIoSchedulers())
                .delay(300, TimeUnit.MILLISECONDS)
                .map(new HttpResultFunc<>())
                .subscribe(new Consumer<DataEntity>() {
                    @Override
                    public void accept(DataEntity dataEntity) throws Exception {
                        LogUtil.i(TAG, " success msgId:" + uploadMsgId);
                        if (uploadMsgId == mLastNeedUploadMsgId && fromId == mLastMsgFromId) {
                            mLastNeedUploadMsgId = -1;
                            mLastMsgFromId = -1;
                        } else {
                            checkUpload();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.i(TAG, " fail msgId:" + uploadMsgId);
                        if (uploadMsgId != mLastNeedUploadMsgId || fromId != mLastMsgFromId) {
                            checkUpload();
                        }
                    }
                });
    }

    /**
     * 当页面可见或者联网时，对消息上报进行检测
     */
    public void checkUpload() {
        if (mLastNeedUploadMsgId <= 0 || null == getCurrentConversationSessionId()
                || mLastMsgFromId <= 0) {
            return;
        }
        if (!mPageVisible || !mIsConnectInternet) {
            return;
        }
        doReadUpload(mLastNeedUploadMsgId, mLastMsgFromId);
    }

    /**
     * IM 聊天界面别遮盖
     */
    public void onPageHide() {
        mPageVisible = false;
    }

    /**
     * 聊天界面变得可见
     */
    public void onPageShow() {
        mPageVisible = true;
        checkUpload();
    }

    /**
     * IM 聊天界面销毁时调用
     */
    public void onPageDestroy() {
        destroy();
    }

    private void destroy() {
        mPageVisible = false;
        if (null != mUploadDisposable) {
            mUploadDisposable.dispose();
            mUploadDisposable = null;
        }
        NetworkStatusManger.instance().unregister(mStatusListener);
        mLastNeedUploadMsgId = -1;
        mCurrentConversationGroupId = -1;
        mLastMsgFromId = -1;
        mStatusListener = null;
        mChatMsgReadUploadManager = null;
    }
}
