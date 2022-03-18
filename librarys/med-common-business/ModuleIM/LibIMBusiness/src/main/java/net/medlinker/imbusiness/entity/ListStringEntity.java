package net.medlinker.imbusiness.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import net.medlinker.base.entity.DataEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description
 * @time 2017/3/9
 */
public class ListStringEntity extends DataEntity {


    @SerializedName("list")
    private List<String> list;
    @SerializedName("start")
    private int start;


    public List<String> getList() {
        if (list == null) list = new ArrayList<>();
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public ListStringEntity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeStringList(this.list);
        dest.writeInt(this.start);
    }

    protected ListStringEntity(Parcel in) {
        super(in);
        this.list = in.createStringArrayList();
        this.start = in.readInt();
    }

    public static final Parcelable.Creator<ListStringEntity> CREATOR = new Parcelable.Creator<ListStringEntity>() {
        @Override
        public ListStringEntity createFromParcel(Parcel source) {
            return new ListStringEntity(source);
        }

        @Override
        public ListStringEntity[] newArray(int size) {
            return new ListStringEntity[size];
        }
    };
}
