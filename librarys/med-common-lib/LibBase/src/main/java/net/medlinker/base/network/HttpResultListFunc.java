package net.medlinker.base.network;

import com.medlinker.network.retrofit.RetrofitProvider;

import net.medlinker.base.entity.BaseListEntity;
import net.medlinker.base.entity.DataEntity;
import net.medlinker.base.entity.DataWraperEntity;

import io.reactivex.functions.Function;

public class HttpResultListFunc<T extends DataEntity> implements Function<BaseListEntity<T>, DataWraperEntity<T>> {

    @Override
    public DataWraperEntity<T> apply(BaseListEntity<T> httpResult) {
        RetrofitProvider.INSTANCE.checkResponse(httpResult.code, httpResult.msg, httpResult);
        return httpResult.data;
    }

}
