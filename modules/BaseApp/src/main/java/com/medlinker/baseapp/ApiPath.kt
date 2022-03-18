package com.medlinker.baseapp

import net.medlinker.libhttp.host.HostManager

object ApiPath {
    private var BASE_H5_PATH = getH5Host()

    /**
     * 隐私协议
     */
    @JvmField
    var APP_PRIVACY_STATEMENT_URL = "$BASE_H5_PATH/ih-v2/protocol/orthopaedics-app-privacy"

    /**
     * 用户协议
     */
    @JvmField
    var USER_AGREEMENT_URL = "$BASE_H5_PATH/ih-v2/protocol/orthopaedics-app-agreement"

    /**
     * 账户注销
     */
    @JvmField
    var CANCEL_ACCOUNT_URL = "$BASE_H5_PATH/ih-v2/withdraw/reason"


    fun getH5Host(): String {
        return getH5HostUrl()
    }

    private fun getH5HostUrl(): String {
        return HostManager.getHost("H5")
    }
}