package net.medlinker.base.account

import androidx.annotation.Keep

/**
 * 重新登录事件
 * @author zhangquan
 */
@Keep
data class ReLoginEvent(var code: Int, var msg: String)
