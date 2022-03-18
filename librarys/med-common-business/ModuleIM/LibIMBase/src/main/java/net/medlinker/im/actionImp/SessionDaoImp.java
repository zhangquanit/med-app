package net.medlinker.im.actionImp;

import android.annotation.SuppressLint;

import net.medlinker.im.action.ISessionDbDao;
import net.medlinker.im.helper.EntityConvertHelper;
import net.medlinker.im.helper.MsgDbManager;
import net.medlinker.im.realm.MsgDbEntity;
import net.medlinker.im.realm.MsgSessionDbEntity;
import net.medlinker.im.router.ModuleIMManager;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * @author hmy
 * @time 2020/9/22 15:51
 */
public class SessionDaoImp implements ISessionDbDao {
    @Override
    public MsgSessionDbEntity getSessionById(String sessionId, Realm realm) {
        return EntityConvertHelper.getSessionById(sessionId, realm);
    }

    @Override
    public void insertSessionDraft(final String sessionId, final CharSequence text) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    final MsgSessionDbEntity sessionById = getSessionById(sessionId, realm);
                    if (sessionById != null) {
                        sessionById.setDraft(text.toString());
//                        sessionById.setDraftAtUsers(
//                                new Gson().toJson(LinkUtil.getAtMembersList(text.toString())));
                    }
                }
            });
        }
    }

    @Override
    public RealmResults<MsgDbEntity> getChatMsgData(Realm realm, String sessionId) {
        try {
            return realm.where(MsgDbEntity.class)
                    .equalTo("sessionId", sessionId)
                    .sort("id", Sort.ASCENDING).findAll();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void deleteMsgBySessionId(final String sessionId, final boolean resetContent) {
        Observable.just(0)
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) {
                        try (Realm realm = Realm.getDefaultInstance()) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.where(MsgDbEntity.class).equalTo("sessionId", sessionId)
                                            .findAll().deleteAllFromRealm();
                                    if (resetContent) {
                                        getSessionById(sessionId, realm).setContent("");
                                    }
                                }
                            });
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    @Override
    public void clearUnreadCount(MsgSessionDbEntity sessionDbEntity, Realm realm) {
        if (sessionDbEntity == null || !sessionDbEntity.isValid()) {
            return;
        }
        sessionDbEntity.setUnreadMsgCount(0);
        sessionDbEntity.setHaveAt(false);
        MsgDbManager.INSTANCE.getMsgDbDao().setHasReadAll(sessionDbEntity.getSessionId(), realm);
        ModuleIMManager.INSTANCE.getIMService().onMsgUnreadCountChanged();
    }
}
