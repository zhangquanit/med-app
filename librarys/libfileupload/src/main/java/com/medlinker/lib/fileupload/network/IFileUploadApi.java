package com.medlinker.lib.fileupload.network;

import com.medlinker.lib.fileupload.entity.TokenEntity;
import com.medlinker.lib.fileupload.entity.UpFileIdEntity;

import net.medlinker.libhttp.BaseEntity;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IFileUploadApi {
    /**
     * 获取七牛token接口
     *
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST("rest/v1/upfiles/tokens/get")
    Observable<BaseEntity<TokenEntity>> getQiNiuToken(@FieldMap Map<String, String> map);

    /**
     * 添加水印
     *
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST("rest/v1/files/create")
    Observable<BaseEntity<UpFileIdEntity>> addQiNiuWaterMarket(@FieldMap Map<String, String> map);
}
