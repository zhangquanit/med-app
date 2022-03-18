package net.medlinker.imbusiness.entity.prescription;

import android.os.Parcel;
import android.os.Parcelable;

import net.medlinker.base.entity.DataEntity;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description
 * @time 2017/3/9
 */
public class PrescriptionEntity extends DataEntity {

    /**
     * objectName : MD: Chufang
     * prescription : {"prescriptionId":120,"transNo":"170310213906252","status":2,"doctorId":57015747,"patientId":50703908,"prescription":"u63a8u7406","doctorAdvice":"u8003u8651u8003u8651","insertTime":1489121390,"drugs":[]}
     * content : null
     * extra : {"msgId":11957}
     * user : {"id":"d57015747","name":"u8c6au65af","icon":"http: //7xp8w2.com1.z0.glb.clouddn.com/FnlWrYDv7tn4o2w87kn56CDuFGIB?imageslim"}
     */

    private PrescriptionBean prescription;

    public PrescriptionBean getPrescription() {
        return prescription;
    }

    public void setPrescription(PrescriptionBean prescription) {
        this.prescription = prescription;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.prescription, flags);
    }

    public PrescriptionEntity() {
    }

    protected PrescriptionEntity(Parcel in) {
        super(in);
        this.prescription = in.readParcelable(PrescriptionBean.class.getClassLoader());
    }

    public static final Parcelable.Creator<PrescriptionEntity> CREATOR = new Parcelable.Creator<PrescriptionEntity>() {
        @Override
        public PrescriptionEntity createFromParcel(Parcel source) {
            return new PrescriptionEntity(source);
        }

        @Override
        public PrescriptionEntity[] newArray(int size) {
            return new PrescriptionEntity[size];
        }
    };
}
