package net.medlinker.imbusiness.network;

import net.medlinker.imbusiness.entity.ClinicMemberEntity;
import net.medlinker.libhttp.BaseEntity;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author hmy
 * @time 2020/9/28 10:36
 */
public interface ClinicApi {

    /**
     * 医生端&患者端诊室成员列表
     *
     * @param groupId
     * @return
     */
    @GET("/chronicDoctor/clinicRoomMembers")
    Observable<BaseEntity<ClinicMemberEntity>> getUserList(@Query("groupId") long groupId);

}
