package net.medlinker.imbusiness.entity;

import android.text.TextUtils;

import net.medlinker.base.entity.DataEntity;

/**
 * @author hmy
 * @time 2020/9/30 15:57
 */
public class MarqueeEntity extends DataEntity {
    private String id;
    private String content;
    private String url;
    private int authority; //1:需要检查不足3天显示医生关闭诊室弹窗

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isCanJump() {
        return !TextUtils.isEmpty(url);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isShowCloseClinicDialog() {
        return authority == 1;
    }
}
