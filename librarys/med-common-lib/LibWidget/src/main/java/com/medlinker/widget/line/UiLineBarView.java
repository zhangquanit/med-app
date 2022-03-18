package com.medlinker.widget.line;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.medlinker.widget.R;

/**
 * 页面小短线（搭配线） 线条 #DDDDDD
 *
 * @author gengyunxiao
 * @time 2021/3/15
 */

public class UiLineBarView extends UiLineView{
    public UiLineBarView(Context context) {
        super(context);
    }

    public UiLineBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public UiLineBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLineColorId() {
        return R.color.ui_enable_false;
    }
}
