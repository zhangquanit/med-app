package com.medlinker.lib.push.med;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

@Keep
public class PushMsgEntity implements Parcelable {
    //通知ID
    public long id;
    //通知标题
    public String title;
    //通知内容
    public String content;
    //接收通知人ID
    public long targetUserId;
    //通知产生时间
    public long time;
    //自定义跳转数据 一般是路由信息
    public String extra;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.content);
        dest.writeLong(this.targetUserId);
        dest.writeLong(this.time);
        dest.writeString(this.extra);
    }

    public void readFromParcel(Parcel source) {
        this.id = source.readLong();
        this.title = source.readString();
        this.content = source.readString();
        this.targetUserId = source.readLong();
        this.time = source.readLong();
        this.extra = source.readString();
    }

    public PushMsgEntity() {
    }

    protected PushMsgEntity(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.content = in.readString();
        this.targetUserId = in.readLong();
        this.time = in.readLong();
        this.extra = in.readString();
    }

    public static final Creator<PushMsgEntity> CREATOR = new Creator<PushMsgEntity>() {
        @Override
        public PushMsgEntity createFromParcel(Parcel source) {
            return new PushMsgEntity(source);
        }

        @Override
        public PushMsgEntity[] newArray(int size) {
            return new PushMsgEntity[size];
        }
    };
}
