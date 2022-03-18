package net.medlinker.imbusiness.entity.prescription;

import android.os.Parcel;

import com.google.gson.annotations.SerializedName;

import net.medlinker.base.entity.DataEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description
 * @time 2017/3/19
 */
public class PrescriptionBean extends DataEntity implements android.os.Parcelable {

    /**
     * 审核中
     */
    public static final int STATUS_VERIFYING = 1;
    /**
     * 已审核
     */
    public static final int STATUS_VERIFYED = 2;
    /**
     * 未通过
     */
    public static final int STATUS_NO_PASS = 3;
    /**
     * 已使用
     */
    public static final int STATUS_USED = 4;
    /**
     * 已失效
     */
    public static final int STATUS_EXPIRED = 5;

    /**
     * 西药处方
     */
    public static final int TYPE_WESTERN = 1;
    /**
     * 中药处方
     */
    public static final int TYPE_CHINESE = 2;

    /**
     * prescriptionId : 120
     * transNo : 170310213906252
     * status : 2
     * doctorId : 57015747
     * patientId : 50703908
     * prescription : u63a8u7406
     * doctorAdvice : u8003u8651u8003u8651
     * insertTime : 1489121390
     * drugs : []
     */


    // 药品类型：1.西药中成药 2. 中药
    private int prType;
    private int recipeType;
    private String prescriptionId;
    private String transNo;
    private int status;
    private int doctorId;
    private int patientId;
    private String prescription;
    private String doctorAdvice;
    private int insertTime;
    private List<DrugEntity> drugs;
    /**
     * 中药药方详情
     */
    private TcmRecipeEntity tcmRecipe;
    // 中药：病名
    private String question;

    /**
     * 中药：证型
     */
    private String tcmDisease;
    private String appVersion;
    private String openType;
    private PharmacistAuditInfoEntity pharmacistAuditInfo;
    /**
     * 续签
     */
    private boolean isContinuation;
    private long webHospitalId;

    /**
     * 续方提醒
     *
     * @return
     */
    private String noticeText;


    /**
     * 处方类型
     */
    private int prescriptionType;

    public String getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public String getTransNo() {
        return transNo;
    }

    public void setTransNo(String transNo) {
        this.transNo = transNo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public String getDoctorAdvice() {
        return doctorAdvice;
    }

    public void setDoctorAdvice(String doctorAdvice) {
        this.doctorAdvice = doctorAdvice;
    }

    public int getInsertTime() {
        return insertTime;
    }

    public int getPrType() {
        return prType;
    }

    public void setPrType(int prType) {
        this.prType = prType;
    }

    public TcmRecipeEntity getTcmRecipe() {
        return tcmRecipe;
    }

    public void setTcmRecipe(TcmRecipeEntity tcmRecipe) {
        this.tcmRecipe = tcmRecipe;
    }

    public void setInsertTime(int insertTime) {
        this.insertTime = insertTime;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getTcmDisease() {
        return tcmDisease;
    }

    public String getOpenType() {
        return openType;
    }

    public void setOpenType(String openType) {
        this.openType = openType;
    }

    public void setTcmDisease(String tcmDisease) {
        this.tcmDisease = tcmDisease;
    }

    public List<DrugEntity> getDrugs() {
        if (drugs == null) {
            return new ArrayList<>();
        }
        if (drugs.size() > 2) {
            return drugs.subList(0, 2);
        }
        return drugs;
    }

    public List<DrugEntity> getCompleteDrugs() {
        return drugs;
    }

    public void setDrugs(List<DrugEntity> drugs) {
        this.drugs = drugs;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public boolean isContinuation() {
        return isContinuation;
    }

    public void setContinuation(boolean continuation) {
        isContinuation = continuation;
    }

    public PharmacistAuditInfoEntity getPharmacistAuditInfo() {
        return pharmacistAuditInfo;
    }

    public void setPharmacistAuditInfo(PharmacistAuditInfoEntity pharmacist) {
        this.pharmacistAuditInfo = pharmacist;
    }

    public long getWebHospitalId() {
        return webHospitalId;
    }

    public void setWebHospitalId(long webHospitalId) {
        this.webHospitalId = webHospitalId;
    }

    public PrescriptionBean() {
    }

    public String getNoticeText() {
        return noticeText;
    }

    public void setNoticeText(String noticeText) {
        this.noticeText = noticeText;
    }

    public void setPrescriptionType(int prescriptionType) {
        this.prescriptionType = prescriptionType;
    }

    /**
     * 中药药方详情
     */
    public static class TcmRecipeEntity implements android.os.Parcelable {
        @SerializedName("id")
        private long id;

        @SerializedName("title")
        private String title;
        /**
         * 用法
         */
        @SerializedName("usage")
        private String usage;

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(this.id);
            dest.writeString(this.title);
            dest.writeString(this.usage);
        }

        public TcmRecipeEntity() {
        }

        protected TcmRecipeEntity(Parcel in) {
            this.id = in.readLong();
            this.title = in.readString();
            this.usage = in.readString();
        }

        public static final Creator<TcmRecipeEntity> CREATOR = new Creator<TcmRecipeEntity>() {
            @Override
            public TcmRecipeEntity createFromParcel(Parcel source) {
                return new TcmRecipeEntity(source);
            }

            @Override
            public TcmRecipeEntity[] newArray(int size) {
                return new TcmRecipeEntity[size];
            }
        };

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUsage() {
            return usage;
        }

        public void setUsage(String usage) {
            this.usage = usage;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.prType);
        dest.writeString(this.prescriptionId);
        dest.writeString(this.transNo);
        dest.writeInt(this.status);
        dest.writeInt(this.doctorId);
        dest.writeInt(this.patientId);
        dest.writeString(this.prescription);
        dest.writeString(this.doctorAdvice);
        dest.writeInt(this.insertTime);
        dest.writeTypedList(this.drugs);
        dest.writeParcelable(this.tcmRecipe, flags);
        dest.writeString(this.question);
        dest.writeString(this.noticeText);
        dest.writeString(this.tcmDisease);
        dest.writeString(this.appVersion);
        dest.writeString(this.openType);
        dest.writeParcelable(this.pharmacistAuditInfo, flags);
        dest.writeByte(this.isContinuation ? (byte) 1 : (byte) 0);
        dest.writeLong(this.webHospitalId);
        dest.writeInt(this.prescriptionType);
    }

    protected PrescriptionBean(Parcel in) {
        super(in);
        this.prType = in.readInt();
        this.prescriptionId = in.readString();
        this.transNo = in.readString();
        this.status = in.readInt();
        this.doctorId = in.readInt();
        this.patientId = in.readInt();
        this.prescription = in.readString();
        this.doctorAdvice = in.readString();
        this.insertTime = in.readInt();
        this.drugs = in.createTypedArrayList(DrugEntity.CREATOR);
        this.tcmRecipe = in.readParcelable(TcmRecipeEntity.class.getClassLoader());
        this.question = in.readString();
        this.noticeText = in.readString();
        this.tcmDisease = in.readString();
        this.appVersion = in.readString();
        this.openType = in.readString();
        this.pharmacistAuditInfo = in.readParcelable(PharmacistAuditInfoEntity.class.getClassLoader());
        this.isContinuation = in.readByte() != 0;
        this.webHospitalId = in.readLong();
        this.prescriptionType = in.readInt();
    }

    public static final Creator<PrescriptionBean> CREATOR = new Creator<PrescriptionBean>() {
        @Override
        public PrescriptionBean createFromParcel(Parcel source) {
            return new PrescriptionBean(source);
        }

        @Override
        public PrescriptionBean[] newArray(int size) {
            return new PrescriptionBean[size];
        }
    };
}
