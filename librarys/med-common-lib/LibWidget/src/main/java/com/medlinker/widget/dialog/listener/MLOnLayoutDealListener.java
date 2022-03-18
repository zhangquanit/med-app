package com.medlinker.widget.dialog.listener;

import android.view.View;

/**
 * Des
 *
 * @author gengyunxiao
 * @time 2021/4/29
 */
public interface MLOnLayoutDealListener {
    /**
     * 获得子View
     *
     * @param contentView
     */
    void setupViews(View contentView);

    /**
     * 处理子View
     */
    void updateViews();

}
