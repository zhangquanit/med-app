package com.medlinker.bridge


/**
 * @author hmy
 * @time 4/28/21 16:25
 */
interface BridgeCallback<T> {
    fun onCallback(data: T?)
}