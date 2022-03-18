package net.medlinker.imbusiness.entity.prescription;

public class ElectronicBean {

    /**
     * 已失效
     */
    public static final int STATUS_EXPIRED = -1;
    /**
     * 未发送给患者
     */
    public static final int STATUS_NO_SEND = 0;
    /**
     * 有效
     */
    public static final int STATUS_VALID = 1;
    /**
     * 已使用
     */
    public static final int STATUS_USED = 2;

    /**
     * auditStatus : 0
     * businessId : 0
     * businessSubType : 0
     * createAt : 0
     * diagnose : string
     * disease : string
     * dispensing : string
     * doctorId : 0
     * expenseCategory : 0
     * genre : 0
     * id : 0
     * internetHospitalId : 0
     * patientAge : 0
     * patientBirthday : 0
     * patientGender : 0
     * patientId : 0
     * patientName : string
     * pharmacist : string
     * prImg : string
     * prPdf : string
     * sectionId : 0
     * sectionName : string
     * status : 0
     * transNo : string
     * type : 0
     */

    private int auditStatus; //药师审核状态：-1-审核拒绝，0-未审核，1-审核通过
    private String businessId; //处方所属业务
    private int businessSubType; //处方所属的业务的子类型
    private long createAt;
    private String diagnose; //临床诊断/中医证型
    private String disease; //中医病名
    private String dispensing; //审核发药药师
    private String doctorId; //开具处方的医生ID
    private int expenseCategory; //处方费用类别：1-自费，2-公费
    private int genre; //处方类型：1-普通，2-儿童, 此处不用bool，预留后续有其他类型
    private long id;
    private int internetHospitalId; //处方所属的互联网医院ID
    private int patientAge;
    private int patientBirthday; //诊断病人的出生年月日，如：20120202'
    private int patientGender; //诊断病人的性别：0-未知，1-男，2-女
    private int patientId;
    private String patientName;
    private String prImg; //处方留底图片, 默认为空
    private String prPdf; //处方留底PDF, 默认为空
    private String sectionId; //处方所属科室
    private String sectionName; //科室名称
    private int status; //状态（大于零为有效处方，小于等于零为无效处方）：-1-已失效，0-未发送给患者，1-有效，2-已使用 默认为0
    private String transNo; //处方号, 服务统一生成
    private int type; //处方药品类型：1-西药/中成药，2-中药药方，3-中药饮片（中草药）
    private StructuredUsages structuredUsages;

    public int getAuditStatus() {
        return auditStatus;
    }

    public boolean isNoPassAudit() {
        return auditStatus == -1;
    }

    public void setAuditStatus(int auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public int getBusinessSubType() {
        return businessSubType;
    }

    public void setBusinessSubType(int businessSubType) {
        this.businessSubType = businessSubType;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public String getDiagnose() {
        return diagnose;
    }

    public void setDiagnose(String diagnose) {
        this.diagnose = diagnose;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getDispensing() {
        return dispensing;
    }

    public void setDispensing(String dispensing) {
        this.dispensing = dispensing;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public int getExpenseCategory() {
        return expenseCategory;
    }

    public void setExpenseCategory(int expenseCategory) {
        this.expenseCategory = expenseCategory;
    }

    public int getGenre() {
        return genre;
    }

    public void setGenre(int genre) {
        this.genre = genre;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getInternetHospitalId() {
        return internetHospitalId;
    }

    public void setInternetHospitalId(int internetHospitalId) {
        this.internetHospitalId = internetHospitalId;
    }

    public int getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(int patientAge) {
        this.patientAge = patientAge;
    }

    public int getPatientBirthday() {
        return patientBirthday;
    }

    public void setPatientBirthday(int patientBirthday) {
        this.patientBirthday = patientBirthday;
    }

    public int getPatientGender() {
        return patientGender;
    }

    public void setPatientGender(int patientGender) {
        this.patientGender = patientGender;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPrImg() {
        return prImg;
    }

    public void setPrImg(String prImg) {
        this.prImg = prImg;
    }

    public String getPrPdf() {
        return prPdf;
    }

    public void setPrPdf(String prPdf) {
        this.prPdf = prPdf;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTransNo() {
        return transNo;
    }

    public void setTransNo(String transNo) {
        this.transNo = transNo;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public StructuredUsages getStructuredUsages() {
        if (structuredUsages == null) {
            structuredUsages = new StructuredUsages();
        }
        return structuredUsages;
    }
}
