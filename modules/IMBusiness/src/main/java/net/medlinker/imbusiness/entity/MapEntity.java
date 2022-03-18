package net.medlinker.imbusiness.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description data为map的string string 结构
 * @time 2017/4/15
 */
public class MapEntity implements Parcelable {

    @SerializedName("errcode")
    private int code;
    @SerializedName("errmsg")
    private String msg;
    @SerializedName("data")
    private Map<String, String> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeString(this.msg);
        dest.writeInt(this.data.size());
        for (Map.Entry<String, String> entry : this.data.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
    }

    public MapEntity() {
    }

    protected MapEntity(Parcel in) {
        this.code = in.readInt();
        this.msg = in.readString();
        int dataSize = in.readInt();
        this.data = new HashMap<String, String>(dataSize);
        for (int i = 0; i < dataSize; i++) {
            String key = in.readString();
            String value = in.readString();
            this.data.put(key, value);
        }
    }

    public static final Parcelable.Creator<MapEntity> CREATOR = new Parcelable.Creator<MapEntity>() {
        @Override
        public MapEntity createFromParcel(Parcel source) {
            return new MapEntity(source);
        }

        @Override
        public MapEntity[] newArray(int size) {
            return new MapEntity[size];
        }
    };
}
