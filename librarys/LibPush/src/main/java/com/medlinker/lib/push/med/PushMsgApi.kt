package com.medlinker.lib.push.med

import androidx.annotation.Keep
import io.reactivex.Observable
import net.medlinker.libhttp.BaseEntity
import net.medlinker.libhttp.host.HostName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

/**
 *
 * @author zhangquan
 */
@HostName("API")
@Keep
interface PushMsgApi {

    @POST("/push-interface/device/register")
    fun bindClientId(@Body params: HashMap<String, Any>): Call<BaseEntity<Any>>
}
