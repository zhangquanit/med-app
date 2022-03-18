package com.medlinker.lib.fileupload.entity;

import android.os.Parcel;
import android.os.Parcelable;

import net.medlinker.base.entity.DataEntity;


public class TokenEntity extends DataEntity implements Parcelable {
    private String token;
    private String domain;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.token);
        dest.writeString(this.domain);
    }

    public TokenEntity() {
    }

    protected TokenEntity(Parcel in) {
        this.token = in.readString();
        this.domain = in.readString();
    }

    public static final Parcelable.Creator<TokenEntity> CREATOR = new Parcelable.Creator<TokenEntity>() {
        public TokenEntity createFromParcel(Parcel source) {
            return new TokenEntity(source);
        }

        public TokenEntity[] newArray(int size) {
            return new TokenEntity[size];
        }
    };

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
