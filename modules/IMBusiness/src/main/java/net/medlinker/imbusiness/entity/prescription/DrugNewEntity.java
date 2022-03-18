package net.medlinker.imbusiness.entity.prescription;

public class DrugNewEntity {
    /**
     * contentId : 0
     * contentThirdCode : string
     * id : 0
     * ingredient : string
     * medicationGenre : 0
     * prescriptionId : 0
     * processingMode : string
     * quantity : 0
     * salePrice : 0
     * specification : string
     * structuredUsages : {"cycle":"string","dosage":0,"dosagePerTimeUnit":"string","usage":"string"}
     * title : string
     * usages : string
     */

    private String contentId; //处方内容ID。如果药品类型是西药/中成药，则是 spu id；如果是中药药方，则是药方ID；如果是第三方药品，则是第三方唯一标识
    private String contentThirdCode; //第三方唯一标识
    private String id;
    private String ingredient; //成份：中药药方的组成、药品的成份
    private int medicationGenre; //药品类型：1-西药，2-中成药，3-中药药方，4-中草药
    private String prescriptionId; //处方id
    private String processingMode; //加工方式：中药药方、中草药的加工方式
    private int quantity; //数量（中药药方疗程）
    private int salePrice; //（针对第三方药品渠道）药品销售单价，单位分
    private String specification; //规格：药品的包装规则、中药药方规格、中草药分量
    private String title; //名称：药品通用名、中药药方名、中草药名称
    private String usages; //用法用量

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getContentThirdCode() {
        return contentThirdCode;
    }

    public void setContentThirdCode(String contentThirdCode) {
        this.contentThirdCode = contentThirdCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public int getMedicationGenre() {
        return medicationGenre;
    }

    public void setMedicationGenre(int medicationGenre) {
        this.medicationGenre = medicationGenre;
    }

    public String getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(String prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public String getProcessingMode() {
        return processingMode;
    }

    public void setProcessingMode(String processingMode) {
        this.processingMode = processingMode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(int salePrice) {
        this.salePrice = salePrice;
    }

    public String getSpecification() {
        return specification;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsages() {
        return usages;
    }

    public void setUsages(String usages) {
        this.usages = usages;
    }

}
