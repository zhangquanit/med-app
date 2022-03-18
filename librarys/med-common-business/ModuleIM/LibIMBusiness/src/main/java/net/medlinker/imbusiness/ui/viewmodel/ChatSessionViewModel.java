package net.medlinker.imbusiness.ui.viewmodel;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ViewModel;

import com.medlinker.lib.fileupload.entity.FileEntity;

import net.medlinker.base.common.Observable2;
import net.medlinker.im.helper.ConversationHelper;
import net.medlinker.im.helper.MsgDbManager;
import net.medlinker.im.realm.MsgDbEntity;
import net.medlinker.im.realm.MsgSessionDbEntity;
import net.medlinker.imbusiness.entity.ImMenuEntity;
import net.medlinker.imbusiness.entity.ListStringEntity;
import net.medlinker.imbusiness.entity.intent.ChatSessionIntentData;
import net.medlinker.imbusiness.eventbus.IMEventMsg;
import net.medlinker.imbusiness.manager.ChatMsgReadUploadManager;
import net.medlinker.imbusiness.msg.action.IMessageSender;
import net.medlinker.imbusiness.msg.actionImp.MessageSenderImp;
import net.medlinker.imbusiness.ui.repository.ChatSessionRepository;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @author hmy
 * @time 2020/9/24 13:58
 */
public class ChatSessionViewModel extends ViewModel implements LifecycleObserver {

    private MsgSessionDbEntity mSession;
    private String mSessionId;
    private long mGroupId;
    private RealmResults<MsgDbEntity> mDatas;
    private ChatSessionIntentData mIntentData;
    private IMessageSender mMsgSender;
    private Realm mRealm;

    private ChatSessionRepository mRepository;
    private MutableLiveData<ListStringEntity> mGetHistoryLiveData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<ImMenuEntity>> mGetImMenuLiveData = new MutableLiveData<>();


    private boolean mIsScrollChatBottom;

    public MsgSessionDbEntity onCreate(ChatSessionIntentData intentData, Realm realm) {
        mIntentData = intentData;
        mRealm = realm;
        mSessionId = intentData.getSessionId();
        mSession = getCurrentSession(mSessionId, realm);
        mMsgSender = new MessageSenderImp();
        if (null == mSession) {
            return null;
        }
        ChatMsgReadUploadManager.instance().init(mSession);
        if (mSession.isGroup()) {
            mGroupId = mSession.getFromGroup().getId();
        }
        clearUnread();
        ConversationHelper.getInstance().setCurrentSession(mRealm.copyFromRealm(mSession));
        ConversationHelper.getInstance().setCurrentSessionId(mSessionId);
        return mSession;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        ChatMsgReadUploadManager.instance().onPageShow();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        ChatMsgReadUploadManager.instance().onPageHide();
    }

    private void clearUnread() {
        if (mSession.getUnreadMsgCount() > 0) {
            mRealm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    MsgDbManager.INSTANCE.getSessionDbDao().clearUnreadCount(mSession, realm);
                }
            });
        }
    }

    public void checkFirstLoad() {
        if (mSession.getStart() == 0) {
            //如果是第一次请求历史消息，就删除本地消息
            MsgDbManager.INSTANCE.getSessionDbDao().deleteMsgBySessionId(mSessionId, false);
            getHistoryMsg();
        }
        if (null != mDatas && mDatas.size() > 0) {
            ChatMsgReadUploadManager.instance().doMsgUpload(mDatas.get(mDatas.size() - 1));
        }
    }

    public RealmResults<MsgDbEntity> getList() {
        return mDatas = MsgDbManager.INSTANCE.getSessionDbDao().getChatMsgData(mRealm, mSessionId);
    }

    public void sendTextMsg(String text) {
        mMsgSender.sendMsg(mSession, text);
    }

    public void sendImageMessage(ArrayList<FileEntity> listExtra) {
        if (listExtra == null) {
            return;
        }
        Disposable disposable = Observable2.from(listExtra)
                .map(new Function<FileEntity, String>() {
                    @Override
                    public String apply(FileEntity photo) throws Exception {
                        return photo.getFileUrl();
                    }
                })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String path) throws Exception {
                        mMsgSender.sendImage(mSession, path);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    MsgSessionDbEntity getCurrentSession(String sessionId, Realm realm) {
        return MsgDbManager.INSTANCE.getSessionDbDao().getSessionById(sessionId, realm);
    }

    public void getHistoryMsg() {
        getHistoryMsg(15, false);
    }

    private void getHistoryMsg(int limit, final boolean isScrollChatBottom) {
        if (getRepository().getHistoryMsg(mSession, limit, mGetHistoryLiveData)) {
            mIsScrollChatBottom = isScrollChatBottom;
        }
    }

    @Override
    protected void onCleared() {
        ConversationHelper.getInstance().onSessionDestroy();
    }

    public void onGetHistoryFinished(final ListStringEntity s) {
        if (null == mRealm || mRealm.isClosed()) {
            return;
        }
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mSession.setStart(s.getStart());
                if (s.getList().isEmpty()) {
                    mSession.setHistorySession(false);
                }
                int size = mDatas.size();
                if (size > 0) {
                    MsgDbEntity msgDbEntity = mDatas.get(size - 1);
                    if (msgDbEntity != null) {
                        mSession.setLastMsgUserType(msgDbEntity.getFromUser().getType());
                        mSession.setLastMsgType(msgDbEntity.getDataType());
                    }
                }
            }
        });
        EventBus.getDefault().post(new IMEventMsg(IMEventMsg.REALM_UNREAD_COUNT_CHANGED));
    }

    public long getGroupId() {
        return mGroupId;
    }

    public boolean isScrollChatBottom() {
        return mIsScrollChatBottom;
    }

    public MutableLiveData<ListStringEntity> getGetHistoryLiveData() {
        return mGetHistoryLiveData;
    }

    public MutableLiveData<ArrayList<ImMenuEntity>> getImMenuLiveData() {
        return mGetImMenuLiveData;
    }

    public ChatSessionRepository getRepository() {
        if (null == mRepository) {
            mRepository = new ChatSessionRepository();
        }
        return mRepository;
    }
}
