package com.medlinker.baseapp

import net.medlinker.libhttp.host.Host
import net.medlinker.libhttp.host.HostManager

object AppClient {
    @JvmField
    var clientName: String? = null

    @JvmStatic
    fun init(name: String) {
        clientName = name
        initHost()
    }

    private fun initHost() {
        val hosts = mapOf<String, Host>(
            //图形验证码
            "CAPTCHA" to Host(
                "https://med-captcha.medlinker.com",
                "https://med-captcha-qa.medlinker.com",
                "https://med-captcha-pre.medlinker.com"
            ),
            //h5页面地址
            "H5" to Host(
                "https://web.medlinker.com",
                "http://web-qa.medlinker.com",
                "http://web-pre.medlinker.com"
            ),
            //大仓
            "API" to Host(
                "https://api.medlinker.com",
                "https://api-qa.medlinker.com",
                "https://api-pre.medlinker.com"
            ),
            //量表
            "PATIENT-MEDICATION" to Host(
                "https://patient-medication.medlinker.com",
                "https://patient-medication-qa.medlinker.com",
                "https://patient-medication-pre.medlinker.com"
            ),
            //
            "APP" to Host(
                "https://app.medlinker.com",
                "https://app-qa.medlinker.com",
                "https://app-pre.medlinker.com"
            ),
            "D2D-PH" to Host(
                "https://ph.medlinker.com",
                "https://d2d-ph-qa.medlinker.com",
                "https://ph-pre.medlinker.com"
            ),
            //
            "IM" to Host(
                "https://im-api.medlinker.com",
                "http://im-api-qa.medlinker.com",
                "https://im-api-pre.medlinker.com"
            ),

            )
        HostManager.addHosts(hosts)
    }
}