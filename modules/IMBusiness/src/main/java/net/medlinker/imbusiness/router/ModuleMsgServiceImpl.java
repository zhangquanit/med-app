package net.medlinker.imbusiness.router;

import android.util.Base64;

import com.medlinker.lib.utils.MedToastUtil;
import com.medlinker.network.retrofit.error.ApiException;
import com.medlinker.protocol.message.MessageOuterClass;

import net.medlinker.base.account.AccountUtil;
import net.medlinker.base.account.UserInfo;
import net.medlinker.base.entity.DataEntity;
import net.medlinker.base.network.HttpResultFunc;
import net.medlinker.im.MsgConstants;
import net.medlinker.im.entity.UserInfoEntity;
import net.medlinker.im.im.ImMsgRecivedManager;
import net.medlinker.im.im.util.LogUtil;
import net.medlinker.im.realm.MsgSessionDbEntity;
import net.medlinker.im.router.ModuleIMManager;
import net.medlinker.im.router.ModuleMsgService;
import net.medlinker.im.util.SchedulersCompat;
import net.medlinker.imbusiness.IMGlobalManager;
import net.medlinker.imbusiness.entity.ListStringEntity;
import net.medlinker.imbusiness.msg.MsgJsonConstants;
import net.medlinker.imbusiness.network.ApiManager;
import net.medlinker.imbusiness.util.MsgSenderHelper;
import net.medlinker.libhttp.BaseEntity;

import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

/**
 * @author hmy
 * @time 2020/9/22 19:01
 */
public class ModuleMsgServiceImpl implements ModuleMsgService {

    private static final String TAG = "ModuleMsgServiceImpl";

    @Override
    public void doMsgReceipt(long msgId) {
        Disposable disposable = ApiManager.getMsgApi().msgReceipt(msgId)
                .compose(SchedulersCompat.<BaseEntity<DataEntity>>applyIoSchedulers())
                .delay(300, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<BaseEntity<DataEntity>>() {
                    @Override
                    public void accept(BaseEntity<DataEntity> dataEntityBaseEntity) throws Exception {
                        LogUtil.i(TAG, "????????????????????????");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    @Override
    public void onExitGroup(long groupId) {
        //TODO       EventBusUtils.post(UserEvent.EXIT_GROUP, groupId);
    }

    @Override
    public void doSomeMsgNotify(int notifyType, boolean isHistory) {
        //TODO ??????im notify??????
    }

    @Override
    public void onImInit(BiConsumer<String, Integer> callBack, Consumer<Throwable> throwableAction) {
        MsgSenderHelper.getHostPort(callBack, throwableAction);
    }

    @Override
    public void bindUserTokenForIM(String token) {
        MsgSenderHelper.bindUserToken(token);
    }

    private Disposable mGetOfflineMsgDisposable;

    @Override
    public void getOfflineMsg() {
        //?????????????????????????????????????????????????????????
        if (ModuleIMManager.INSTANCE.getIMService().isVisitor()
                || null != mGetOfflineMsgDisposable && !mGetOfflineMsgDisposable.isDisposed()) {
            return;
        }
// TODO       EventBusUtils.post(UserEvent.MSG_GET_MSG_SHOW);
        mGetOfflineMsgDisposable = ApiManager.getMsgApi().getOfflineMsg()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new HttpResultFunc<ListStringEntity>())
                .filter(new Predicate<ListStringEntity>() {
                    @Override
                    public boolean test(ListStringEntity listStringEntity) throws Exception {
                        return listStringEntity.getList() != null;
                    }
                })
                .subscribe(new Consumer<ListStringEntity>() {
                    @Override
                    public void accept(final ListStringEntity listStringEntity) throws Exception {
                        try (Realm realm = Realm.getDefaultInstance()) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    for (String msg : listStringEntity.getList()) {
                                        byte[] decode = Base64.decode(msg, Base64.NO_WRAP);
                                        ImMsgRecivedManager.INSTANCE.saveMessageData(decode, true, realm);
                                    }
                                }
                            });
                        }
                        ModuleIMManager.INSTANCE.getIMService().onMsgUnreadCountChanged();
//       TODO                 EventBusUtils.post(UserEvent.MSG_GET_MSG_HIDE);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (throwable instanceof ApiException) {
                            MedToastUtil.showMessage(ModuleIMBusinessManager.INSTANCE.getApplication(), throwable.getMessage());
                        }
                        throwable.printStackTrace();
//       TODO                 EventBusUtils.post(UserEvent.MSG_GET_MSG_HIDE);
                    }
                });

    }

    @Override
    public void notifyMsg(MsgSessionDbEntity entity) {
        IMGlobalManager.INSTANCE.notifyMsg(entity);
    }

    @Override
    public UserInfoEntity getCurrentUser() {
        UserInfo userInfo = AccountUtil.INSTANCE.getUserInfo();
        if (userInfo == null) {
            userInfo = new UserInfo();
        }
        return new UserInfoEntity(userInfo.getId(), userInfo.getName(), userInfo.getAvatar(), MsgConstants.CHAT_USER_TYPE_PATIENT);
    }

    @Override
    public boolean isInterceptMsg(MessageOuterClass.Message message) {
        // ????????????IM??????
        return false;
    }

    @Override
    public String buildJsonMsgContent(MessageOuterClass.Message message) {
        int jsonType = message.getJson().getType();
        if (jsonType == MsgJsonConstants.MSG_TYPE_JSON_SPORTS_PRESCRIPTION) {
            return "[????????????]";
//        } else if (jsonType == MsgJsonConstants.MSG_TYPE_JSON_EVALUATION_COGNITIVE_DISORDER) {
//            return "[??????????????????]";
//        } else if (jsonType == MsgJsonConstants.MSG_TYPE_JSON_REVIEW_PRESCRIPTION_NEW) {
//            return "[???????????????]";
        } else if (jsonType == MsgJsonConstants.MSG_TYPE_JSON_SERVICE_PACK) {
            return "[?????????]";
//        } else if (jsonType == MsgJsonConstants.MSG_TYPE_JSON_PRESCRIPTION_NEW
//                || jsonType == MsgJsonConstants.MSG_TYPE_JSON_PRESCRIPTION_CHINESE) {
//            return "[???????????????]";
        } else if (jsonType == MsgJsonConstants.MSG_TYPE_JSON_RESERVATION_CALL) {
            return "[??????????????????]";
        } else if (jsonType == MsgJsonConstants.MSG_TYPE_JSON_RESERVATION_VIDEO) {
            return "[??????????????????]";
        } else if (jsonType == MsgJsonConstants.MSG_TYPE_JSON_VIDEO_INVITE) {
            return "[??????????????????]";
//        } else if (jsonType == MsgJsonConstants.MSG_TYPE_JSON_USE_WESTERN_MEDICAL_ADVICE
//                || jsonType == MsgJsonConstants.MSG_TYPE_JSON_USE_CHINA_MEDICAL_ADVICE) {
//            return "[????????????]";
//        } else if (jsonType == MsgJsonConstants.MSG_TYPE_JSON_PRESCRIBE_MEDICINE) {
//            return "[????????????]";
        }
        return "";
    }

//    @Override
//    public void init(Context context) {
//    }
}
