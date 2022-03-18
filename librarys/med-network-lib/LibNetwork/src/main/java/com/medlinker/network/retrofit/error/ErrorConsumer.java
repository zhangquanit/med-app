package com.medlinker.network.retrofit.error;

import com.medlinker.network.retrofit.RetrofitProvider;

import io.reactivex.functions.Consumer;

/**
 * @author hmy
 * @time 2021/1/5 16:00
 */
public class ErrorConsumer implements Consumer<Throwable> {
    @Override
    public void accept(Throwable throwable) {
        try {
            ErrorUtil.errorHandler(throwable);
        } catch (Exception e) {
            RetrofitProvider.INSTANCE.getNetworkConfig().showToast(e.getMessage());
            e.printStackTrace();
        }
    }
}
