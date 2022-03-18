package com.medlinker.filedownloader.db

import androidx.annotation.Keep

/**
 * @author zhangquan
 */
@Keep
object ModelColumn {

    /**
     * 下载路径
     */
    const val url = "url"

    /**
     * 父目录
     */
    const val parentFile = "parentFile"

    /**
     * 文件名
     */
    const val fileName = "fileName"

    /**
     * 文件大小
     */
    const val total = "total"

}