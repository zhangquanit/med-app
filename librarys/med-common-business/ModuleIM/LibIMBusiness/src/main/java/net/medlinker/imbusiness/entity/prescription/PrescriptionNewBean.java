package net.medlinker.imbusiness.entity.prescription;


import net.medlinker.base.entity.DataEntity;

import java.util.ArrayList;
import java.util.List;

public class PrescriptionNewBean extends DataEntity {

    private String objectId;
    private String roleName = "";
    private ElectronicBean electronic;
    private List<DrugNewEntity> drugs;
    private PrescriptionExtraEntity extra;

    private boolean isContinuation; //是否是续方
    private int prescriptionType; //处方类型

    public ElectronicBean getElectronic() {
        if (electronic == null) {
            electronic = new ElectronicBean();
        }
        return electronic;
    }

    public void setElectronic(ElectronicBean electronic) {
        this.electronic = electronic;
    }

    public List<DrugNewEntity> getDrugs() {
        if (drugs == null) {
            drugs = new ArrayList<>();
        }
        if (drugs.size() > 2) {
            return drugs.subList(0, 2);
        }
        return drugs;
    }

    public List<DrugNewEntity> getCompleteDrugs() {
        if (drugs == null) {
            drugs = new ArrayList<>();
        }
        return drugs;
    }

    public void setDrugs(List<DrugNewEntity> drugs) {
        this.drugs = drugs;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public PrescriptionExtraEntity getExtra() {
        if (extra == null) {
            extra = new PrescriptionExtraEntity();
        }
        return extra;
    }

    public void setExtra(PrescriptionExtraEntity extra) {
        this.extra = extra;
    }

    public boolean isContinuation() {
        return isContinuation;
    }

    public void setContinuation(boolean continuation) {
        isContinuation = continuation;
    }

    public int getPrescriptionType() {
        return prescriptionType;
    }

    public void setPrescriptionType(int prescriptionType) {
        this.prescriptionType = prescriptionType;
    }
}
