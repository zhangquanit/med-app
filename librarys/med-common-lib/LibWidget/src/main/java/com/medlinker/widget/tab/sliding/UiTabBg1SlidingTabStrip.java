package com.medlinker.widget.tab.sliding;



import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.medlinker.widget.tab.slidingtab.UiSlidingTabStrip;


/**
 * @author gengyunxiao
 * @time 2021/3/17
 */
public class UiTabBg1SlidingTabStrip extends UiSlidingTabStrip {

    public UiTabBg1SlidingTabStrip(Context context) {
        super(context);
    }

    public UiTabBg1SlidingTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void drawIndicator(int left, int height, int right, int color, Canvas canvas) {
        mRectF.set(left, mSelectedIndicatorThickness, right, height - mSelectedIndicatorThickness);
        mSelectedIndicatorPaint.setAntiAlias(true);
        int r = (height - mSelectedIndicatorThickness) / 2;
        canvas.drawRoundRect(mRectF, r, r, mSelectedIndicatorPaint);
    }
}
