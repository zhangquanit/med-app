package com.medlinker.lib.push.med

import android.util.Log
import com.medlinker.lib.utils.MedAppInfo

/**
 *
 * @author zhangquan
 */
object PushLog {
    @JvmStatic
    fun log(msg: String) {
        if (MedAppInfo.isDebug) {
            Log.d("MedPush", msg)
        }
    }
}