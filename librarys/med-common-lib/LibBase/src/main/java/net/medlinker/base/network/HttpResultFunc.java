package net.medlinker.base.network;

import android.os.Parcel;

import com.medlinker.network.retrofit.RetrofitProvider;

import net.medlinker.base.entity.DataEntity;
import net.medlinker.libhttp.BaseEntity;

import io.reactivex.functions.Function;

/**
 * @param <T>
 * @author hmy
 */
public class HttpResultFunc<T extends DataEntity> implements Function<BaseEntity<T>, T> {
    @Override
    public T apply(BaseEntity<T> httpResult) throws Exception {
        RetrofitProvider.INSTANCE.checkResponse(httpResult.errcode, httpResult.errmsg, httpResult);
        return httpResult.data == null ? (T) T.CREATOR.createFromParcel(Parcel.obtain()) : httpResult.data;
    }
}
