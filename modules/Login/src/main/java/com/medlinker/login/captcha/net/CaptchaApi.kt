package com.medlinker.login.captcha.net

import androidx.annotation.Keep
import com.medlinker.login.bean.SlideEntity
import net.medlinker.libhttp.BaseEntity
import net.medlinker.libhttp.host.HostName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

@HostName("CAPTCHA")
@Keep
interface CaptchaApi {
    //获取滑块验证图片
    @POST("/captcha/get")
    fun getSlideImage(@Body params: HashMap<String, Any>): Call<BaseEntity<com.medlinker.login.bean.SlideEntity>>

    //校验滑块验证结果
    @POST("/captcha/check")
    fun slideCheck(@Body params: HashMap<String, Any>): Call<BaseEntity<com.medlinker.login.bean.SlideEntity>>
}