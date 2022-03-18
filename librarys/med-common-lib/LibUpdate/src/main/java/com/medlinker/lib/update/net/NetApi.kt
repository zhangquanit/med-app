package com.medlinker.lib.update.net

import androidx.annotation.Keep
import com.medlinker.lib.update.bean.AppVersionEntity
import com.medlinker.lib.update.bean.AppVersionReportEntity
import com.medlinker.lib.utils.MedAppInfo
import com.medlinker.lib.utils.MedDeviceUtil
import io.reactivex.Observable
import net.medlinker.libhttp.BaseEntity
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

@Keep
interface NetApi {
    @GET("/general-interface/appVersion/v2/getVersionUpgradeInfo")
    fun getAppInfo(
        @Query("appKey") appKey: String,
        @Query("appVersion") appVersion: String,
        @Query("deviceId") deviceId: String = MedDeviceUtil.getDeviceId(MedAppInfo.appContext)
    ): Observable<BaseEntity<AppVersionEntity>>

    @POST("/general-interface/appVersion/v2/upgradeReport")
    fun upgradeReport(@Body params: HashMap<String, Any>): Observable<BaseEntity<AppVersionReportEntity>>

}