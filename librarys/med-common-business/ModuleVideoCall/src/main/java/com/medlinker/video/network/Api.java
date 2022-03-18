package com.medlinker.video.network;

import com.medlinker.video.entity.VideoReportEntity;
import com.medlinker.video.entity.VideoRoomEntity;

import net.medlinker.base.entity.DataEntity;
import net.medlinker.libhttp.BaseEntity;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @author hmy
 * @time 12/6/21 15:45
 */
public interface Api {

    @GET("/base-interface/video/detail")
    Observable<BaseEntity<VideoRoomEntity>> getVideoDetail(@Query("userId") String userId,
                                                           @Query("userType") long userType,
                                                           @Query("roomId") int roomId);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("/base-interface/video/event/send")
    Observable<BaseEntity<VideoReportEntity>> reportVideoSend(@Body RequestBody bytes);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("/base-interface/video/event/start")
    Observable<BaseEntity<DataEntity>> reportVideoStart(@Body RequestBody bytes);

    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @POST("/base-interface/video/event/end")
    Observable<BaseEntity<DataEntity>> reportVideoEnd(@Body RequestBody bytes);
}
