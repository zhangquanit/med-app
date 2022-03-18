package net.medlinker.base.entity;/**
 * Created by kuiwen on 2015/10/20.
 */

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

/**
 * @author <a href="mailto:zhangkuiwen@medlinker.net">Kuiwen.Zhang</a>
 * @version 1.0
 * @description 分页列表数据结构
 * @time 2015/10/20 18:51
 **/
@Keep
public class BaseListEntity<T extends DataEntity> {
    @SerializedName("errcode")
    public int code;
    @SerializedName("errmsg")
    public String msg;
    @SerializedName("data")
    public DataWraperEntity<T> data;

    @Override
    public String toString() {
        return "BaseListEntity{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
