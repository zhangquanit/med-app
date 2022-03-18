package net.medlinker.im.im;

import android.text.TextUtils;
import android.util.Base64;

import com.google.protobuf.InvalidProtocolBufferException;
import com.medlinker.protocol.message.MessageOuterClass;
import com.medlinker.protocol.message.types.CardOuterClass;

import net.medlinker.im.helper.EntityConvertHelper;
import net.medlinker.im.helper.MsgDbManager;
import net.medlinker.im.helper.MsgVoiceManager;
import net.medlinker.im.im.util.LogUtil;
import net.medlinker.im.realm.MsgDbEntity;
import net.medlinker.im.realm.MsgSessionDbEntity;
import net.medlinker.im.router.ModuleIMManager;

import java.util.Collection;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.realm.Realm;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description 消息接受管理类
 * @time 2017/2/15
 */
public enum ImMsgRecivedManager {

    INSTANCE;

    private static final String TAG = ImMsgRecivedManager.class.getName();

    /**
     *
     */
    public void saveMessageData(final byte[] datas, Realm realm) {
        saveMessageData(datas, false, realm);
    }

    /**
     *
     */
    public void saveMessageData(final byte[] datas, boolean isOffline, Realm realm) {
        try {
            MessageOuterClass.Message message = MessageOuterClass.Message.parseFrom(datas);
//            LogUtil.i("ImMsg", message.toString());
            saveMessageData(message, isOffline, realm);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            LogUtil.e(TAG, "InvalidProtocolBufferException");
        }
    }

    /**
     * 获取会话最后一条消息恢复会话，不保存任何消息，只更新会话，如果没有就新增
     *
     * @param msg
     */
    public void msg2Session(final Collection<String> msg) {
        if (msg != null) {
            final Realm realm = Realm.getDefaultInstance();
            Disposable disposable = Observable.fromIterable(msg)
                    .filter(new Predicate<String>() {
                        @Override
                        public boolean test(String s) throws Exception {
                            return !TextUtils.isEmpty(s);
                        }
                    })
                    .map(new Function<String, MessageOuterClass.Message>() {
                        @Override
                        public MessageOuterClass.Message apply(String s) throws Exception {
                            try {
                                return MessageOuterClass.Message.parseFrom(Base64.decode(s, Base64.NO_WRAP));
                            } catch (InvalidProtocolBufferException e) {
                                e.printStackTrace();
                                LogUtil.e(TAG, "InvalidProtocolBufferException");
                            }
                            return null;
                        }
                    })
                    .filter(new Predicate<MessageOuterClass.Message>() {
                        @Override
                        public boolean test(MessageOuterClass.Message message) throws Exception {
                            return message != null;
                        }
                    })
                    .doOnNext(new Consumer<MessageOuterClass.Message>() {
                        @Override
                        public void accept(final MessageOuterClass.Message message) throws Exception {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(EntityConvertHelper.upDataHistorySession(message, realm));
                                    EntityConvertHelper.upDataFatherSession(message, realm);
                                }
                            });
                        }
                    })
                    .doFinally(new Action() {
                        @Override
                        public void run() throws Exception {
                            if (realm != null && !realm.isClosed()) {
                                realm.close();
                            }
                        }
                    })
                    .subscribe(new Consumer<MessageOuterClass.Message>() {
                        @Override
                        public void accept(MessageOuterClass.Message message) throws Exception {
                            ModuleIMManager.INSTANCE.getIMService().onMsgUnreadCountChanged();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            throwable.printStackTrace();
                        }
                    });

        }
    }

    /**
     * 历史消息记录，问诊会话需要
     *
     * @param datas
     */
    public void saveHistoryMsg(final byte[] datas, Realm realm) {
        try {
            MessageOuterClass.Message message = MessageOuterClass.Message.parseFrom(datas);
            save2Realm(message, true, false, realm);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            LogUtil.e(TAG, "InvalidProtocolBufferException");
        }
    }

    /**
     * 保存消息方法
     *
     * @param message
     */
    private void saveMessageData(MessageOuterClass.Message message, boolean isOffline, Realm realm) {
        if (!isOffline) {
            doMsgReceipt(message.getId());
        }
        boolean isSelf = message.getFrom().getId() == ModuleIMManager.INSTANCE.getIMService().getCurrentUserId();
        if (isInterceptMsg(message)) {
            return;
        }
        //如果是自己的消息并且不是问诊相关
        save2Realm(message, isSelf, isOffline, realm);
    }

    private boolean isInterceptMsg(MessageOuterClass.Message message) {
        return ModuleIMManager.INSTANCE.getMsgService().isInterceptMsg(message);
    }


    /**
     * 消息回执
     *
     * @param id
     */
    private void doMsgReceipt(final long id) {
        ModuleIMManager.INSTANCE.getMsgService().doMsgReceipt(id);
    }

    /**
     * 根据数据类型的不同，保存解析成功的数据到realm数据库中
     *
     * @param message
     * @param isHistory
     */
    private void save2Realm(final MessageOuterClass.Message message, final boolean isHistory, final boolean isOffline, Realm realm) {
        switch (message.getDataCase()) {
            case TEXT:
            case VOICE:
            case IMAGE:
            case LOCATION:
            case NOTICE:
            case BUSINESSCARD:
            case JSON:
            case DATA_NOT_SET:
                saveMsgDbAndSessionDb(realm, message, isHistory, isOffline);
                break;
            case CARD:
                checkIfToast(realm, message, isHistory, isOffline);
                break;
            case WITHDRAW:
                MsgDbEntity msg = realm.where(MsgDbEntity.class).equalTo("id", message.getWithdraw().getId()).findFirst();
                if (msg != null) {
                    msg.setWithDraw(true);
                    msg.setFromUser(EntityConvertHelper.protolUser2ImUser(message.getFrom(), realm));
                    msg.setHasRead(true);
                    //撤回的消息设置为已读
                    if (!isHistory) {
                        MsgSessionDbEntity msgSessionDbEntity = EntityConvertHelper.getSessionById(msg.getSessionId(), realm);
                        if (msgSessionDbEntity != null) {
                            msgSessionDbEntity.multiUnread();
                            msgSessionDbEntity.setContent(EntityConvertHelper.getWithDrawContent(message));
                        }
                    }
                }
                break;
            case FRIENDOPERATION:
                if (!isHistory) {
                    //历史消息的新的朋友屏蔽
                    realm.copyToRealmOrUpdate(EntityConvertHelper.conver2NewFriend(message.getFrom(), message.getFriendOperation(), realm));
                    if (message.getFriendOperation().getOperation() != 1) {
                        realm.copyToRealmOrUpdate(EntityConvertHelper.msg2SessionEntity(message, false, isOffline, realm));
                        EntityConvertHelper.upDataFatherSession(message, realm);
                    }
                }
                break;
            case NOTIFY:
                doSomeMsgNotify(message, isHistory, realm);
                break;
            case LOGIN:
                break;
            case GROUPOPERATION:
                if (message.getGroupOperation().getOperation() == 6) {
                    long groupId = message.getGroupOperation().getGroup();
                    ModuleIMManager.INSTANCE.getMsgService().onExitGroup(groupId);
                    //收到退群的消息,清空未读消息数以及设置为已读。
                    MsgSessionDbEntity sessionDbEntity = EntityConvertHelper.getSessionById(EntityConvertHelper.getGroupSessionId(groupId), realm);
                    if (sessionDbEntity != null) {
                        sessionDbEntity.setUnreadMsgCount(0);
                        MsgDbManager.INSTANCE.getMsgDbDao().setHasReadAll(sessionDbEntity.getSessionId(), realm);
                    }
                    return;
                }
                if (!isHistory) {
                    //加群的消息，在新的朋友那边更新数据
                    realm.copyToRealmOrUpdate(EntityConvertHelper.msg2SessionEntity(message, false, isOffline, realm));
                    EntityConvertHelper.upDataFatherSession(message, realm);
//                    //小红点通知更新UI
//                    EventBusUtils.post(EventMsg.HAVE_NEW_APPLY_INFO_MSG);
                }
                break;
            default:
        }
    }

    private void doSomeMsgNotify(MessageOuterClass.Message message, boolean isHistory, Realm realm) {
        ModuleIMManager.INSTANCE.getMsgService().doSomeMsgNotify(message.getNotify().getType(), isHistory);
    }

    /**
     * 检查是否需要toast
     * 0:系统消息会话; 1:toast only; 2:会话+toast
     *
     * @param realm
     * @param message
     * @param isHistory
     */
    private void checkIfToast(Realm realm, MessageOuterClass.Message message, boolean isHistory, boolean isOffline) {
        CardOuterClass.Card card = message.getCard();
        final String title = card.getTitle();
        if (title == null) {
            return;
        }
        final String[] split = title.contains("+") ? title.split("\\+") : null;
        switch (card.getDisplay()) {
//            case 1:
//                if (!isHistory && split != null) {
//                    ToastUtil.showReadAwardToast(MedlinkerApp.getApplication().getApplicationContext(), split[0], "+".concat(split[1]));
//                }
//                break;
//            case 2:
//                if (!isHistory && split != null) {
//                    ToastUtil.showReadAwardToast(MedlinkerApp.getApplication().getApplicationContext(), split[0], "+".concat(split[1]));
//                }
            case 0:
                saveMsgDbAndSessionDb(realm, message
                                .toBuilder()
                                .setCard(card
                                        .toBuilder()
                                        .setTitle(card.getTitle().replace("#", ""))
                                        .build())
                                .build()
                        , isHistory, isOffline);
                break;
            default:
        }
    }


    /**
     * 保存数据和创建会话操作
     *
     * @param mRealm
     * @param message
     * @param isHistory
     */
    private void saveMsgDbAndSessionDb(final Realm mRealm, final MessageOuterClass.Message message, boolean isHistory, boolean isOffline) {
        mRealm.copyToRealmOrUpdate(EntityConvertHelper.msg2ImDbEntity(message, isHistory, mRealm));
        if (!isHistory) {
            mRealm.copyToRealmOrUpdate(EntityConvertHelper.msg2SessionEntity(message, false, isOffline, mRealm));
            EntityConvertHelper.upDataFatherSession(message, mRealm);
        }
        doSaveVoiceUrl(message, mRealm);
    }

    /**
     * 下载语音文件及更新本地消息状态
     *
     * @param message
     * @param mRealm
     */
    private void doSaveVoiceUrl(final MessageOuterClass.Message message, Realm mRealm) {
        if (message.getDataCase() == MessageOuterClass.Message.DataCase.VOICE) {
            //如果是语音消息，则下载文件替换本地的播放url
            MsgDbEntity msgDbEntity = mRealm.where(MsgDbEntity.class).equalTo("id", message.getId()).findFirst();
            msgDbEntity.setMsgSendStatus(MsgDbEntity.MSG_STATUS_SENDING);
            if (TextUtils.isEmpty(msgDbEntity.getVoiceLocalPath())) {
                //如果没有下载好，就下载
                MsgVoiceManager.getInstance().loadVoiceFile(message.getVoice().getUrl(), new Consumer<String>() {
                    @Override
                    public void accept(final String path) throws Exception {
                        //如果是语音消息，则下载语音到本地，并且更新localUrl
                        try (Realm realm = Realm.getDefaultInstance()) {
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    MsgDbEntity voiceDb = realm.where(MsgDbEntity.class).equalTo("id", message.getId()).findFirst();
                                    if (voiceDb != null) {
                                        voiceDb.setMsgSendStatus(MsgDbEntity.MSG_STATUS_SUCCEED);
                                        voiceDb.setVoiceLocalPath(path);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }
    }
}
