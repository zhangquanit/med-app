package com.medlinker.video.network;

import com.medlinker.lib.utils.MedApiConstants;
import com.medlinker.lib.utils.MedAppInfo;
import com.medlinker.network.retrofit.RetrofitProvider;

/**
 * @author hmy
 */
public enum ApiManager {

    INSTANCE;

    private Api mApi;

    public Api getApi() {
        if (mApi == null) {
            mApi = RetrofitProvider.INSTANCE
                    .build(RetrofitProvider.INSTANCE.buildRetrofit(getApiUrl()), Api.class);
        }
        return mApi;
    }

    private String getApiUrl() {
        String url = "https://api-qa.medlinker.com";
        switch (MedAppInfo.getEnvType()) {
            case MedApiConstants.API_TYPE_QA:
                url = "https://api-qa.medlinker.com";
                break;
            case MedApiConstants.API_TYPE_ONLINE:
                url = "https://api.medlinker.com";
                break;
        }
        return url;
    }
}
