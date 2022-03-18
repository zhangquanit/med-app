package com.medlinker.player.entity;

import androidx.annotation.Keep;

/**
 * @author zhangquan
 */
@Keep
public class MedVideoInfo {
    /**
     * 视频标题
     */
    public String title;
    /**
     * 视频地址
     */
    public String url;
    /**
     * 视频封面
     */
    public String coverUrl;

    /**
     * 其他信息
     */
    public String extraInfo;

    @Override
    public String toString() {
        return "MedVideoInfo{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                ", extraInfo='" + extraInfo + '\'' +
                '}';
    }
}
