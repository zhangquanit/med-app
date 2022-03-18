package com.medlinker.protocol

import net.medlinker.base.storage.KVUtil

/**
 *
 * @author zhangquan
 */
object PrivacyUtil {
    private const val KEY_PRIVACY_PROTOCOL_STATE = "KEY_PRIVACY_PROTOCOL_STATE"

    @JvmStatic
    fun isPrivacyGranted(): Boolean {
        return KVUtil.getBoolean(KEY_PRIVACY_PROTOCOL_STATE)
    }
    @JvmStatic
    fun setPrivacyGranted(granted: Boolean) {
        KVUtil.set(KEY_PRIVACY_PROTOCOL_STATE, granted)
    }
}