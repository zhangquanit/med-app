package com.medlinker.common.router.module;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;

import retrofit2.Retrofit;

/**
 * @author hmy
 */
public interface MainNetDelegateService extends IProvider {

    void initSomeConfig(RxAppCompatActivity activity);

    void getSomeNetData(RxAppCompatActivity activity);

    void checkShowDialog(RxAppCompatActivity activity);

    void updateUserInfo(RxAppCompatActivity activity);

    Retrofit getDoctorAppRetrofit();
}
