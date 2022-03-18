package com.medlinker.hybrid.core.jsbridge.entity

import com.medlinker.bridge.BridgeErrorCode


/**
 * @author hmy
 * @time 3/16/21 15:56
 */
class WVHybridCallbackEntity(
        var code: Int = BridgeErrorCode.SUCCESS,
        var message: String = "",
        var data: Any? = null) {

    fun setData(data: Any?): WVHybridCallbackEntity {
        this.data = data
        return this
    }
}