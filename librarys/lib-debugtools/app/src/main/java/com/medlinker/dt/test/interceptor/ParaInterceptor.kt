package com.medlinker.dt.test.interceptor

import android.os.Build
import com.medlinker.dt.test.BuildConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class ParaInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response? {
        return chain.proceed(addDefaultParams(chain.request()))
    }

    private fun addDefaultParams(request: Request): Request {
        val builder = request.url().newBuilder()
                .addQueryParameter("sys_p", "a")
                .addQueryParameter("x_platform", "app")
                .addQueryParameter("sys_m", Build.MODEL)
                .addQueryParameter("sys_v", Build.VERSION.RELEASE)
                .addQueryParameter("sys_vc", Build.VERSION.SDK_INT.toString())
                .addQueryParameter("cli_c", "medlinker")
                .addQueryParameter("sys_d", "13ifhioajqrioewjwtioansdda")
                .addQueryParameter("sys_dc", "mi8") //手机设备号（在7.6.4版本添加改字段）
                .addQueryParameter("sys_pkg", "net.medlinker.medlinker")
        val httpUrl = builder.build()
        return request.newBuilder().url(httpUrl)
                .build()
    }
}