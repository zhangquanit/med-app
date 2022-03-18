package net.medlinker.imbusiness.msg.actionImp;

import android.annotation.SuppressLint;
import android.content.ContentResolver;

import com.medlinker.lib.fileupload.RxMedFileUpload;
import com.medlinker.lib.fileupload.entity.FileEntity;
import com.medlinker.lib.fileupload.entity.UploadConfig;
import com.medlinker.lib.utils.MedToastUtil;
import com.medlinker.network.retrofit.RetrofitProvider;
import com.medlinker.network.retrofit.error.ApiException;
import com.medlinker.network.retrofit.error.ErrorConsumer;
import com.medlinker.protocol.message.MessageOuterClass;

import net.medlinker.base.common.CommonCallBack;
import net.medlinker.im.helper.EntityConvertHelper;
import net.medlinker.im.helper.MsgDbManager;
import net.medlinker.im.helper.ProtolBuildHelper;
import net.medlinker.im.im.ImManager;
import net.medlinker.im.realm.ImUserDbEntity;
import net.medlinker.im.realm.MsgDbEntity;
import net.medlinker.im.realm.MsgSessionDbEntity;
import net.medlinker.im.router.ModuleIMManager;
import net.medlinker.imbusiness.entity.MsgCardEntity;
import net.medlinker.imbusiness.entity.ProtolCallBackEntity;
import net.medlinker.imbusiness.eventbus.IMEventMsg;
import net.medlinker.imbusiness.msg.action.IMessageSender;
import net.medlinker.imbusiness.router.ModuleIMBusinessManager;
import net.medlinker.imbusiness.util.MsgSenderHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.net.URI;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.realm.Realm;

/**
 * @author hmy
 * @time 2020/9/24 16:06
 */
public class MessageSenderImp implements IMessageSender {
    public static final String UPLOAD_CHAT = "pub-med-chat";

    @Override
    public void sendMsg(final MsgSessionDbEntity session, final String content) {
        final MsgDbEntity msgDbEntity = new MsgDbEntity();
        msgDbEntity.setDataType(MessageOuterClass.Message.DataCase.TEXT.getNumber());
        msgDbEntity.setTextContent(content);
        packageCommonData(msgDbEntity, session);
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
//                    if (atMembersList != null) {
//                        for (UserDetailBaseEntity userBaseEntity : atMembersList) {
//                            ImUserDbEntity userDbEntity = realm.where(ImUserDbEntity.class).equalTo("id", userBaseEntity.getId()).findFirst();
//                            if (userDbEntity == null) {
//                                userDbEntity = new ImUserDbEntity(userBaseEntity.getId(), userBaseEntity.getName());
//                            }
//                            msgDbEntity.addAtUsers(userDbEntity);
//                        }
//                    }
                    session.setContent(content);
                    session.setTimeStamp(System.currentTimeMillis());
                    updateParentSession(session);
                    realm.copyToRealmOrUpdate(msgDbEntity);
                }
            });
        }
        sendMessage2Server(msgDbEntity.getTimestamp(), ProtolBuildHelper.getTextMessage(msgDbEntity));

    }

    @Override
    public void sendImage(final MsgSessionDbEntity session, String imagePath) {
        final MsgDbEntity msgDbEntity = new MsgDbEntity();
        msgDbEntity.setDataType(MessageOuterClass.Message.DataCase.IMAGE.getNumber());
        msgDbEntity.setImageUrl(imagePath);
        packageCommonData(msgDbEntity, session);
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    session.setContent("你发送了一张图片");
                    session.setTimeStamp(System.currentTimeMillis());
                    updateParentSession(session);
                    realm.copyToRealmOrUpdate(msgDbEntity);
                }
            });
        }
        Observable<MessageOuterClass.Message> observable;
        if (imagePath.startsWith("http")) {
            observable = Observable.just(imagePath)
                    .map(new Function<String, MessageOuterClass.Message>() {
                        @Override
                        public MessageOuterClass.Message apply(String s) throws Exception {
                            return ProtolBuildHelper.getImageMessage(msgDbEntity);
                        }
                    });
        } else {
            File tempFile = null;
            if (imagePath.startsWith("/")) {
                tempFile = new File(imagePath);
            } else if (imagePath.startsWith(ContentResolver.SCHEME_FILE)) {
                tempFile = new File(URI.create(imagePath));
            }
            if (tempFile == null || !tempFile.exists()) {
                observable = Observable.just(imagePath)
                        .map(new Function<String, MessageOuterClass.Message>() {
                            @Override
                            public MessageOuterClass.Message apply(String s) throws Exception {
                                throw new RuntimeException();
                            }
                        });
            } else {
                imagePath = tempFile.getAbsolutePath();
                observable = RxMedFileUpload.startUpload(new UploadConfig()
                        .setUploadBucket(UPLOAD_CHAT)
                        .setFileEntity(new FileEntity(imagePath.hashCode(), imagePath)))
                        .map(new Function<List<FileEntity>, MessageOuterClass.Message>() {
                            @Override
                            public MessageOuterClass.Message apply(List<FileEntity> fileEntities) throws Exception {
                                msgDbEntity.setImageUrl(fileEntities.get(0).getFileUrl());
                                return ProtolBuildHelper.getImageMessage(msgDbEntity);
                            }
                        });
            }
        }
        Disposable disposable = observable.subscribe(new Consumer<MessageOuterClass.Message>() {
            @Override
            public void accept(MessageOuterClass.Message message) throws Exception {
                sendMessage2Server(msgDbEntity.getTimestamp(), message);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                try (Realm realm = Realm.getDefaultInstance()) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            MsgDbEntity msg = realm.where(MsgDbEntity.class)
                                    .equalTo("timestamp", msgDbEntity.getTimestamp())
                                    .findFirst();
                            if (msg != null) {
                                msg.setMsgSendStatus(MsgDbEntity.MSG_STATUS_FAILURE);
                            }
                        }
                    });
                }
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public void reSendMsg(MsgDbEntity message) {
        if (message == null || !message.isValid()) {
            return;
        }
        try (Realm realm = Realm.getDefaultInstance()) {
            final String sessionId = message.getSessionId();
            final long time = message.getTimestamp();
            MsgSessionDbEntity sessionDbEntity = getSessionById(sessionId, realm);
            MessageOuterClass.Message.DataCase dataCase = MessageOuterClass.Message.DataCase.forNumber(message.getDataType());
            if (dataCase == null) {
                return;
            }
            switch (dataCase) {
                case TEXT:
                    sendMsg(sessionDbEntity, message.getTextContent() /*, EntityConvertHelper.imUsers2MedUsers(message.getAtUsers())*/);
                    break;
                case VOICE:
                    // TODO
//                    FileEntity fileEntity = new FileEntity();
//                    fileEntity.setFileUrl(message.getVoiceUrl());
//                    fileEntity.setDuration(message.getVoiceDuration());
//                    sendVoice(sessionDbEntity, fileEntity);
                    break;
                case JSON:
                    message.setTimestamp(System.currentTimeMillis());
                    sendMessage2Server(message.getTimestamp(), ProtolBuildHelper.getJsonMessage(message), new CommonCallBack() {
                        @Override
                        public void onCallBack(Object object) {
                            final ProtolCallBackEntity protolCallBackEntity = (ProtolCallBackEntity) object;
                            boolean isSuccess = null != protolCallBackEntity;
                            if (isSuccess) {
                                try (Realm realm = Realm.getDefaultInstance()) {
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            MsgSessionDbEntity session = getSessionById(sessionId, realm);
                                            if (session != null) {
                                                session.setTimeStamp(System.currentTimeMillis());
                                                MsgDbEntity msg = realm.where(MsgDbEntity.class)
                                                        .equalTo("timestamp", time)
                                                        .findFirst();
                                                if (msg != null) {
                                                    msg.setId(protolCallBackEntity.getMsgId());
                                                    msg.setMsgSendStatus(MsgDbEntity.MSG_STATUS_SUCCEED);
                                                }
                                            }

                                        }
                                    });
                                }
                            }
                        }
                    });
                    break;
                case IMAGE:
//                    try {
//                        int id = Integer.parseInt(message.getImageUrl());
//                        EmojiconEntity e = new EmojiconEntity();
//                        e.setIdentityCode(id);
//                        e.setName(message.getImagePreview());
//                        sendEmojIcon(sessionDbEntity, e); //如果是表情就重新发送表情
//                    } catch (NumberFormatException e) {
//                        sendImage(sessionDbEntity, message.getImageUrl()); //如果是图片才重新发送图片
//                    }
                    sendImage(sessionDbEntity, message.getImageUrl());
                    break;
                case BUSINESSCARD:
//                    shareUserCard(sessionDbEntity, EntityConvertHelper.imUser2MedUser(message.getCardUser()));
                    break;
                case CARD:
                    sendShareData(sessionDbEntity, message);
                    break;
                default:
            }
        }
        MsgDbManager.INSTANCE.getMsgDbDao().deleteMsgByTimeStamp(message.getTimestamp()); //重新发送过后删除之前的消息
    }

    private void sendShareData(final MsgSessionDbEntity session, final MsgDbEntity oldMsg) {
        MsgCardEntity cardEntity = new MsgCardEntity();
        cardEntity.setLabel(oldMsg.getCardLabel());
        cardEntity.setTitle(oldMsg.getCardTitle());
        cardEntity.setSummary(oldMsg.getCardSummary());
        cardEntity.setImage(oldMsg.getCardImageUrl());
        cardEntity.setUrl(oldMsg.getCardTargetUrl());
        cardEntity.setExtra(oldMsg.getCardExtra());
        sendMsgCard(session, cardEntity);
    }

    @Override
    public void sendMsgCard(final MsgSessionDbEntity session, final MsgCardEntity cardMsg) {
        final MsgDbEntity msgDbEntity = new MsgDbEntity();
        packageCommonData(msgDbEntity, session);
        msgDbEntity.setDataType(MessageOuterClass.Message.DataCase.CARD.getNumber());
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    msgDbEntity.setCardLabel(cardMsg.getLabel());
                    msgDbEntity.setCardSummary(cardMsg.getSummary());
                    msgDbEntity.setCardTitle(cardMsg.getTitle());
                    msgDbEntity.setCardImageUrl(cardMsg.getImage());
                    msgDbEntity.setCardTargetUrl(cardMsg.getUrl());
                    msgDbEntity.setCardExtra(cardMsg.getExtra());
                    session.setTimeStamp(System.currentTimeMillis());
                    session.setContent("[".concat(cardMsg.getTitle()).concat("]"));
                    updateParentSession(session);
                    realm.copyToRealmOrUpdate(msgDbEntity);
                }
            });
        }
        sendMessage2Server(msgDbEntity.getTimestamp(), ProtolBuildHelper.getCardMessage(msgDbEntity));
    }

    @SuppressLint("CheckResult")
    @Override
    public void withDrawMsg(final MsgDbEntity data) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    MsgDbManager.INSTANCE.getSessionDbDao().getSessionById(data.getSessionId(), realm).setContent("你撤回了一条消息");
                }
            });
        }
        MsgSenderHelper.sendImMessage2Server(ProtolBuildHelper.getWithDrawMessage(data.getId()))
                .subscribe(new Consumer<ProtolCallBackEntity>() {
                    @Override
                    public void accept(ProtolCallBackEntity protolCallBackEntity) throws Exception {
                        try (Realm realm = Realm.getDefaultInstance()) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    MsgDbEntity dbEntity = realm.where(MsgDbEntity.class)
                                            .equalTo("id", data.getId())
                                            .findFirst();
                                    dbEntity.setWithDraw(true);
                                    dbEntity.setWithdrawTimestamp(System.currentTimeMillis());
                                }
                            });
                        }
                    }
                }, new ErrorConsumer());
    }

    @Override
    public void updateParentSession(MsgSessionDbEntity session) {

    }

    @Override
    public void reSendPrescription(MsgDbEntity message) {
        try (Realm realm = Realm.getDefaultInstance()) {
            final String sessionId = message.getSessionId();
            MsgSessionDbEntity sessionDbEntity = getSessionById(sessionId, realm);
            MsgDbEntity msgDbEntity = getJsonMsgDbEntity(sessionDbEntity, message.getJsonString(), message.getJsonType());
            sendPrescriptionEntity(sessionDbEntity, msgDbEntity, null);
            MsgDbManager.INSTANCE.getMsgDbDao().deleteMsgByTimeStamp(message.getTimestamp()); //重新发送过后删除之前的消息
        }
    }

    public void sendPrescriptionEntity(final MsgSessionDbEntity session, final MsgDbEntity msgDbEntity, final CommonCallBack<Boolean> commonCallBack) {
//        final String sessionId = session.getSessionId();
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    session.setContent("你发送了一个电子处方笺给对方");
                    session.setTimeStamp(msgDbEntity.getTimestamp());
                    updateParentSession(session);
                    realm.copyToRealmOrUpdate(msgDbEntity);
                }
            });
        }
        sendMessage2Server(msgDbEntity.getTimestamp(), ProtolBuildHelper.getJsonMessage(msgDbEntity), new CommonCallBack<ProtolCallBackEntity>() {
            @Override
            public void onCallBack(final ProtolCallBackEntity object) {
                boolean isSuccess = null != object;
                if (null != commonCallBack) {
                    commonCallBack.onCallBack(isSuccess);
                }
                if (isSuccess) {
//                    EventBusUtils.post(UserEvent.OPEN_PRESCRIPTION_FINISHED, sessionId);
                }
            }
        });
    }

    @Override
    public void sendJsonMsgEntity(MsgSessionDbEntity session, final String json, final int jsonType, final String sessionContent, final CommonCallBack<Boolean> commonCallBack) {
        MsgDbEntity msgDbEntity = getJsonMsgDbEntity(session, json, jsonType);

        final String sessionId = session.getSessionId();
        final long timestamp = msgDbEntity.getTimestamp();
        sendMessage2Server(timestamp, ProtolBuildHelper.getJsonMessage(msgDbEntity), new CommonCallBack<ProtolCallBackEntity>() {
            @Override
            public void onCallBack(final ProtolCallBackEntity object) {
                boolean isSuccess = null != object;
                if (null != commonCallBack) {
                    commonCallBack.onCallBack(isSuccess);
                }
                if (isSuccess) {
                    try (Realm realm = Realm.getDefaultInstance()) {
                        final MsgSessionDbEntity sessionDbEntity = getSessionById(sessionId, realm);
                        if (null != sessionDbEntity) {
                            final MsgDbEntity msgDbEntity = getJsonMsgDbEntity(sessionDbEntity, json, jsonType);
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    sessionDbEntity.setContent(sessionContent);
                                    sessionDbEntity.setTimeStamp(timestamp);
                                    updateParentSession(sessionDbEntity);

                                    msgDbEntity.setTimestamp(timestamp);
                                    msgDbEntity.setId(object.getMsgId());
                                    msgDbEntity.setMsgSendStatus(MsgDbEntity.MSG_STATUS_SUCCEED);
                                    realm.copyToRealmOrUpdate(msgDbEntity);
                                }
                            });
                        } //避免事务嵌套
                    }
                }
            }
        });
    }

    private void sendMessage2Server(final long time, final MessageOuterClass.Message message) {
        sendMessage2Server(time, message, null);
    }

    /**
     * 通过接口发送数据
     *
     * @param time
     * @param message
     */
    @SuppressLint("CheckResult")
    private void sendMessage2Server(final long time, final MessageOuterClass.Message message, final CommonCallBack<ProtolCallBackEntity> callBack) {
        MsgSenderHelper.sendImMessage2Server(message)
                .subscribe(new Consumer<ProtolCallBackEntity>() {
                    @Override
                    public void accept(final ProtolCallBackEntity protolCallBackEntity) {
                        if (null != callBack) {
                            callBack.onCallBack(protolCallBackEntity);
                        }
                        try (Realm realm = Realm.getDefaultInstance()) {
                            realm.executeTransactionAsync(new Realm.Transaction() {

                                @Override
                                public void execute(Realm realm) {
                                    MsgDbEntity msg = realm.where(MsgDbEntity.class)
                                            .equalTo("timestamp", time)
                                            .findFirst();
                                    if (msg != null) {
                                        msg.setMsgSendStatus(MsgDbEntity.MSG_STATUS_SUCCEED);
                                        msg.setId(protolCallBackEntity.getMsgId());
                                    }
                                }
                            });
                            // 发送成功通知
                            EventBus.getDefault().post(IMEventMsg.MSG_SEND_STATUS_SUCCESS);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        if (null != callBack) {
                            callBack.onCallBack(null);
                        }
                        try (Realm realm = Realm.getDefaultInstance()) {
                            realm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    MsgDbEntity msg = realm.where(MsgDbEntity.class)
                                            .equalTo("timestamp", time)
                                            .findFirst();
                                    if (msg != null) {
                                        msg.setMsgSendStatus(MsgDbEntity.MSG_STATUS_FAILURE);
                                    }
                                }
                            });
                        }
                        throwable.printStackTrace();
                        if (throwable instanceof ApiException) {
                            ApiException apiException = (ApiException) throwable;
                            if (apiException.getCode()
                                    == ModuleIMBusinessManager.INSTANCE.getBusinessService().getUnLoginErrCode()) {
                                ModuleIMManager.INSTANCE.getIMService().dealOffline(throwable.getMessage());
                            } else {
                                MedToastUtil.showMessage(ModuleIMBusinessManager.INSTANCE.getApplication(), throwable.getMessage());
                            }
                        } else {
                            RetrofitProvider.INSTANCE.errorHandler(throwable);
                        }
                    }
                });
    }

    public MsgDbEntity getJsonMsgDbEntity(MsgSessionDbEntity session, String json, int jsonType) {
        return getJsonMsgDbEntity(System.currentTimeMillis(), session, json, jsonType);
    }

    public MsgDbEntity getJsonMsgDbEntity(long timestamp, MsgSessionDbEntity session, String json, int jsonType) {
        MsgDbEntity msgDbEntity = new MsgDbEntity();
        msgDbEntity.setDataType(MessageOuterClass.Message.DataCase.JSON.getNumber());
        msgDbEntity.setJsonString(json);
        msgDbEntity.setJsonType(jsonType);
        packageCommonData(timestamp, msgDbEntity, session);
        return msgDbEntity;
    }

    /**
     * 拼装一些公共的数据
     *
     * @param msgDbEntity
     * @param session
     */
    private void packageCommonData(MsgDbEntity msgDbEntity, MsgSessionDbEntity session) {
        packageCommonData(System.currentTimeMillis(), msgDbEntity, session);
    }

    private void packageCommonData(long timestamp, MsgDbEntity msgDbEntity, MsgSessionDbEntity session) {
        msgDbEntity.setTimestamp(timestamp);
        msgDbEntity.setId(EntityConvertHelper.timeGetId(msgDbEntity.getTimestamp()));
        msgDbEntity.setMsgSendStatus(MsgDbEntity.MSG_STATUS_SENDING);
        msgDbEntity.setHasRead(true);
        msgDbEntity.setSessionId(session.getSessionId());
        msgDbEntity.setSelfMsg(true);
        msgDbEntity.setGroup(session.isGroup());
        try (Realm realm = Realm.getDefaultInstance()) {
            final long currentUserId = ModuleIMBusinessManager.INSTANCE.getBusinessService().getCurrentUserId();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) { //确保当前的user保存到数据库
                    ImUserDbEntity imUserDbEntity = realm.where(ImUserDbEntity.class).equalTo("id", currentUserId).findFirst();
                    imUserDbEntity = ModuleIMBusinessManager.INSTANCE.getBusinessService().transformCurrentUserDb(imUserDbEntity);
                    realm.copyToRealmOrUpdate(imUserDbEntity);
                }
            });
            msgDbEntity.setFromUser(realm.where(ImUserDbEntity.class).equalTo("id", currentUserId).findFirst());
        }
        if (msgDbEntity.isGroup()) {
            msgDbEntity.setToGroup(session.getFromGroup());
        } else {
            msgDbEntity.setToUser(session.getFromUser());
        }
        msgDbEntity.setHospitalId(session.getHospitalId());

        //这里检测消息进程是否存在，在每次发送消息的地方。
        ImManager.checkImRomoteProcessAlive();
    }

    private MsgSessionDbEntity getSessionById(String sessionId, Realm realm) {
        return EntityConvertHelper.getSessionById(sessionId, realm);
    }

}
