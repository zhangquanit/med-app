package com.medlinker.protocol

import androidx.annotation.Keep
import net.medlinker.libhttp.BaseEntity
import net.medlinker.libhttp.host.HostName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST


/**
 * @author hmy
 * @time 2/15/22 10:17
 */
@HostName("API")
@Keep
interface PrivacyApi {

    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("/user-interface/user/agreement")
    fun agreement(
        @Body params: PrivacyParam,
    ): Call<BaseEntity<PrivacyEntity>>

}