package net.medlinker.imbusiness.entity.prescription;

public class PrescriptionExtraEntity {
    /**
     * doctorPatientBind : 0
     * failedReason : string
     * isBiMiniPr : 0
     * isContinuePr : 0
     */

    private int doctorPatientBind;
    private String failedReason;
    private int isBiMiniPr;
    private int isContinuePr;

    public int getDoctorPatientBind() {
        return doctorPatientBind;
    }

    public void setDoctorPatientBind(int doctorPatientBind) {
        this.doctorPatientBind = doctorPatientBind;
    }

    public String getFailedReason() {
        return failedReason;
    }

    public void setFailedReason(String failedReason) {
        this.failedReason = failedReason;
    }

    public int getIsBiMiniPr() {
        return isBiMiniPr;
    }

    public void setIsBiMiniPr(int isBiMiniPr) {
        this.isBiMiniPr = isBiMiniPr;
    }

    public int getIsContinuePr() {
        return isContinuePr;
    }

    public void setIsContinuePr(int isContinuePr) {
        this.isContinuePr = isContinuePr;
    }
}
