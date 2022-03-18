package net.medlinker.imbusiness.network

import androidx.annotation.Keep
import com.medlinker.baseapp.entity.DataListResponse
import io.reactivex.Observable
import net.medlinker.base.entity.DataEntity
import net.medlinker.imbusiness.entity.ImButtonEntity
import net.medlinker.imbusiness.entity.OrderListEntity
import net.medlinker.imbusiness.entity.ServicePackDoctorEntity
import net.medlinker.imbusiness.entity.doctor.DoctorConversationEntity
import net.medlinker.libhttp.BaseEntity
import net.medlinker.libhttp.host.HostName
import retrofit2.Call
import retrofit2.http.*


/**
 * @author hmy
 * @time 12/1/21 10:52
 */
@HostName("API")
@Keep
interface Api {
    /**
     * 患者端我的医生列表
     */
    @GET("/followup-interface/v1/doctor-patient/doctor_list")
    fun getDoctorList(@Query("topSectionId") topSectionId: Int): Call<BaseEntity<DataListResponse<DoctorConversationEntity>>>

    /**
     * 获取骨科app电话视频问诊显示按钮
     */
    @GET("/inquiry-interface/v1/doctor/support_inquiry")
    fun getImButtons(
        @Query("doctorId") doctorId: Long,
        @Query("channel") channel: String? = "gukeapp"
    ): Call<BaseEntity<DataListResponse<ImButtonEntity>>>

    /**
     * 订单医生列表
     */
    @GET("/packet-interface/order/doctor-list")
    @Headers("Content-Type:application/x-www-form-urlencoded")
    fun getServicePackList(
        @Query("patientId") patientId: Long,
        @Query("statuses") statuses: Int = 1,
        @Query("labels") labels: Int = 10,
        @Query("start") start: Int = 0,
        @Query("limit") limit: Int = 1000,
    ): Call<BaseEntity<DataListResponse<ServicePackDoctorEntity>>>

    /**
     * 问诊详情
     * @param transNo
     * @return
     */
    @GET("/inquiry-interface/v1/inquiry/simple_detail")
    fun getVideoInquiryDetail(@Query("transNo") transNo: String?): Observable<BaseEntity<OrderListEntity>>?

    @JvmSuppressWildcards
    @POST("/inquiry-interface/v1/inquiry/appoint")
    fun updateReservationTime(@Body map: Map<String, Any>): Observable<BaseEntity<DataEntity>>?
}