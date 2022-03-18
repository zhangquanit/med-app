package net.medlinker.imbusiness.network;

import net.medlinker.base.entity.DataEntity;
import net.medlinker.imbusiness.entity.VideoParamEntity;
import net.medlinker.libhttp.BaseEntity;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @author hmy
 * @time 2020/10/12 19:35
 */
public interface InquiryWebApi {

    @POST("patient-app/inquiry/appoint")
    @FormUrlEncoded
    Observable<BaseEntity<DataEntity>> updateReservationTime(@Field("appointStartTime") long appointStartTime,
                                                             @Field("transNo") String transNo);

    /* 视频前置信息
     *
     * @param transNo
     * @return
     */
    @GET("/patient-app/video/detail")
    Observable<BaseEntity<VideoParamEntity>> getVideoParam(@Query("transNo") String transNo);
}
