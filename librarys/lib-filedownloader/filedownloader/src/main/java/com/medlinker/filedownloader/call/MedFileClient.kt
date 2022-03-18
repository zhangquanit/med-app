package com.medlinker.filedownloader.call

import okhttp3.OkHttpClient
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 *
 * @author zhangquan
 */
object MedFileClient {
    private var httpClient: OkHttpClient? = null
    private var pool: ThreadPoolExecutor? = null
    fun getOKHttpClient(): OkHttpClient {
        if (null == httpClient) {
            httpClient = OkHttpClient.Builder()
                .connectTimeout(30_000, TimeUnit.MILLISECONDS)
                .readTimeout(60_000, TimeUnit.MILLISECONDS)
                .writeTimeout(60_000, TimeUnit.MILLISECONDS)
                .build()
        }
        return httpClient!!
    }

    fun getThreadPool(): ThreadPoolExecutor {
        if (null == pool) {
            pool = ThreadPoolExecutor(
                7, 10,
                500, TimeUnit.MILLISECONDS,
                LinkedBlockingQueue()
            );
        }
        return pool!!
    }
}