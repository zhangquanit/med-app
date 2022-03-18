package com.medlinker.lib.imagepicker.dialog;

/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 1.0
 * @description 功能描述
 * @time 2016/4/14 19:36
 */
public class DialogViewItem {
    public String name;
    public OnViewItemClickListener itemClickListener;
    public boolean isVisible = true;

    public DialogViewItem(String name, OnViewItemClickListener itemClickListener) {
        this.name = name;
        this.itemClickListener = itemClickListener;
    }

    public DialogViewItem(String name, OnViewItemClickListener itemClickListener,boolean isVisible) {
        this.name = name;
        this.itemClickListener = itemClickListener;
        this.isVisible = isVisible;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
