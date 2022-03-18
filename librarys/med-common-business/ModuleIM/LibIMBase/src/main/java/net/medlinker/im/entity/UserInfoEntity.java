package net.medlinker.im.entity;

/**
 * @author hmy
 * @time 2020/9/22 19:26
 */
public class UserInfoEntity {
    private long id;
    private String name;
    private String avatar;
    private int type;

    public UserInfoEntity(long id, String name, String avatar, int type) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
