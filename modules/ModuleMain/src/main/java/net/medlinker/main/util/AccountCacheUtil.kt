package net.medlinker.main.util

import net.medlinker.base.account.AccountUtil

/**
 *
 * @author zhangquan
 */
object AccountCacheUtil {

    fun clearCache() {
        //清除用户缓存
        AccountUtil.clear()
    }
}