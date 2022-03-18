package com.medlinker.widget.bean.dialog;

/**
 * Des
 *
 * @author gengyunxiao
 * @time 2021/4/22
 */
public class MLWheelBean implements IMLWheelBean {

    private String wheelItemTitle;

    public MLWheelBean(String wheelItemTitle) {
        this.wheelItemTitle = wheelItemTitle;
    }

    public static MLWheelBean build(String wheelItemTitle) {
        return new MLWheelBean(wheelItemTitle);
    }


    @Override
    public String getPickerViewText() {
        return wheelItemTitle;
    }
}
