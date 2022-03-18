package net.medlinker.imbusiness.entity.prescription;

public class PrescriptionNewEntity {

    private PrescriptionNewBean prescription;

    public PrescriptionNewBean getPrescription() {
        if (prescription == null) {
            prescription = new PrescriptionNewBean();
        }
        return prescription;
    }

    public void setPrescription(PrescriptionNewBean prescription) {
        this.prescription = prescription;
    }
}
