package net.medlinker.base.entity;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 4.0
 * @description
 * @time 2016-12-14-17:37
 */
@Keep
public class DataWraperEntity<T extends DataEntity> extends DataEntity {

    @SerializedName("start")
    public String start;
    @SerializedName("more")
    public int more;
    @SerializedName("list")
    public ArrayList<T> list;
    @SerializedName("total")
    public int total;

}
