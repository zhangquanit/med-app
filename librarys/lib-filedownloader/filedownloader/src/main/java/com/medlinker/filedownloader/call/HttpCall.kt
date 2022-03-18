package com.medlinker.filedownloader.call

/**
 *
 * @author zhangquan
 */
enum class HttpCall {
    /**
     * OkHttp下载
     */
    OKHTTP,

    /**
     * HttpUrlConnection下载
     */
    HTTP_URL_CONNECTION
}