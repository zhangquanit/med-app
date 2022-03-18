package com.medlinker.lib.fileupload.network;

import com.medlinker.lib.fileupload.MedFileUpload;
import com.medlinker.lib.utils.MedAppInfo;
import com.medlinker.network.retrofit.RetrofitProvider;

public class FileUploadApi {
    private static IFileUploadApi fileUploadApi;

    public static IFileUploadApi getFileUploadApi() {
        if (fileUploadApi == null) {
            fileUploadApi = RetrofitProvider.INSTANCE.build(RetrofitProvider.INSTANCE.buildRetrofit(getBaseUrl()), IFileUploadApi.class);
        }
        return fileUploadApi;
    }

    private static String getBaseUrl() {
        if (MedAppInfo.isDebug())
            return "https://app-qa.medlinker.com";
        else
            return "https://app.medlinker.com";
    }
}
