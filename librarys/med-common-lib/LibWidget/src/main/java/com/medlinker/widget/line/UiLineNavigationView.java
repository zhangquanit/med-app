package com.medlinker.widget.line;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.medlinker.widget.R;

/**
 * 标题栏（CommonNavigationBar底部） 线条 #eeeeee
 *
 * @author gengyunxiao
 * @time 2021/3/15
 */

public class UiLineNavigationView extends UiLineView{
    public UiLineNavigationView(Context context) {
        super(context);
    }

    public UiLineNavigationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public UiLineNavigationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getLineColorId() {
        return R.color.ui_line_bar;
    }
}
