package net.medlinker.im.realm;


import android.text.TextUtils;

import net.medlinker.im.router.ModuleIMManager;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description
 * @time 2017/2/27
 */
public class ImGroupDbEntity extends RealmObject {

    @PrimaryKey
    private long id;
    private RealmList<RealmString> avatarList;
    private String name = "";
    private long owner;
    private int amount;
    private int businessType;//0是普通群 1是 问诊群 2是 企鹅咨询 3 就是小组 4 是随诊群 5 多学科会诊 6 开药门诊
    private long businessId;//群业务id   如businessType！=0.则businessId 为具体业务业务id
    private boolean isSave;//是否保存到通讯录
    private boolean isRejectMsg;//是否消息免打扰
    private String groupQrCode = "";
    private RealmList<ImUserDbEntity> members;//成员
    //5.2新增加字段
    private ImUserDbEntity ownerUser;
    private boolean isOpenGroup = false;//是否是公开群
    private String singleAvatar = "";//单独设置的群头像
    private String groupInfo = "";//公开群设置的群简介
    private long insertTime;//创建时间
    private String shareH5Url = "";//公开群的分享地址
    private int joinStatus;//自己加入该群的维护关系，0未加入 1 已加入 2审核中，这里可以修改和server同步即可
    private String groupTips;
    private String teamName;//对应的小组名字,id为businessId

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<String> getAvatarList() {
        List<String> datas = new ArrayList<>();
        if (TextUtils.isEmpty(singleAvatar)) {//如果设置的头像为空才拼凑群成员头像，否则就显示一个自己设置的头像就好了
            if (avatarList != null) {
                for (RealmString realmString : avatarList) {
                    datas.add(realmString.getString());
                }
            }
        } else {
            datas.add(singleAvatar);
        }
        return datas;
    }

    public void setAvatarList(List<String> avatars) {
        if (this.avatarList == null) {
            this.avatarList = new RealmList<>();
        }
        this.avatarList.clear();
        for (String s : avatars) {
            this.avatarList.add(new RealmString(s));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getOwner() {
        return owner;
    }

    public void setOwner(long owner) {
        this.owner = owner;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isSave() {
        return isSave;
    }

    public void setSave(boolean save) {
        isSave = save;
    }

    public String getGroupQrCode() {
        return groupQrCode;
    }

    public void setGroupQrCode(String groupQrCode) {
        this.groupQrCode = groupQrCode;
    }

    public boolean isOwner() {
        return owner == ModuleIMManager.INSTANCE.getIMService().getCurrentUserId();
    }


    public RealmList<ImUserDbEntity> getMembers() {
        if (members == null) members = new RealmList<>();
        return members;
    }

    public void setMembers(RealmList<ImUserDbEntity> members) {
        this.members = members;
    }

    public boolean isRejectMsg() {
        return isRejectMsg;
    }

    public void setRejectMsg(boolean rejectMsg) {
        isRejectMsg = rejectMsg;
    }

    public long getBusinessId() {
        return businessId;
    }

    public void setBusinessId(long businessId) {
        this.businessId = businessId;
    }

    public int getBusinessType() {
        return businessType;
    }

    public void setBusinessType(int businessType) {
        this.businessType = businessType;
    }

    public void setAvatarList(RealmList<RealmString> avatarList) {
        this.avatarList = avatarList;
    }

    public boolean isOpenGroup() {
        return isOpenGroup;
    }

    public void setOpenGroup(boolean openGroup) {
        isOpenGroup = openGroup;
    }

    public String getSingleAvatar() {
        return singleAvatar;
    }

    public void setSingleAvatar(String singleAvatar) {
        this.singleAvatar = singleAvatar;
    }

    public String getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(String groupInfo) {
        this.groupInfo = groupInfo;
    }

    public long getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(long insertTime) {
        this.insertTime = insertTime;
    }

    public String getShareH5Url() {
        return shareH5Url;
    }

    public void setShareH5Url(String shareH5Url) {
        this.shareH5Url = shareH5Url;
    }

    public int getJoinStatus() {
        return joinStatus;
    }

    public void setJoinStatus(int joinStatus) {
        this.joinStatus = joinStatus;
    }

    public ImUserDbEntity getOwnerUser() {
        return ownerUser;
    }

    public void setOwnerUser(ImUserDbEntity ownerUser) {
        this.ownerUser = ownerUser;
    }

    public String getGroupTips() {
        return groupTips;
    }

    public void setGroupTips(String groupTips) {
        this.groupTips = groupTips;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    @Override
    public String toString() {
        return "ImGroupDbEntity{" +
                "id=" + id +
                ", avatarList=" + avatarList +
                ", name='" + name + '\'' +
                ", owner=" + owner +
                ", amount=" + amount +
                ", businessType=" + businessType +
                ", businessId=" + businessId +
                ", isSave=" + isSave +
                ", isRejectMsg=" + isRejectMsg +
                ", groupQrCode='" + groupQrCode + '\'' +
                ", members=" + members +
                ", ownerUser=" + ownerUser +
                ", isOpenGroup=" + isOpenGroup +
                ", singleAvatar='" + singleAvatar + '\'' +
                ", groupInfo='" + groupInfo + '\'' +
                ", insertTime=" + insertTime +
                ", shareH5Url='" + shareH5Url + '\'' +
                ", joinStatus=" + joinStatus +
                ", groupTips='" + groupTips + '\'' +
                '}';
    }
}
