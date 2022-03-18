package com.medlinker.login.net

import androidx.annotation.Keep
import net.medlinker.base.account.LoginInfo
import net.medlinker.base.account.UserInfo
import net.medlinker.libhttp.BaseEntity
import net.medlinker.libhttp.host.HostName
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

@HostName("API")
@Keep
interface AccountApi {
    //发送验证码
    @POST("/passport-interface/appPatient/verifyCode/get")
    fun getVerifyCode(@Body params: HashMap<String, Any>): Call<BaseEntity<Void>>

    //退出登陆
    @POST("/passport-interface/appPatient/logout")
    fun logout():  Call<BaseEntity<Void>>

    //手机号一键登陆
    @POST("/passport-interface/appPatient/login/flash")
    fun loginWithSy(@Body params: HashMap<String, Any>): Call<BaseEntity<LoginInfo>>

    //手机号+验证码登陆
    @POST("/passport-interface/appPatient/login/verifyCode")
    fun loginWithCode(@Body params: HashMap<String, Any>): Call<BaseEntity<LoginInfo>>

    //获取用户信息
    @GET("/user-interface/app_patient/profile")
    fun fetchUserInfo(): Call<BaseEntity<UserInfo>>

    //修改用户信息
    @POST("/user-interface/app_patient/profile")
    fun modifyUserInfo(@Body params: HashMap<String, Any>): Call<BaseEntity<Void>>
}