package net.medlinker.imbusiness.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 卡片消息类型
 *
 * @author jiantao
 * @date 2018/5/15
 */
public class MsgCardEntity implements Parcelable {

    /**
     * 随诊计划类型
     */
    public static final int MSG_TYPE_JSON_CARD_SUIZHEN_PLAN = 1;

    /**
     * 卡片类型，由Native约定
     */
    private int type;

    /**
     * 卡片底部显示的标签
     */
    private String label;


    private String title;

    private String subtitle;

    private String summary;

    /**
     * 图片
     */
    private String image;

    /**
     * targetUrl : 跳转链接
     */
    private String url;

    private String extra;

    public MsgCardEntity() {
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeString(this.label);
        dest.writeString(this.title);
        dest.writeString(this.subtitle);
        dest.writeString(this.summary);
        dest.writeString(this.image);
        dest.writeString(this.url);
        dest.writeString(this.extra);
    }

    protected MsgCardEntity(Parcel in) {
        this.type = in.readInt();
        this.label = in.readString();
        this.title = in.readString();
        this.subtitle = in.readString();
        this.summary = in.readString();
        this.image = in.readString();
        this.url = in.readString();
        this.extra = in.readString();
    }

    public static final Creator<MsgCardEntity> CREATOR = new Creator<MsgCardEntity>() {
        @Override
        public MsgCardEntity createFromParcel(Parcel source) {
            return new MsgCardEntity(source);
        }

        @Override
        public MsgCardEntity[] newArray(int size) {
            return new MsgCardEntity[size];
        }
    };
}
