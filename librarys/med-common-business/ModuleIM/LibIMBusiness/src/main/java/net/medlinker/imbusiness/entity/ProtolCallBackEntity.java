package net.medlinker.imbusiness.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import net.medlinker.base.entity.DataEntity;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description im服务器返回地址和端口实体
 * @time 2017/2/16
 */
public class ProtolCallBackEntity extends DataEntity {

    @SerializedName("tcp")
    private String hostPort;
    @SerializedName("msgId")
    private long msgId;
    @SerializedName("groupId")
    private long groupId;

    public String getHostPort() {
        return hostPort;
    }

    public void setHostPort(String hostPort) {
        this.hostPort = hostPort;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "ProtolCallBackEntity{" +
                "hostPort='" + hostPort + '\'' +
                ", msgId=" + msgId +
                ", groupId=" + groupId +
                '}';
    }

    public ProtolCallBackEntity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.hostPort);
        dest.writeLong(this.msgId);
        dest.writeLong(this.groupId);
    }

    protected ProtolCallBackEntity(Parcel in) {
        super(in);
        this.hostPort = in.readString();
        this.msgId = in.readLong();
        this.groupId = in.readLong();
    }

    public static final Parcelable.Creator<ProtolCallBackEntity> CREATOR = new Parcelable.Creator<ProtolCallBackEntity>() {
        @Override
        public ProtolCallBackEntity createFromParcel(Parcel source) {
            return new ProtolCallBackEntity(source);
        }

        @Override
        public ProtolCallBackEntity[] newArray(int size) {
            return new ProtolCallBackEntity[size];
        }
    };
}
