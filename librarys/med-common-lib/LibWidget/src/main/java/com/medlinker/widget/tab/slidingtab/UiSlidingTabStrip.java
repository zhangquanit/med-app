/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.medlinker.widget.tab.slidingtab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;


public class UiSlidingTabStrip extends LinearLayout implements UiSlidingTabLayout.OnTabListener {

    private static final int DEFAULT_BOTTOM_BORDER_THICKNESS_DIPS = 2;
    private static final byte DEFAULT_BOTTOM_BORDER_COLOR_ALPHA = 0x26;
    private static final int SELECTED_INDICATOR_THICKNESS_DIPS = 8;
    private static final int DEFAULT_SELECTED_INDICATOR_COLOR = 0xFF33B5E5;

    private static final int DEFAULT_DIVIDER_THICKNESS_DIPS = 1;
    private static final byte DEFAULT_DIVIDER_COLOR_ALPHA = 0x20;
    private static final float DEFAULT_DIVIDER_HEIGHT = 0.5f;

    /*private final*/ int mBottomBorderThickness;
    private final Paint mBottomBorderPaint;

    protected /*private*/ int mSelectedIndicatorThickness;
    protected final Paint mSelectedIndicatorPaint;

    private final int mDefaultBottomBorderColor;

    private final Paint mDividerPaint;
    private final float mDividerHeight;

    private int mSelectedPosition;
    private float mSelectionOffset;

    private UiSlidingTabLayout.TabColorizer mCustomTabColorizer;
    private final SimpleTabColorizer mDefaultTabColorizer;

    private boolean mDrawHorizontalIndicator = true;
    private boolean mDrawVerticalIndicator;
    boolean mDrawBottomUnderline;
    private int mTextViewId = -1; // 根据文字宽度显示下划线宽度（暂时没用）

    float mBottomBorderIndicatorRatio = 1.0f;
    protected RectF mRectF = new RectF();
    private boolean mIsShowRound = true;


    public UiSlidingTabStrip(Context context) {
        this(context, null);
    }

    public UiSlidingTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);

        final float density = getResources().getDisplayMetrics().density;

        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.colorForeground, outValue, true);
        final int themeForegroundColor = outValue.data;

        mDefaultBottomBorderColor = setColorAlpha(themeForegroundColor,
                DEFAULT_BOTTOM_BORDER_COLOR_ALPHA);

        mDefaultTabColorizer = new SimpleTabColorizer();
        mDefaultTabColorizer.setIndicatorColors(DEFAULT_SELECTED_INDICATOR_COLOR);
        mDefaultTabColorizer.setDividerColors(setColorAlpha(themeForegroundColor,
                DEFAULT_DIVIDER_COLOR_ALPHA));

        mBottomBorderThickness = (int) (DEFAULT_BOTTOM_BORDER_THICKNESS_DIPS * density);
        mBottomBorderPaint = new Paint();
        mBottomBorderPaint.setColor(mDefaultBottomBorderColor);

        mSelectedIndicatorThickness = (int) (SELECTED_INDICATOR_THICKNESS_DIPS * density);
        mSelectedIndicatorPaint = new Paint();

        mDividerHeight = DEFAULT_DIVIDER_HEIGHT;
        mDividerPaint = new Paint();
        mDividerPaint.setStrokeWidth((int) (DEFAULT_DIVIDER_THICKNESS_DIPS * density));

    }

    void setCustomTabColorizer(UiSlidingTabLayout.TabColorizer customTabColorizer) {
        mCustomTabColorizer = customTabColorizer;
        invalidate();
    }

    void setDrawHorizontalIndicator(boolean drawHorizontalIndicator) {
        this.mDrawHorizontalIndicator = drawHorizontalIndicator;
    }

    void setDrawVerticalIndicator(boolean drawVerticalIndicator) {
        this.mDrawVerticalIndicator = drawVerticalIndicator;
    }

    void setSelectedIndicatorColors(int... colors) {
        // Make sure that the custom colorizer is removed
        mCustomTabColorizer = null;
        mDefaultTabColorizer.setIndicatorColors(colors);
        invalidate();
    }

    void setDividerColors(int... colors) {
        // Make sure that the custom colorizer is removed
        mCustomTabColorizer = null;
        mDefaultTabColorizer.setDividerColors(colors);
        invalidate();
    }

    void onViewPagerPageChanged(int position, float positionOffset) {
        mSelectedPosition = position;
        mSelectionOffset = positionOffset;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int height = getHeight();
        final int childCount = getChildCount();
        final UiSlidingTabLayout.TabColorizer tabColorizer = mCustomTabColorizer != null
                ? mCustomTabColorizer
                : mDefaultTabColorizer;
        // Thick colored underline below the current selection
        float ratio = 1 - mBottomBorderIndicatorRatio;
        if (childCount > 0) {
            View selectedTitle = getChildAt(mSelectedPosition);
            int titleWidth = selectedTitle.getWidth();
            int left = (int) (selectedTitle.getLeft() + ratio * titleWidth / 2);
            int right = (int) (left + mBottomBorderIndicatorRatio * titleWidth);

            int color = tabColorizer.getIndicatorColor(mSelectedPosition);

            Log.e("view", "====>left = " + left + " mSelectionOffset = " + mSelectionOffset + " mSelectedPosition = " + mSelectedPosition);
            if (mSelectionOffset > 0f && mSelectedPosition < (getChildCount() - 1)) {
                int nextColor = tabColorizer.getIndicatorColor(mSelectedPosition + 1);
                if (color != nextColor) {
                    color = blendColors(nextColor, color, mSelectionOffset);
                }

                // Draw the selection partway between the tabs
                View nextTitle = getChildAt(mSelectedPosition + 1);
                int newTitleLeft = (int) (nextTitle.getLeft() + ratio * nextTitle.getWidth() / 2);
                int newTitleRight = (int) (newTitleLeft + mBottomBorderIndicatorRatio * nextTitle.getWidth());

                left = (int) (mSelectionOffset * newTitleLeft + (1.0f - mSelectionOffset) * left);
                right = (int) (mSelectionOffset * newTitleRight + (1.0f - mSelectionOffset) * right);
            }
            //bottom indicator
            if (mDrawHorizontalIndicator) {
                mSelectedIndicatorPaint.setColor(color);
                drawIndicator(left,height,right,color,canvas);
            }
        }
        // Vertical separators between the titles
        if (mDrawVerticalIndicator) {
            final int dividerHeightPx = (int) (Math.min(Math.max(0f, mDividerHeight), 1f) * height);
            int separatorTop = (height - dividerHeightPx) / 2;
            for (int i = 0; i < childCount - 1; i++) {
                View child = getChildAt(i);
                mDividerPaint.setColor(tabColorizer.getDividerColor(i));
                canvas.drawLine(child.getRight(), separatorTop, child.getRight(),
                        separatorTop + dividerHeightPx, mDividerPaint);
            }
        }
    }

    protected void drawIndicator(int left,int height,int right,int color,Canvas canvas){
        mRectF.set(left, height - mSelectedIndicatorThickness, right, height);
        if (mIsShowRound) {
            canvas.drawRoundRect(mRectF, 10, 10, mSelectedIndicatorPaint);
        } else {
            canvas.drawRect(mRectF, mSelectedIndicatorPaint);
        }
        // Thin underline along the entire bottom edge
        if (mDrawBottomUnderline) {
            canvas.drawRect(0, height - mBottomBorderThickness, getWidth(), height, mBottomBorderPaint);
        }
    }

    /**
     * 设置是否显示圆角
     */
    public void setIsShowRound(boolean isShowRound) {
        mIsShowRound = isShowRound;
    }

    /**
     * Set the alpha value of the {@code color} to be the given {@code alpha} value.
     */
    private static int setColorAlpha(int color, byte alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    /**
     * Blend {@code color1} and {@code color2} using the given ratio.
     *
     * @param ratio of which to blend. 1.0 will return {@code color1}, 0.5 will give an even blend,
     *              0.0 will return {@code color2}.
     */
    private static int blendColors(int color1, int color2, float ratio) {
        final float inverseRation = 1f - ratio;
        float r = (Color.red(color1) * ratio) + (Color.red(color2) * inverseRation);
        float g = (Color.green(color1) * ratio) + (Color.green(color2) * inverseRation);
        float b = (Color.blue(color1) * ratio) + (Color.blue(color2) * inverseRation);
        return Color.rgb((int) r, (int) g, (int) b);
    }

    @Override
    public void onScroll(int tabIndex, int positionOffset, int targetScrollX) {
        //mRoundLeftX =  getChildAt(tabIndex).getLeft() + positionOffset + mPaddingLeftRight;
    }

    public void setTextViewId(int textViewId) {
        this.mTextViewId = textViewId;
    }

    private static class SimpleTabColorizer implements UiSlidingTabLayout.TabColorizer {
        private int[] mIndicatorColors;
        private int[] mDividerColors;

        @Override
        public final int getIndicatorColor(int position) {
            return mIndicatorColors[position % mIndicatorColors.length];
        }

        @Override
        public final int getDividerColor(int position) {
            return mDividerColors[position % mDividerColors.length];
        }

        void setIndicatorColors(int... colors) {
            mIndicatorColors = colors;
        }

        void setDividerColors(int... colors) {
            mDividerColors = colors;
        }
    }

}
