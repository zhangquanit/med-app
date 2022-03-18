package com.medlinker.widget.line;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.medlinker.widget.R;

/**
 * 页面线条 #F1F1F1
 *
 * @author gengyunxiao
 * @time 2021/3/15
 */
public class UiLineView extends View {
    public UiLineView(Context context) {
        super(context);
        if (!isInEditMode())
            init();
    }

    public UiLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode())
            init();
    }

    public UiLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode())
            init();
    }

    private void init() {
        setBackgroundResource(getLineColorId());
    }

    protected int getLineColorId(){
        return R.color.ui_line;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        if (hMode == MeasureSpec.UNSPECIFIED || hMode == MeasureSpec.AT_MOST) {//不确定情况下为默认
            int heightMeasureSpecAudo = MeasureSpec.makeMeasureSpec(getResources().getDimensionPixelSize(R.dimen.line_h_height), MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, heightMeasureSpecAudo);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }
}
