package net.medlinker.imbusiness.network;

import net.medlinker.base.entity.BaseListEntity;
import net.medlinker.base.entity.DataEntity;
import net.medlinker.imbusiness.entity.ImMenuEntity;
import net.medlinker.imbusiness.entity.ListStringEntity;
import net.medlinker.imbusiness.entity.MapEntity;
import net.medlinker.imbusiness.entity.ProtolCallBackEntity;
import net.medlinker.libhttp.BaseEntity;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * @author hmy
 * @time 2020/9/23 15:25
 */
public interface MsgApi {
    /**
     * im请求参数
     *
     * @param bytes
     * @return
     */
    @POST("/message")
    Observable<BaseEntity<ProtolCallBackEntity>> sendImMessage2Server(@Body RequestBody bytes);

    /**
     * 获取im分配的端口和地址接口
     *
     * @param id
     * @return
     */
//    @GET("http://114.55.3.97:8123")
//    @GET("https://im-gateway.medlinker.com")
    @GET
    Observable<BaseEntity<ProtolCallBackEntity>> getPort(@Url String url, @Query("id") long id);

    /**
     * 获取离线消息
     *
     * @return
     */
    @GET("/offline")
    Observable<BaseEntity<ListStringEntity>> getOfflineMsg();

    /**
     * 消息回执
     *
     * @return
     */
    @GET("/receipt")
    Observable<BaseEntity<DataEntity>> msgReceipt(@Query("id") long msgId);

    /**
     * 获取群会话的历史记录
     *
     * @param groupId
     * @return
     */
    @GET("/message/history")
    Observable<BaseEntity<ListStringEntity>> getGroupHistoryMsg(@Query("groupId") long groupId, @Query("sort") long sort, @Query("limit") int limit, @Query("start") int start);


    /**
     * * 获取单聊历史记录
     *
     * @param toId
     * @param toRef
     * @param limit
     * @param start
     * @return
     */
    @GET("/message/history")
    Observable<BaseEntity<ListStringEntity>> getSingleChatHistroyMsg(@Query("toId") long toId, @Query("sort") long sort,
                                                                     @Query("toRef") long toRef, @Query("limit") int limit, @Query("start") int start);

    /**
     * 获取历史会话记录
     *
     * @return
     */
    @GET("/message/chat-list")
    Observable<MapEntity> getHistorySession();

    /**
     * 获取IM动态菜单
     *
     * @param type 1:随诊 2:问诊 3:开药门诊
     * @return
     */
    @GET("/get-im-menu")
    Observable<BaseListEntity<ImMenuEntity>> getImMenuList(@Query("type") int type,
                                                           @Query("patient_id") long patientId,
                                                           @Query("group_id") long groupId,
                                                           @Query("rn_version") int rnVersion,
                                                           @Query("nv_version") int nvVersion,
                                                           @Query("disease_tag_id") String diseaseTagId,
                                                           @Query("doctor_id") String doctorId);

    /**
     * 标记指定会话消息已读
     *
     * @param map_id IM 会话id
     * @param msg_id IM 消息id
     * @return
     */
    @FormUrlEncoded
    @POST("/v2/message/read/mark")
    Observable<BaseEntity<DataEntity>> markMsgRead(@Field("map_id") String map_id, @Field("msg_id") String msg_id, @Field("type") String type);

}
