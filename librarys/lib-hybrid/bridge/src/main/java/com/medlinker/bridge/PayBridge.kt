package com.medlinker.bridge


/**
 * @author hmy
 * @time 12/10/21 10:16
 */
abstract class PayBridge {
    /**
     * 启动支付宝支付失败后调用
     */
    abstract fun payInterceptorWithUrlByAliPay(url: String?, callback: BridgeCallback<Void>)

    open fun getWeiXinPayReferer(): String {
        return "https://web.medlinker.com"
    }
}