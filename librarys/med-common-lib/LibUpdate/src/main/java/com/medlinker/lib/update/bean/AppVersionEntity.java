package com.medlinker.lib.update.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.Keep;

import com.medlinker.lib.utils.MedAppInfo;

import net.medlinker.base.entity.DataEntity;

@Keep
public class AppVersionEntity extends DataEntity implements Parcelable {

    public String title;
    //versionName
    public String appVersion;
    //更新细节
    public String describe;
    //下载地址
    public String downUrl;
    //是否弹窗
    public boolean pop;

    public int vid;

    //是否强制更新
    public boolean forceUpgradeStatus;

    public AppVersionEntity(){}

    protected AppVersionEntity(Parcel in) {
        super(in);
        title = in.readString();
        appVersion = in.readString();
        describe = in.readString();
        downUrl = in.readString();
        pop = in.readByte() != 0;
        vid = in.readInt();
        forceUpgradeStatus = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(title);
        dest.writeString(appVersion);
        dest.writeString(describe);
        dest.writeString(downUrl);
        dest.writeByte((byte) (pop ? 1 : 0));
        dest.writeInt(vid);
        dest.writeByte((byte)(forceUpgradeStatus ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AppVersionEntity> CREATOR = new Creator<AppVersionEntity>() {
        @Override
        public AppVersionEntity createFromParcel(Parcel in) {
            return new AppVersionEntity(in);
        }

        @Override
        public AppVersionEntity[] newArray(int size) {
            return new AppVersionEntity[size];
        }
    };

    public int getVersionCode() {
        if (TextUtils.isEmpty(appVersion)) {
            return 0;
        }

        String []items = appVersion.split("\\.");
        if (items.length != 3) {
            return 0;
        }

        try {
            return Integer.parseInt(items[0]) * 10000 + Integer.parseInt(items[1]) * 100 + Integer.parseInt(items[2]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public boolean hasNewVersion() {
        return getVersionCode() > MedAppInfo.getVersionCode();
    }
}
