package net.medlinker.imbusiness.entity;

import android.text.TextUtils;

import net.medlinker.base.entity.DataEntity;
/**
 * @author hmy
 * @time 2020/9/27 17:58
 */
public class ImMenuEntity extends DataEntity {
    private int id;
    private int check;
    private String title;
    private String icon;
    private String jumpUrl;
    private int jumpType; //1：rn；2：native
    private int resId;

    public ImMenuEntity(String title, int resId, String jumpUrl) {
        this.title = title;
        this.resId = resId;
        this.jumpUrl = jumpUrl;
    }

    public ImMenuEntity() {
    }

    public boolean isNeedCertification() {
        return check == 1;
    }

    public boolean isNeedMultiSited() {
        return check == 2;
    }

    public boolean isJump() {
        return !TextUtils.isEmpty(jumpUrl);
    }

    public boolean isJumpRN() {
        return jumpType == 1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public int getJumpType() {
        return jumpType;
    }

    public void setJumpType(int jumpType) {
        this.jumpType = jumpType;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

}
