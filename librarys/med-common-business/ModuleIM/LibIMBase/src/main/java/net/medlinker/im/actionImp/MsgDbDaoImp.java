package net.medlinker.im.actionImp;

import android.annotation.SuppressLint;

import net.medlinker.im.action.IMsgDbDao;
import net.medlinker.im.realm.MsgDbEntity;

import io.reactivex.functions.Consumer;
import io.reactivex.internal.operators.observable.ObservableFromIterable;
import io.reactivex.plugins.RxJavaPlugins;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @author hmy
 * @time 2020/9/22 15:53
 */
public class MsgDbDaoImp implements IMsgDbDao {

    @SuppressLint("CheckResult")
    @Override
    public void setHasReadAll(String sessionId, Realm realm) {
        RealmResults<MsgDbEntity> data = realm.where(MsgDbEntity.class)
                .equalTo("sessionId", sessionId)
                .equalTo("hasRead", false).findAll();
        RxJavaPlugins.onAssembly(new ObservableFromIterable<>(data)).subscribe(new Consumer<MsgDbEntity>() {
            @Override
            public void accept(MsgDbEntity msgDbEntity) throws Exception {
                msgDbEntity.setHasRead(true);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                throwable.printStackTrace();
            }
        });
    }

    @Override
    public void deleteMsgByTimeStamp(final long timestamp) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.where(MsgDbEntity.class).equalTo("timestamp", timestamp).findAll().deleteAllFromRealm();
                }
            });
        }
    }
}
