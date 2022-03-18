package com.medlinker.widget.tab.slidingtab;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/11/3 2:51 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class UiRoundSlidingTabStrip extends UiSlidingTabStrip {

    public UiRoundSlidingTabStrip(Context context) {
        this(context,null);
    }

    public UiRoundSlidingTabStrip(Context context, AttributeSet attrs) {
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
