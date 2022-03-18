package net.medlinker.imbusiness.ui.repository;

import android.util.Base64;

import androidx.lifecycle.MutableLiveData;

import com.medlinker.network.retrofit.error.ErrorConsumer;

import net.medlinker.base.network.HttpResultFunc;
import net.medlinker.im.im.ImMsgRecivedManager;
import net.medlinker.im.realm.MsgSessionDbEntity;
import net.medlinker.im.util.SchedulersCompat;
import net.medlinker.imbusiness.entity.ListStringEntity;
import net.medlinker.imbusiness.network.ApiManager;
import net.medlinker.libhttp.BaseEntity;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;
import io.realm.Realm;

/**
 * @author hmy
 * @time 2020/9/24 19:43
 */
public class ChatSessionRepository {

    private Disposable mGetHistoryMsgDisposable;

    public boolean getHistoryMsg(MsgSessionDbEntity mSession, final int limit,
                                 final MutableLiveData<ListStringEntity> liveData) {
        if (mGetHistoryMsgDisposable != null && !mGetHistoryMsgDisposable.isDisposed()
                || null == mSession || !mSession.isValid()) {
            liveData.setValue(null);
            return false;
        }
        Observable<BaseEntity<ListStringEntity>> observable = mSession.isGroup()
                ? ApiManager.getMsgApi().getGroupHistoryMsg(mSession.getFromGroup().getId(), 1, limit,
                mSession.getStart())
                : ApiManager.getMsgApi().getSingleChatHistroyMsg(mSession.getFromUser().getId(), 1,
                mSession.getFromUser().getRefrence(), 15, mSession.getStart());
        mGetHistoryMsgDisposable = observable
                .compose(SchedulersCompat.<BaseEntity<ListStringEntity>>applyIoSchedulers())
                .map(new HttpResultFunc<ListStringEntity>())
                .filter(new Predicate<ListStringEntity>() {
                    @Override
                    public boolean test(ListStringEntity listStringEntity) throws Exception {
                        return listStringEntity != null && listStringEntity.getList() != null;
                    }
                })
                .doOnNext(new Consumer<ListStringEntity>() {
                    @Override
                    public void accept(final ListStringEntity listStringEntity) throws Exception {
                        try (Realm realm = Realm.getDefaultInstance()) {
                            realm.executeTransaction(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    for (String s : listStringEntity.getList()) {
                                        if (s == null) {
                                            continue;
                                        }
                                        byte[] decode = Base64.decode(s, Base64.NO_WRAP);
                                        ImMsgRecivedManager.INSTANCE.saveHistoryMsg(decode, realm);
                                    }
                                }
                            });
                        }
                    }
                })
                .subscribe(new Consumer<ListStringEntity>() {
                    @Override
                    public void accept(final ListStringEntity s) throws Exception {
                        if (null != liveData) {
                            liveData.setValue(s);
                        }
                    }
                }, new ErrorConsumer() {
                    @Override
                    public void accept(Throwable throwable) {
                        super.accept(throwable);
                        if (null != liveData) {
                            liveData.setValue(null);
                        }
                    }
                });
        return true;
    }

}
