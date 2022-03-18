package com.medlinker.dt.test.act

import android.os.Bundle
import android.util.Log
import android.view.View
import com.didichuxing.doraemonkit.kit.loginfo.LogInfoManager
import com.medlinker.debugtools.`fun`.lane.DTLaneStorage
import com.medlinker.debugtools.base.DTBaseActivity
import com.medlinker.debugtools.manager.DTOkHttpManager
import com.medlinker.dt.test.R
import com.medlinker.dt.test.interceptor.LaneMockDataInterceptor
import okhttp3.*
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class DTLaneTestActivity : DTBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lane_test)
        DTOkHttpManager.instance().addGlobalInterceptors(LaneMockDataInterceptor())
        Log.d(TAG,"lane data = " + DTLaneStorage.getLaneDomains())
    }

    fun laneTest(view: View) {
        showDialogLoading(true)
        val client = OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS) //对象的创建
                .build()

        val requestBuild = Request.Builder()

        val urlBuilder = Objects
                .requireNonNull(HttpUrl.parse("http://app-qa.medlinker.com/rest/v1" + "/users/login"))
                ?.newBuilder()
                ?: return

        urlBuilder.addQueryParameter("username", "18228536817")
        urlBuilder.addQueryParameter("password", "654321")
        requestBuild.url(urlBuilder.build())
        val mCall = client.newCall(requestBuild.build())
        mCall.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    hideDialogLoading()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    hideDialogLoading()
                }
            }
        })
    }

    override fun onStop() {
        super.onStop()
        LogInfoManager.getInstance().stop()
        DTOkHttpManager.instance().removeGlobalInterceptors(LaneMockDataInterceptor.javaClass.simpleName)
    }
    companion object{
        private const val TAG = "DTLaneTestActivity"
    }
}