package net.medlinker.imbusiness.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import net.medlinker.base.entity.DataEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description
 * @time 2017/6/12
 */
public class ImConfigEntity extends DataEntity {


    /**
     * groupSilence : 1
     * groupSilencePeriod : [[0,21600]]
     * groupSilenceText : 晚间12点后，为不打扰已休息的同伴，群消息开启勿扰模式。 勿扰模式将在次日6点关闭。
     * upgradeText : 抱歉,旧版本无法支持页面打开,请升级新版app。
     * version : 1497004566
     */

    @SerializedName("groupSilence")
    private int groupSilence;
    @SerializedName("groupSilenceText")
    private String groupSilenceText = "晚间12点后，为不打扰已休息的同伴，群消息开启勿扰模式。 勿扰模式将在次日6点关闭。";
    @SerializedName("upgradeText")
    private String upgradeText = "抱歉，旧版本无法支持页面打开，请升级新版app。";
    @SerializedName("version")
    private long version;
    @SerializedName("groupSilencePeriod")
    private ArrayList<ArrayList<Integer>> groupSilencePeriod;

    public int getGroupSilence() {
        return groupSilence;
    }

    public void setGroupSilence(int groupSilence) {
        this.groupSilence = groupSilence;
    }

    public String getGroupSilenceText() {
        return groupSilenceText;
    }

    public void setGroupSilenceText(String groupSilenceText) {
        this.groupSilenceText = groupSilenceText;
    }

    public String getUpgradeText() {
        return upgradeText;
    }

    public void setUpgradeText(String upgradeText) {
        this.upgradeText = upgradeText;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public ArrayList<ArrayList<Integer>> getGroupSilencePeriod() {
        if (groupSilencePeriod == null) groupSilencePeriod = new ArrayList<>();
        return groupSilencePeriod;
    }

    public void setGroupSilencePeriod(ArrayList<ArrayList<Integer>> groupSilencePeriod) {
        this.groupSilencePeriod = groupSilencePeriod;
    }

    @Override
    public String toString() {
        return "ImConfigEntity{" +
                "groupSilence=" + groupSilence +
                ", groupSilenceText='" + groupSilenceText + '\'' +
                ", upgradeText='" + upgradeText + '\'' +
                ", version=" + version +
                ", groupSilencePeriod=" + groupSilencePeriod +
                '}';
    }

    public ImConfigEntity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.groupSilence);
        dest.writeString(this.groupSilenceText);
        dest.writeString(this.upgradeText);
        dest.writeLong(this.version);
        dest.writeList(this.groupSilencePeriod);
    }

    protected ImConfigEntity(Parcel in) {
        super(in);
        this.groupSilence = in.readInt();
        this.groupSilenceText = in.readString();
        this.upgradeText = in.readString();
        this.version = in.readLong();
        this.groupSilencePeriod = new ArrayList<ArrayList<Integer>>();
        Type collectionType = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        in.readList(this.groupSilencePeriod, collectionType.getClass().getClassLoader());
    }

    public static final Parcelable.Creator<ImConfigEntity> CREATOR = new Parcelable.Creator<ImConfigEntity>() {
        @Override
        public ImConfigEntity createFromParcel(Parcel source) {
            return new ImConfigEntity(source);
        }

        @Override
        public ImConfigEntity[] newArray(int size) {
            return new ImConfigEntity[size];
        }
    };
}
