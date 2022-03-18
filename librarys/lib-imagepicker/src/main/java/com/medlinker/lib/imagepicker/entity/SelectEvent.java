package com.medlinker.lib.imagepicker.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author <a href="mailto:xumingqian@medlinker.net">MingQian.Xu</a>
 * @version 1.0
 * @description 功能描述 大图浏览选择时EventBus消息广播
 * @time 2015/10/27 11:44
 */
public class SelectEvent implements Serializable {


    private int postion;//选择的位置
    private boolean isCheck;//是否被选择

    public SelectEvent(int postion,boolean isCheck) {
        this.postion = postion;
        this.isCheck = isCheck;
    }
    public int getPostion() {
        return postion;
    }
    public boolean isCheck() {
        return isCheck;
    }

}
