package com.medlinker.lib.update.net

import com.medlinker.lib.update.net.download.ApkDownloadApi
import com.medlinker.lib.utils.MedAppInfo
import com.medlinker.network.retrofit.RetrofitProvider

class ApiManager {
    companion object {
        private lateinit var mNetApi: NetApi
        fun getNetApi(host: String): NetApi {
            ApkDownloadApi.init(MedAppInfo.appContext)
            if (!::mNetApi.isInitialized) {
                mNetApi = RetrofitProvider.INSTANCE.buildRetrofit(host).create(NetApi::class.java)
            }
            return mNetApi
        }
    }
}