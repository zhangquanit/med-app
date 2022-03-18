package com.medlinker.dt.test

import android.app.Application
import android.util.Log
import com.didichuxing.doraemonkit.kit.AbstractKit
import com.medlinker.debugtools.DTModule
import com.medlinker.debugtools.config.DTConfig
import com.medlinker.debugtools.config.DTLaneConfig
import com.medlinker.debugtools.config.DTRouterConfig
import com.medlinker.debugtools.config.IDTCustomConfig
import com.medlinker.debugtools.manager.DTOkHttpManager
import com.medlinker.dt.test.interceptor.LaneMockDataInterceptor
import com.medlinker.dt.test.interceptor.OkHttpLogInterceptor
import com.medlinker.dt.test.interceptor.ParaInterceptor

class DTHelper {
    companion object {
        private const val AUTHORIZATION = "Basic bWVkbGlua2VyOm1lZDEyMzQ1Ng=="
        private const val TAG = "DTHelper"

        fun init(application: Application?){
            DTModule.init(application,CUSTOM_CONFIG)
            initLaneConfig(application)
            initGlobalOkHttpInterceptors()
        }

        private fun initLaneConfig(application: Application?) {

            DTConfig.instance()
                .initLaneConfig(DTLaneConfig()
                    .authorization(AUTHORIZATION)
                    .laneAuthority(DEFAULT_AUTHORITY_DOMAINS)
                    .onlineDomains(DEFAULT_ONLINE_DOMAINS)
                    .callBack {
                        Log.d(TAG,"name = ${it.laneName},domains = ${it.laneDomains}")
                    })
                .initRouterConfig(DTRouterConfig()
                    .routerConfig {
                        Log.d(TAG,"url = $it")
                    })
        }

       private val CUSTOM_CONFIG = IDTCustomConfig { mutableListOf() }

        private val DEFAULT_AUTHORITY_DOMAINS = listOf(
                "app-qa.medlinker.com",
                "doctor-app-qa.medlinker.com",
                "d2d-qa.medlinker.com",
                "d2d-ph-qa.medlinker.com",
                "web-qa.medlinker.com",
                "rn-qa.medlinker.com",
                "wzapi-qa.medlinker.com",
                "inquiry-web-qa.medlinker.com",
                "im-api-qa.medlinker.com",
                "passport-qa.medlinker.com",
                "im-wss-qa.medlinker.com/gate",
                "api-qa.medlinker.com",
                "data-bi.qa.medlinker.com",
                "quiz-mini-svr-qa.medlinker.com",
                "med-pay-qa.medlinker.com",
                "m-qa.medlinker.com",
                "med-files-qa.medlinker.com")

        private val DEFAULT_ONLINE_DOMAINS = hashMapOf(
                "app-qa.medlinker.com" to "@app.medlinker.com",
                "doctor-app-qa.medlinker.com" to "@doctor-app.medlinker.com",
                "d2d-qa.medlinker.com" to "@ylt.medlinker.com",
                "d2d-ph-qa.medlinker.com" to "@ph.medlinker.com",
                "inquiry-web-qa.medlinker.com" to "@inquiry.medlinker.com",
                "wzapi-qa.medlinker.com" to "@wzapi.medlinker.com",
                "inquiry-web-qa.medlinker.com" to "@inquiry.medlinker.com",
                "im-api-qa.medlinker.com" to "@im-api.medlinker.com",
                "passport-qa.medlinker.com" to "@passport.medlinker.com",
                "im-wss-qa.medlinker.com" to "@im-gateway.medlinker.com",
                "data-bi.qa.medlinker.com" to "data.medlinker.com",
                "upgrade.qa.medlinker.com/version" to "upgrade.medlinker.com/version",
                //
                "web-qa.medlinker.com" to "@web.medlinker.com",
                "qa-pay.medlinker.com" to "@pay.medlinker.com",
                "47.110.164.227" to "@msg.medlinker.com",
                "47.110.164.227:9988" to "@idcard.medlinker.com",
                "svr.cms.pdt5.medlinker.com" to "@svr.cms.medlinker.com",
                "h5.qa.medlinker.com" to "h5.medlinker.com",
                //
                "api-qa.medlinker.com" to "@api.medlinker.com",
                "quiz-mini-svr-qa.medlinker.com" to "@quiz-mini-svr.medlinker.com",
                "med-pay-qa.medlinker.com" to "@med-pay.medlinker.com",
                "m-qa.medlinker.com" to "@m.medlinker.com",
                "med-files-qa.medlinker.com" to "@med-files.medlinker.com")

        private fun initGlobalOkHttpInterceptors(){
            DTOkHttpManager.instance().addGlobalInterceptors(ParaInterceptor())
            DTOkHttpManager.instance().addGlobalNetworkInterceptors(OkHttpLogInterceptor())
        }
    }
}