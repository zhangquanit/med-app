package com.medlinker.widget.bean.dialog;

/**
 * Des
 *
 * @author gengyunxiao
 * @time 2021/4/21
 */
public class MLActionBean implements IMLActionBean {
    private boolean isSelected;
    private boolean isNoClick;
    private String title;


    public MLActionBean(String title) {
        this.title = title;
    }


    public static MLActionBean build(String title) {
        return new MLActionBean(title);
    }

    @Override
    public String getActionItemTitle() {
        return title;
    }

    @Override
    public boolean isActionItemSelected() {
        return isSelected;
    }

    @Override
    public boolean isActionItemNoClicked() {
        return isNoClick;
    }

    @Override
    public void setActionItemSelected(boolean selected) {
        this.isSelected = selected;
    }

    @Override
    public void setActionItemNoClicked(boolean noClicked) {
        this.isNoClick = noClicked;
    }
}

