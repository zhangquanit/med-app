package net.medlinker.imbusiness.entity;

/**
 * @author hmy
 * @time 2020/8/23 14:48
 */
public class MsgReservationJsonEntity {

    private MsgReservationJsonBean self;
    private MsgReservationJsonBean other;

    public MsgReservationJsonBean getSelf() {
        if (self == null) {
            self = new MsgReservationJsonBean();
        }
        return self;
    }

    public void setSelf(MsgReservationJsonBean self) {
        this.self = self;
    }

    public MsgReservationJsonBean getOther() {
        if (other == null) {
            other = new MsgReservationJsonBean();
        }
        return other;
    }

    public void setOther(MsgReservationJsonBean other) {
        this.other = other;
    }

    public class MsgReservationJsonBean {

        /**
         * title :
         * appointTime :
         * appointTimeText :
         * duration :
         * transNo :
         */

        private String title;
        private long appointTime;
        private String appointTimeText;
        private String duration;
        private String transNo;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public long getAppointTime() {
            return appointTime;
        }

        public void setAppointTime(long appointTime) {
            this.appointTime = appointTime;
        }

        public String getAppointTimeText() {
            return appointTimeText;
        }

        public void setAppointTimeText(String appointTimeText) {
            this.appointTimeText = appointTimeText;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getTransNo() {
            return transNo;
        }

        public void setTransNo(String transNo) {
            this.transNo = transNo;
        }
    }
}
