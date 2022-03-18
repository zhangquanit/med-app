package net.medlinker.imbusiness.network;

import net.medlinker.imbusiness.entity.ImConfigEntity;
import net.medlinker.libhttp.BaseEntity;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @author hmy
 * @time 2020/9/22 17:08
 */
public interface ImConfigApi {
    @FormUrlEncoded
    @POST("/rest/v1/sync/data")
    Observable<BaseEntity<ImConfigEntity>> getImConfig(@Field("name") String name);
}
