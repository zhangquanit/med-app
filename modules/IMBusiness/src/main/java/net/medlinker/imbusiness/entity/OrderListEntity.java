package net.medlinker.imbusiness.entity;

import android.text.TextUtils;

import net.medlinker.base.entity.DataEntity;

/**
 * @author hmy
 */
public class OrderListEntity extends DataEntity {

    private InquiryExt inquiryExt;

    public void setInquiryExt(InquiryExt inquiryExt) {
        this.inquiryExt = inquiryExt;
    }

    public InquiryExt getInquiryExt() {
        return this.inquiryExt;
    }

    public static class InquiryExt {
        private int price;

        private int isThirdDeleted;

        private int auditStatus;

        private String transNo;

        private int settlementPrice;

        private int isPaid;

        private String ext;

        private int updatedAt;

        private int isMedicalInsurance;

        private String roomId;

        private int createdAt;

        private int appointEndTime;

        private String storeId;

        private int limitTime;

        private int appointStartTime;

        public void setPrice(int price) {
            this.price = price;
        }

        public int getPrice() {
            return this.price;
        }

        public void setIsThirdDeleted(int isThirdDeleted) {
            this.isThirdDeleted = isThirdDeleted;
        }

        public int getIsThirdDeleted() {
            return this.isThirdDeleted;
        }

        public void setAuditStatus(int auditStatus) {
            this.auditStatus = auditStatus;
        }

        public int getAuditStatus() {
            return this.auditStatus;
        }

        public void setTransNo(String transNo) {
            this.transNo = transNo;
        }

        public String getTransNo() {
            return this.transNo;
        }

        public void setSettlementPrice(int settlementPrice) {
            this.settlementPrice = settlementPrice;
        }

        public int getSettlementPrice() {
            return this.settlementPrice;
        }

        public void setIsPaid(int isPaid) {
            this.isPaid = isPaid;
        }

        public int getIsPaid() {
            return this.isPaid;
        }

        public void setExt(String ext) {
            this.ext = ext;
        }

        public String getExt() {
            return this.ext;
        }

        public void setUpdatedAt(int updatedAt) {
            this.updatedAt = updatedAt;
        }

        public int getUpdatedAt() {
            return this.updatedAt;
        }

        public void setIsMedicalInsurance(int isMedicalInsurance) {
            this.isMedicalInsurance = isMedicalInsurance;
        }

        public int getIsMedicalInsurance() {
            return this.isMedicalInsurance;
        }

        public void setRoomId(String roomId) {
            this.roomId = roomId;
        }

        public String getRoomId() {
            return this.roomId;
        }

        public int getRoomIdInt() {
            if (TextUtils.isEmpty(roomId)) {
                return 0;
            }
            return Integer.parseInt(roomId);
        }

        public void setCreatedAt(int createdAt) {
            this.createdAt = createdAt;
        }

        public int getCreatedAt() {
            return this.createdAt;
        }

        public void setAppointEndTime(int appointEndTime) {
            this.appointEndTime = appointEndTime;
        }

        public int getAppointEndTime() {
            return this.appointEndTime;
        }

        public void setStoreId(String storeId) {
            this.storeId = storeId;
        }

        public String getStoreId() {
            return this.storeId;
        }

        public void setLimitTime(int limitTime) {
            this.limitTime = limitTime;
        }

        public int getLimitTime() {
            return this.limitTime;
        }

        public void setAppointStartTime(int appointStartTime) {
            this.appointStartTime = appointStartTime;
        }

        public int getAppointStartTime() {
            return this.appointStartTime;
        }
    }

}
