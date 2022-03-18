package com.medlinker.abtest;

import java.util.List;

public interface ResultCallback {
    void onSuccess(List<ABTestModel> aBTestResultList);

    void onError(String errMsg);
}
