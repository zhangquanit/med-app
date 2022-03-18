package com.medlinker.widget.dialog.base;

import com.medlinker.widget.dialog.listener.MLOnDialogDismissListener;

/**
 * Des
 *
 * @author gengyunxiao
 * @time 2021/4/26
 */
public interface IMLDialog extends IMLDealDialog {

    /**
     * 弹窗消失监听
     *
     * @param mlOnDialogDismissListener
     */
    void setOnDialogDismissListener(MLOnDialogDismissListener mlOnDialogDismissListener);

    /**
     * 区域外是否可以取消Dialog
     *
     * @param isCanceledOnTouchOutside
     * @return
     */
     void setCanceledOnTouchOutside(boolean isCanceledOnTouchOutside);

    /**
     * dismiss对话框
     */
    void dismissDialog();
}
