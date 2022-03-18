package com.medlinker.widget.bean.dialog;

/**
 * Des
 *
 * @author gengyunxiao
 * @time 2021/4/21
 */
public interface IMLActionBean {

    String getActionItemTitle();

    boolean isActionItemSelected();

    boolean isActionItemNoClicked();

    void setActionItemSelected(boolean selected);

    void setActionItemNoClicked(boolean noClicked);
}
