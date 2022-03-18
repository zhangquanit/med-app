package net.medlinker.imbusiness.entity;

/**
 * @author hmy
 * @time 2020/9/27 14:20
 */
public class OwnerUserEntity {
    /**
     * avatar : string
     * hospital : string
     * hospitalId : 0
     * isSurgeon : 0
     * jumpUrl : string
     * name : string
     * sectionId : 0
     * sectionName : string
     * sex : 0
     * titleId : 0
     * titleName : string
     * type : 0
     * userId : 0
     * userPlatform : string
     */

    private String avatar;
    private String hospital;
    private int hospitalId;
    private int isSurgeon;
    private String jumpUrl;
    private String name;
    private int sectionId;
    private String sectionName;
    private int sex;
    private int titleId;
    private String titleName;
    private int type;
    private int userId;
    private String userPlatform;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public int getIsSurgeon() {
        return isSurgeon;
    }

    public void setIsSurgeon(int isSurgeon) {
        this.isSurgeon = isSurgeon;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getTitleId() {
        return titleId;
    }

    public void setTitleId(int titleId) {
        this.titleId = titleId;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserPlatform() {
        return userPlatform;
    }

    public void setUserPlatform(String userPlatform) {
        this.userPlatform = userPlatform;
    }
}
