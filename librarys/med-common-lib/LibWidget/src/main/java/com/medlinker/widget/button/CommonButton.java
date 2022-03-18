package com.medlinker.widget.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;

import androidx.annotation.Nullable;

import com.medlinker.base.widget.RoundCornerTextView;
import com.medlinker.widget.R;


/**
 * 通用Button
 *
 * @author gengyunxiao
 * @time 2021/3/15
 */
public class CommonButton extends RoundCornerTextView {
    public CommonButton(Context context) {
        super(context);
        initButton();
    }

    public CommonButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initButton();
    }

    public CommonButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initButton();
    }

    private void initButton() {
    }

    @Override
    public void setTextAppearance(int resId) {
        super.setTextAppearance(resId);
    }

    @Override
    public void setTextAppearance(Context context, int resId) {
        //读取自定义属性
        TypedArray typedArray=context.obtainStyledAttributes(resId, R.styleable.RoundCornerTextView);
        dealTypeArray(typedArray);
        //手动居中
        setGravity(Gravity.CENTER);
        //读取TextAppearance，包含TextView的一些属性信息,不包含Gravity
        super.setTextAppearance(context, resId);
    }

}
