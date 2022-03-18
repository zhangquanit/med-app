package com.medlinker.widget.dialog.base;

import android.view.View;

import androidx.fragment.app.FragmentManager;

/**
 * Des
 *
 * @author gengyunxiao
 * @time 2021/6/9
 */
public interface IMLDealDialog {
    /**
     * 获得对话框的LayoutId
     *
     * @return
     */
    int getDialogLayoutId();


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

    /**
     * 显示对话框
     *
     * @param manager
     */
    void showDialog(FragmentManager manager);
}
