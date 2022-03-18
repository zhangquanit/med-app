package com.medlinker.dt.test.interceptor

import okhttp3.*
import java.io.IOException

class LaneMockDataInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response? {
        return if (chain.request().url().toString().contains("/project/app-domains")) {
            Response.Builder()
                    .code(200)
                    .addHeader("Content-Type", "application/json")
                    .body(ResponseBody.create(MediaType.parse("application/json"), FAKE_STR))
                    .message("fake data")
                    .request(chain.request())
                    .protocol(Protocol.HTTP_2)
                    .build()
        } else {
            chain.proceed(chain.request())
        }
    }

    companion object {
        const val FAKE_STR = "{\"errcode\":0,\"errmsg\":\"OK\",\"data\":{\"app-qa.medlinker.com\":\"@app.medlinker.com\"}}"
    }
}