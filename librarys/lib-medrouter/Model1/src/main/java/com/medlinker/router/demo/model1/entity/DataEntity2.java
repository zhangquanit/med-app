package com.medlinker.router.demo.model1.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author zhangquan
 */
public class DataEntity2 implements Parcelable {
    public String param1;
    public int param2;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.param1);
        dest.writeInt(this.param2);
    }

    public void readFromParcel(Parcel source) {
        this.param1 = source.readString();
        this.param2 = source.readInt();
    }

    public DataEntity2() {
    }

    protected DataEntity2(Parcel in) {
        this.param1 = in.readString();
        this.param2 = in.readInt();
    }

    public static final Parcelable.Creator<DataEntity2> CREATOR = new Parcelable.Creator<DataEntity2>() {
        @Override
        public DataEntity2 createFromParcel(Parcel source) {
            return new DataEntity2(source);
        }

        @Override
        public DataEntity2[] newArray(int size) {
            return new DataEntity2[size];
        }
    };
}
