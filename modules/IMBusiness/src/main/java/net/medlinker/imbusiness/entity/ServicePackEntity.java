package net.medlinker.imbusiness.entity;

import com.google.gson.annotations.SerializedName;

/**
 * @author hmy
 * @time 2020-04-22 11:24
 */
public class ServicePackEntity {

    @SerializedName("package")
    private PackageBean packageX;
    @SerializedName("diseaseTag")
    private DiseaseTagBean diseaseTag;

    public PackageBean getPackage() {
        if (packageX == null) {
            packageX = new PackageBean();
        }
        return packageX;
    }

    public void setPackage(PackageBean packageX) {
        this.packageX = packageX;
    }

    public static class PackageBean {

        private long createdTime;
        private String diseaseTagId;
        private String doctorId;
        private String id;
        private String intro;
        private int level; //1-体验版，2-轻享版，3-标准版，4-plus版
        private String name;

        public long getCreatedTime() {
            return createdTime;
        }

        public void setCreatedTime(long createdTime) {
            this.createdTime = createdTime;
        }

        public String getDiseaseTagId() {
            return diseaseTagId;
        }

        public void setDiseaseTagId(String diseaseTagId) {
            this.diseaseTagId = diseaseTagId;
        }

        public String getDoctorId() {
            return doctorId;
        }

        public void setDoctorId(String doctorId) {
            this.doctorId = doctorId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIntro() {
            return intro;
        }

        public void setIntro(String intro) {
            this.intro = intro;
        }

        public String getLevelStr() {
            switch (level) {
                case 1:
                    return "体验版";
                case 2:
                    return "轻享版";
                case 3:
                    return "标准版";
                case 4:
                    return "plus版";
            }
            return "";
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public DiseaseTagBean getDiseaseTag() {
        if (null == diseaseTag) {
            diseaseTag = new DiseaseTagBean();
        }
        return diseaseTag;
    }

    public void setDiseaseTag(DiseaseTagBean diseaseTag) {
        this.diseaseTag = diseaseTag;
    }

}
