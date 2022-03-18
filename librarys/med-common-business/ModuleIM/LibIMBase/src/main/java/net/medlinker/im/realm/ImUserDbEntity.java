package net.medlinker.im.realm;

import android.text.TextUtils;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description 群组或者user
 * @time 2017/2/27
 */
public class ImUserDbEntity extends RealmObject {

    @PrimaryKey
    private long id;
    private int refrence; // 目前用户类型:  0=医联用户, 1=经纪人用户 9=系统用户
    private String avatar;
    private String name;
    // 用户修改的新头像
    private String newAvatar;
    // 用户修改的新名称
    private String newName;
    private int type;// 医联用户角色(仅type=0时有效): 1=医生用户 2=机构用户 3=手机注册用户 4=游客(仅redis) 5=微信 6=QQ 11=营销平台患者 33=第三方账号
    private String hospital;
    private String title;
    private String section;
    //新的朋友相关字段
    private boolean isNewFriend;//是否是新的朋友的数据，如果删除新的朋友数据，这里就变成false就行了，每次来新的朋友数据，直接更新这个user即可
    private boolean isMobileContact;//是否是手机联系人 true是 false 不是
    private String addFriendContent;//添加朋友附加信息
    private int addFriendReference;//添加朋友来源
    private int addFriendOperation;//添加朋友操作类型
    private String brokerPhoneNum;//经纪人电话

    public ImUserDbEntity() {
    }

    public ImUserDbEntity(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAvatar() {
        return TextUtils.isEmpty(newAvatar) ? avatar : newAvatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return TextUtils.isEmpty(newName) ? name : newName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRefrence() {
        return refrence;
    }

    public void setRefrence(int refrence) {
        this.refrence = refrence;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public boolean isNewFriend() {
        return isNewFriend;
    }

    public void setNewFriend(boolean newFriend) {
        isNewFriend = newFriend;
    }

    public String getAddFriendContent() {
        return addFriendContent;
    }

    public void setAddFriendContent(String addFriendContent) {
        this.addFriendContent = addFriendContent;
    }

    public boolean isMobileContact() {
        return isMobileContact;
    }

    public void setMobileContact(boolean mobileContact) {
        isMobileContact = mobileContact;
    }

    public int getAddFriendReference() {
        return addFriendReference;
    }

    public void setAddFriendReference(int addFriendReference) {
        this.addFriendReference = addFriendReference;
    }

    public int getAddFriendOperation() {
        return addFriendOperation;
    }

    public void setAddFriendOperation(int addFriendOperation) {
        this.addFriendOperation = addFriendOperation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getBrokerPhoneNum() {
        return brokerPhoneNum;
    }

    public void setBrokerPhoneNum(String brokerPhoneNum) {
        this.brokerPhoneNum = brokerPhoneNum;
    }


    public String getNewAvatar() {
        return newAvatar;
    }

    public void setNewAvatar(String newAvatar) {
        this.newAvatar = newAvatar;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImUserDbEntity that = (ImUserDbEntity) o;
        return id == that.id;
    }

    @Override
    public String toString() {
        return "ImUserDbEntity{" +
                "id=" + id +
                ", refrence=" + refrence +
                ", avatar='" + avatar + '\'' +
                ", name='" + name + '\'' +
                ", newAvatar='" + newAvatar + '\'' +
                ", newName='" + newName + '\'' +
                ", type=" + type +
                ", hospital='" + hospital + '\'' +
                ", title='" + title + '\'' +
                ", section='" + section + '\'' +
                ", isNewFriend=" + isNewFriend +
                ", isMobileContact=" + isMobileContact +
                ", addFriendContent='" + addFriendContent + '\'' +
                ", addFriendReference=" + addFriendReference +
                ", addFriendOperation=" + addFriendOperation +
                ", brokerPhoneNum='" + brokerPhoneNum + '\'' +
                '}';
    }
}
