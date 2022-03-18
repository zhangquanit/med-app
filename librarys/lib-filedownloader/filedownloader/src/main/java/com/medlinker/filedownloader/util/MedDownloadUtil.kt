package com.medlinker.filedownloader.util

import com.medlinker.lib.log.LogUtil


/**
 *
 * @author zhangquan
 */
object MedDownloadUtil {
    const val tag = "MedFileDownloder"

    fun log(msg: String) {
        LogUtil.d(tag, msg)
    }
}