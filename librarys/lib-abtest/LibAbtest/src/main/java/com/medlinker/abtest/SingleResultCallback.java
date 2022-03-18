package com.medlinker.abtest;


public interface SingleResultCallback {
    void onSuccess(ABTestModel aBTestResult);

    void onError(String errMsg);
}