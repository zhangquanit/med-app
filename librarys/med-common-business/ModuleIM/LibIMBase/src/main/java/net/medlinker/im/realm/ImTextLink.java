package net.medlinker.im.realm;

import io.realm.RealmObject;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 5.0
 * @description
 * @time 2017/3/30
 */
public class ImTextLink extends RealmObject {

    private int startIndex;
    private int linkLenth;
    private String linkUrl;

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public int getLinkLenth() {
        return linkLenth;
    }

    public void setLinkLenth(int linkLenth) {
        this.linkLenth = linkLenth;
    }

    public int getEndIndex() {
        return startIndex + linkLenth;
    }
}
