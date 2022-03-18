package net.medlinker.imbusiness.emoj;

import java.util.List;

/**
 * 一组表情所对应的实体类
 */
public class EmEaGroupEntity {
    /**
     * 表情数据
     */
    private List<EmojiconEntity> emojiconList;
    /**
     * 图片
     */
    private int icon;
    /**
     * 组名
     */
    private String name;
    /**
     * 表情类型
     */
    private EmojiconEntity.Type type;

    public EmEaGroupEntity() {
    }

    public EmEaGroupEntity(int icon, List<EmojiconEntity> emojiconList) {
        this.icon = icon;
        this.emojiconList = emojiconList;
        type = EmojiconEntity.Type.NORMAL;
    }

    public EmEaGroupEntity(int icon, List<EmojiconEntity> emojiconList, EmojiconEntity.Type type) {
        this.icon = icon;
        this.emojiconList = emojiconList;
        this.type = type;
    }

    public List<EmojiconEntity> getEmojiconList() {
        return emojiconList;
    }

    public void setEmojiconList(List<EmojiconEntity> emojiconList) {
        this.emojiconList = emojiconList;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EmojiconEntity.Type getType() {
        return type;
    }

    public void setType(EmojiconEntity.Type type) {
        this.type = type;
    }


}
