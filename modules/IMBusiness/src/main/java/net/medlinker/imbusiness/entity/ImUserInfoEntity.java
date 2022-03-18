package net.medlinker.imbusiness.entity;


import net.medlinker.base.entity.DataEntity;

/**
 * @author hmy
 * @time 2020/6/30 17:18
 */
public class ImUserInfoEntity extends DataEntity {
    /**
     * userId : 57015747
     * name : 豪斯
     * avatar : http://pub-med-avatar.imgs.medlinker.net/64c02270-1714-46b4-ab2b-48633e691fd5?imageslim
     * roleName: "医生"
     */

    private long userId;
    private String name;
    private String avatar;
    private String roleName;
    private String userType; //本地使用 doctor、patient、assistant、healthManager
    private int type; // 本地使用，用户类型

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isDoctor() {
        return "doctor".equals(userType);
    }

    public boolean isPatient() {
        return "patient".equals(userType);
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
