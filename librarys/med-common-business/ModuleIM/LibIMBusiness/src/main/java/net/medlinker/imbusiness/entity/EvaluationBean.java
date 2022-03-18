package net.medlinker.imbusiness.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class EvaluationBean implements Parcelable {
    /**
     * id : 13
     * doctorId : 877809046
     * doctorName : greatcz1
     * evaluationUserName : testUserName
     * score : 22
     * educationDegree : 小学
     */

    private long id;
    private long doctorId;
    private String doctorName;
    private String evaluationUserName;
    private int score;
    private String educationDegree;
    private String appVersion;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(long doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getEvaluationUserName() {
        return evaluationUserName;
    }

    public void setEvaluationUserName(String evaluationUserName) {
        this.evaluationUserName = evaluationUserName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getEducationDegree() {
        return educationDegree;
    }

    public void setEducationDegree(String educationDegree) {
        this.educationDegree = educationDegree;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public EvaluationBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.doctorId);
        dest.writeString(this.doctorName);
        dest.writeString(this.evaluationUserName);
        dest.writeInt(this.score);
        dest.writeString(this.educationDegree);
        dest.writeString(this.appVersion);
    }

    protected EvaluationBean(Parcel in) {
        this.id = in.readLong();
        this.doctorId = in.readLong();
        this.doctorName = in.readString();
        this.evaluationUserName = in.readString();
        this.score = in.readInt();
        this.educationDegree = in.readString();
        this.appVersion = in.readString();
    }

    public static final Creator<EvaluationBean> CREATOR = new Creator<EvaluationBean>() {
        @Override
        public EvaluationBean createFromParcel(Parcel source) {
            return new EvaluationBean(source);
        }

        @Override
        public EvaluationBean[] newArray(int size) {
            return new EvaluationBean[size];
        }
    };
}
