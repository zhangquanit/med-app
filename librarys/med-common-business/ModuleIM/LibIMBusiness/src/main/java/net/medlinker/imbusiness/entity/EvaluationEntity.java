package net.medlinker.imbusiness.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 认知障碍测评
 *
 * @author hmy
 */
public class EvaluationEntity implements Parcelable {


    /**
     * objectName : MD:RenZhiPingCeJieGuo
     * h5Url :
     * evaluation : {"id":13,"doctorId":877809046,"doctorName":"greatcz1","evaluationUserName":"testUserName","score":22,"educationDegree":"小学"}
     * content : null
     * extra : {"msgId":0}
     * user : {"id":"p877809052","name":"中指","icon":"http://avatar-file.qa.medlinker.com/15395711131h47l9ym.jpg"}
     */

    private EvaluationBean evaluation;

    public EvaluationBean getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(EvaluationBean evaluation) {
        this.evaluation = evaluation;
    }

    public EvaluationEntity() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.evaluation, flags);
    }

    protected EvaluationEntity(Parcel in) {
        this.evaluation = in.readParcelable(EvaluationBean.class.getClassLoader());
    }

    public static final Creator<EvaluationEntity> CREATOR = new Creator<EvaluationEntity>() {
        @Override
        public EvaluationEntity createFromParcel(Parcel source) {
            return new EvaluationEntity(source);
        }

        @Override
        public EvaluationEntity[] newArray(int size) {
            return new EvaluationEntity[size];
        }
    };
}
