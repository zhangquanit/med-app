package net.medlinker.imbusiness.entity.prescription;

import android.os.Parcel;

public class PharmacistAuditInfoEntity implements android.os.Parcelable{

    private String failedReason;

    public String getFailedReason() {
        return failedReason;
    }

    public void setFailedReason(String failedReason) {
        this.failedReason = failedReason;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.failedReason);
    }

    public PharmacistAuditInfoEntity() {
    }

    protected PharmacistAuditInfoEntity(Parcel in) {
        this.failedReason = in.readString();
    }

    public static final Creator<PharmacistAuditInfoEntity> CREATOR = new Creator<PharmacistAuditInfoEntity>() {
        @Override
        public PharmacistAuditInfoEntity createFromParcel(Parcel source) {
            return new PharmacistAuditInfoEntity(source);
        }

        @Override
        public PharmacistAuditInfoEntity[] newArray(int size) {
            return new PharmacistAuditInfoEntity[size];
        }
    };
}
