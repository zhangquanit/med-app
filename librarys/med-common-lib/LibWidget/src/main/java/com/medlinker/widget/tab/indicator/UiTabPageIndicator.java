/*
 * Copyright (C) 2011 The Android Open Source Project
 * Copyright (C) 2011 Jake Wharton
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
package com.medlinker.widget.tab.indicator;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

import com.medlinker.widget.R;
import com.medlinker.widget.tab.TabCompatHelper;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * This widget implements the dynamic action bar tab behavior that can change
 * across different configurations or circumstances.
 */
public class UiTabPageIndicator extends HorizontalScrollView implements UiPageIndicator {
    /**
     * Title text used when no title is provided by the adapter.
     */
    private static final CharSequence EMPTY_TITLE = "";

    /**
     * Interface for a callback when the selected tab has been reselected.
     */
    public interface OnTabReselectedListener {
        /**
         * Callback when the selected tab has been reselected.
         *
         * @param position Position of the current center item.
         */
        void onTabReselected(int position);
    }

    private Runnable mTabSelector;
    private long mLastClickTime;
    private long mLastDoubleTime;
    private final OnClickListener mTabClickListener = new OnClickListener() {
        public void onClick(View view) {
            TabView tabView = (TabView) view;
            final int oldSelected = mViewPager.getCurrentItem();
            final int newSelected = tabView.getIndex();
            mViewPager.setCurrentItem(newSelected);
            if (oldSelected == newSelected && mTabReselectedListener != null) {
                mTabReselectedListener.onTabReselected(newSelected);
            }
            //双击事件
            final long time = System.currentTimeMillis();
            final long delta = time - mLastClickTime;
            if (delta <= ViewConfiguration.getDoubleTapTimeout() && delta >= 40 && time - mLastDoubleTime > 2000) {//ViewConfiguration.getDoubleTapMinTime() = 40
                tabView.setTag(newSelected);
                if (mDoubleClickListener != null) {
                    mDoubleClickListener.onClick(tabView);
                }
                mLastDoubleTime = time;
            }
            mLastClickTime = time;
        }
    };

    private UiIcsLinearLayout mTabLayout;

    private ViewPager mViewPager;
    private OnPageChangeListener mListener;

    private SparseIntArray mChildWidths = new SparseIntArray();
    private int mMaxTabWidth;
    private int mMaxTextWidth;
    private boolean mMatchParent = false; // 子控件的宽度是否能够填充满父控件
    private int mSelectedTabIndex;

    private OnTabReselectedListener mTabReselectedListener;
    private OnClickListener mDoubleClickListener;
    private int customTabsBackgroundRes;
    private int mTextSizeSp;//sp
    private float mTextSizePx;//像素px
    private int mTextColorRes = 0;
    private boolean mIsTextBoldSelected = false;
    private boolean mIsTabViewChildFill = false;//TextView是否充满TabView
    private boolean mIsTabEquality = true;//Tabs不超过屏宽时是否等分

    private static final int TITLE_OFFSET_DIPS = 24;
    private int mTitleOffset;
    private int mScrollState;
    private int mRedOffset;

    public UiTabPageIndicator(Context context) {
        this(context, null);
    }


    public UiTabPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTab(context);
    }

    private void initTab(Context context) {
        setHorizontalScrollBarEnabled(false);
        mTabLayout = new UiIcsLinearLayout(context);
        mTabLayout.setGravity(Gravity.CENTER);
        mTabLayout.setmDividerWidth(dip2px(26));
        mTitleOffset = (int) (TITLE_OFFSET_DIPS * getResources().getDisplayMetrics().density);
        LayoutParams layoutParams = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        addView(mTabLayout, layoutParams);
        TabCompatHelper.dealTabPageIndicatorStandard(this);//默认标准Tab
    }

    public void setDraHorizontalIndicator(boolean isShow) {
        mTabLayout.setDraHorizontalIndicator(isShow);
    }

    public void setDrawHorizontalIndicatorColorRes(int drawHorizontalIndicatorColorRes) {
        mTabLayout.setDrawHorizontalIndicatorColorRes(drawHorizontalIndicatorColorRes);
    }

    public void setTabDividerWidth(int px) {
        mTabLayout.setmDividerWidth(px);
    }

    public void setTabDividerHeight(int px) {
        mTabLayout.setSelectedIndicatorThickness(px);
    }

    public void setOnTabReselectedListener(OnTabReselectedListener listener) {
        mTabReselectedListener = listener;
    }

    public void setOnTabDoubleClickListener(OnClickListener clickListener) {
        mDoubleClickListener = clickListener;
    }

    public void setCustomTabsBackgroundRes(int backgroundRes) {
        this.customTabsBackgroundRes = backgroundRes;
    }

    public void setTabGroupBackgroundRes(int backgroundRes) {
        if (mTabLayout != null) {
            mTabLayout.setBackgroundResource(backgroundRes);
        }
    }

    public void setTabTextSize(int textSizeSp) {
        this.mTextSizeSp = textSizeSp;
    }

    public void setTabTextSizePx(float textSizePx) {
        this.mTextSizePx = textSizePx;
    }

    public void setTabTextColorRes(int textColorRes) {
        this.mTextColorRes = textColorRes;
    }


    public void setIsTextBoldSelected(boolean isTextBoldSelected) {
        this.mIsTextBoldSelected = isTextBoldSelected;
    }

    public void setIsTabViewChildFill(boolean isTabViewChildFill) {
        this.mIsTabViewChildFill = isTabViewChildFill;
    }

    public void setIsTabEquality(boolean isTabEquality) {
        this.mIsTabEquality = isTabEquality;
        if (!mIsTabEquality) {
            mTabLayout.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            LayoutParams layoutParams = (LayoutParams) mTabLayout.getLayoutParams();
            layoutParams.width = WRAP_CONTENT;
            layoutParams.height = WRAP_CONTENT;
            layoutParams.gravity = Gravity.CENTER_VERTICAL | Gravity.LEFT;
            mTabLayout.setLayoutParams(layoutParams);
        }
    }

    public int getSelectedTabIndex() {
        return mSelectedTabIndex;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final boolean lockedExpanded = widthMode == MeasureSpec.EXACTLY;
        setFillViewport(lockedExpanded);

        /*if (customTabsBackgroundRes != 0 && mIsTabViewChildFill) {
            int allWidth = MeasureSpec.getSize(widthMeasureSpec);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(allWidth - dip2px(7) * 2, widthMode);
        }*/

        final int childCount = mTabLayout.getChildCount();
        if (childCount > 1 && (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST)) {
            float width = MeasureSpec.getSize(widthMeasureSpec);

            if (childCount > 2) {
                int size = mChildWidths.size();
                int totalWidth = 0;
                for (int i = 0; i < size; i++) {
                    totalWidth += mChildWidths.get(i);
                }
                if (totalWidth >= width) {
                    mMatchParent = true;
                } else {
                    mMatchParent = false;
                    if (mMaxTextWidth * childCount < width) {
                        mMaxTabWidth = (int) (width / childCount);
                    }
                }
            } else {
                mMaxTabWidth = (int) (width / 2);
            }
        } else {
            mMaxTabWidth = -1;
        }

        final int oldWidth = getMeasuredWidth();

        /*if (customTabsBackgroundRes != 0 && mIsTabViewChildFill) {
            int allWidth = MeasureSpec.getSize(widthMeasureSpec);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(allWidth + dip2px(7) * 2, widthMode);
        }*/
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int newWidth = getMeasuredWidth();

        if (lockedExpanded && oldWidth != newWidth) {
            // Recenter the tab display if we're at a new (scrollable) size.
            setCurrentItem(mSelectedTabIndex);
        }
    }

    private void animateToTab(final int position) {
        final View tabView = mTabLayout.getChildAt(position);
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
        mTabSelector = new Runnable() {
            public void run() {
                final int scrollPos = tabView.getLeft() - (getWidth() - tabView.getWidth()) / 2;
                smoothScrollTo(scrollPos, 0);
                mTabSelector = null;
            }
        };
        post(mTabSelector);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mTabSelector != null) {
            // Re-post the selector we saved
            post(mTabSelector);
        }
        if (mViewPager != null) {
            scrollToTab(mViewPager.getCurrentItem(), 0);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
    }

    private int addTabIndex = 0;

    private void addTab(int index, CharSequence text, int iconResId) {
        final TabView tabView = new TabView(getContext());
        tabView.mIndex = index;
        tabView.setFocusable(true);
        tabView.setOnClickListener(mTabClickListener);
        TextView tv = tabView.getTextView();

        if (customTabsBackgroundRes != 0) {
            if (mIsTabViewChildFill) {
                if (mViewPager != null && mViewPager.getAdapter() != null) {
                    if (addTabIndex == 0) {
                        ViewGroup.LayoutParams layoutParams = tv.getLayoutParams();
                        if (layoutParams != null && layoutParams instanceof FrameLayout.LayoutParams) {
                            FrameLayout.LayoutParams frameLayoutParams = (LayoutParams) layoutParams;
                            frameLayoutParams.leftMargin = dip2px(12);
                            frameLayoutParams.rightMargin = dip2px(5);
                            frameLayoutParams.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
                        }
                    } else if (addTabIndex == mViewPager.getAdapter().getCount() - 1) {
                        ViewGroup.LayoutParams layoutParams = tv.getLayoutParams();
                        if (layoutParams != null && layoutParams instanceof FrameLayout.LayoutParams) {
                            FrameLayout.LayoutParams frameLayoutParams = (LayoutParams) layoutParams;
                            frameLayoutParams.leftMargin = dip2px(5);
                            frameLayoutParams.rightMargin = dip2px(12);
                            frameLayoutParams.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                        }
                    } else {
                        ViewGroup.LayoutParams layoutParams = tv.getLayoutParams();
                        if (layoutParams != null && layoutParams instanceof FrameLayout.LayoutParams) {
                            FrameLayout.LayoutParams frameLayoutParams = (LayoutParams) layoutParams;
                            frameLayoutParams.leftMargin = dip2px(5);
                            frameLayoutParams.rightMargin = dip2px(5);
                            frameLayoutParams.gravity = Gravity.CENTER;
                        }
                    }
                }
            }
            tv.setBackgroundResource(customTabsBackgroundRes);
        }

        if (mTextSizeSp != 0 && mTextSizeSp > 0) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSizeSp);
        }

        if (mTextSizePx != 0 && mTextSizePx > 0) {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSizePx);
        }

        if (mTextColorRes != 0) {
            tv.setTextColor(ContextCompat.getColorStateList(getContext(), mTextColorRes));
        }

        if (iconResId != 0) {
            tv.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);
        }

        tv.setText(text);

        int width = (int) Layout.getDesiredWidth(text, tv.getPaint());

       /* if (customTabsBackgroundRes != 0 && mIsTabViewChildFill) {
            width = width + dip2px(5) * 2;
        }*/

        width = width + tabView.getPaddingLeft() + tabView.getPaddingRight() + tv.getPaddingLeft() + tv.getPaddingRight();
        mChildWidths.put(index, width);
        mMaxTextWidth = Math.max(width, mMaxTextWidth);
        int height = getResources().getDimensionPixelSize(R.dimen.Tab_standard_height);
        if (mIsTabEquality) {
            mTabLayout.addView(tabView, new LinearLayout.LayoutParams(0, height, 1));
        } else {
            mTabLayout.addView(tabView, new LinearLayout.LayoutParams(width, height));
        }
        addTabIndex++;
    }

    public void setTabPadding(int left, int right) {
        mTabLayout.setPadding(left, 0, right, 0);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        mScrollState = arg0;
        if (mListener != null) {
            mListener.onPageScrollStateChanged(arg0);
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        mTabLayout.onPageScroll(arg0, arg1);
        View selectedTitle = mTabLayout.getChildAt(arg0);
        int extraOffset = (selectedTitle != null)
                ? (int) (arg1 * selectedTitle.getWidth())
                : 0;
        scrollToTab(arg0, extraOffset);
        if (mListener != null) {
            mListener.onPageScrolled(arg0, arg1, arg2);
        }
    }

    @Override
    public void onPageSelected(int arg0) {
        selectedTabIndex(arg0);
        if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
            mTabLayout.onPageScroll(arg0, 0f);
            setCurrentItem(arg0);
        }
        if (mListener != null) {
            mListener.onPageSelected(arg0);
        }
    }

    private void selectedTabIndex(int selectedTabIndex) {
        final int tabCount = mTabLayout.getChildCount();
        for (int i = 0; i < tabCount; i++) {
            final View child = mTabLayout.getChildAt(i);
            final boolean isSelected = (i == selectedTabIndex);
            child.setSelected(isSelected);
            if (mIsTextBoldSelected) {
                TextView textView = child.findViewById(R.id.navigation_tv_title);
                if (textView != null) {
                    TextPaint paint = textView.getPaint();
                    paint.setFakeBoldText(isSelected);
                }
            }
        }
    }

    @Override
    public void setViewPager(ViewPager view) {
        /*if (mViewPager == view) {
            return;
        }*/
        if (mViewPager != null) {
            mViewPager.setOnPageChangeListener(null);
        }
        final PagerAdapter adapter = view.getAdapter();
        if (adapter == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        mViewPager = view;
        if (mViewPager != null) {
            mViewPager.addOnPageChangeListener(this);
            notifyDataSetChanged();
        }

    }

    public void notifyDataSetChanged() {
        mMaxTextWidth = 0;
        mTabLayout.removeAllViews();
        mChildWidths.clear();
        mMatchParent = false;
        PagerAdapter adapter = mViewPager.getAdapter();
        UiIconPagerAdapter iconAdapter = null;
        if (adapter instanceof UiIconPagerAdapter) {
            iconAdapter = (UiIconPagerAdapter) adapter;
        }
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            CharSequence title = adapter.getPageTitle(i);
            if (title == null) {
                title = EMPTY_TITLE;
            }
            int iconResId = 0;
            if (iconAdapter != null) {
                iconResId = iconAdapter.getIconResId(i);
            }
            addTab(i, title, iconResId);
        }
        if (mSelectedTabIndex > count) {
            mSelectedTabIndex = count - 1;
        }
        setCurrentItem(mSelectedTabIndex);
        requestLayout();
    }

    @Override
    public void setViewPager(ViewPager view, int initialPosition) {
        setViewPager(view);
        setCurrentItem(initialPosition);
    }

    @Override
    public void setCurrentItem(int item) {
        if (mViewPager == null) {
//            throw new IllegalStateException("ViewPager has not been bound.");
            return;
        }
        mSelectedTabIndex = item;
        mViewPager.setCurrentItem(item);

    /*    final int tabCount = mTabLayout.getChildCount();
        for (int i = 0; i < tabCount; i++) {
            final View child = mTabLayout.getChildAt(i);
            final boolean isSelected = (i == item);
            child.setSelected(isSelected);
            if (isSelected) {
                animateToTab(item);
            }
        }*/
        selectedTabIndex(item);
//        scrollToTab(item, 0);
    }

    private void scrollToTab(int tabIndex, int positionOffset) {
        final int tabStripChildCount = mTabLayout.getChildCount();
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return;
        }
        View selectedChild = mTabLayout.getChildAt(tabIndex);
        if (selectedChild != null) {
            int targetScrollX = selectedChild.getLeft() + positionOffset;

            if (tabIndex > 0 || positionOffset > 0) {
                // If we're not at the first child and are mid-scroll, make sure we obey the offset
                targetScrollX -= mTitleOffset;
            }
            scrollTo(targetScrollX, 0);
        }
    }

    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mListener = listener;
    }

    public class TabView extends FrameLayout {

        private int mIndex;
        private TextView mTextView;
        private View mTvRedPoint;

        public TabView(Context context) {
            super(context, null, R.attr.vpiTabPageIndicatorStyle);
            mTextView = new TextView(context, null, R.attr.vpiTabPageIndicatorTabsStyle);
            mTextView.setSingleLine();
            mTextView.setId(R.id.navigation_tv_title);
            mTextView.setLines(1);
            mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            mTextView.setGravity(Gravity.CENTER);
            LayoutParams params;
            if (mIsTabViewChildFill) {
                mTextView.setPadding(0, 0, 0, 0);
                mTextView.setHorizontalFadingEdgeEnabled(false);
                mTextView.setFadingEdgeLength(0);
                this.setPadding(0, 0, 0, 0);
                this.setHorizontalFadingEdgeEnabled(false);
                this.setFadingEdgeLength(0);
                params = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
            } else {
                params = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            }
            params.gravity = Gravity.CENTER;
            addView(mTextView, params);

            mTextView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    final int width = dip2px(5);
                    int maginRight = 0;

                    final int rightDistance = (getWidth() - mTextView.getRight());
                    if (rightDistance - getPaddingRight() - 2 > width) {
                        maginRight = rightDistance - getPaddingRight();
                    }

                    mTvRedPoint = new View(getContext());
                    mTvRedPoint.setBackgroundResource(R.drawable.point_ui_red);
                    LayoutParams paramsRedPoint = new LayoutParams(width, width);
                    paramsRedPoint.gravity = Gravity.RIGHT;
                    paramsRedPoint.setMargins(0, mTextView.getTop() + mTextView.getPaddingTop() - mRedOffset, maginRight - mRedOffset, 0);
                    mTvRedPoint.setVisibility(INVISIBLE);
                    addView(mTvRedPoint, paramsRedPoint);
                    if (Build.VERSION.SDK_INT < 16) {
                        mTextView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        mTextView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }

        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = 0;
            if (mIsTabEquality) {
                if (mMatchParent) {
                    width = mChildWidths.get(mIndex);
                } else {
                    width = Math.max(mMaxTabWidth, mMaxTextWidth);
                }
            } else {
                width = mChildWidths.get(mIndex);
            }

            /*if (customTabsBackgroundRes != 0 && mIsTabViewChildFill) {
                width = width - dip2px(5) * 2;
            }*/

            super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), heightMeasureSpec);
        }


        public int getIndex() {
            return mIndex;
        }

        public TextView getTextView() {
            return mTextView;
        }

        public View getRedView() {
            return mTvRedPoint;
        }
    }

    public void setRedOffset(int redOffset) {
        this.mRedOffset = redOffset;
    }

    /**
     * 得到指定位置上面的tabview
     *
     * @param position
     * @return
     */
    public TabView getTabViewAtPositon(int position) {
        return (TabView) mTabLayout.getChildAt(position);
    }

    @Override
    public void setClickable(boolean clickable) {
        super.setClickable(clickable);
        int count = mTabLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            mTabLayout.getChildAt(i).setClickable(clickable);
        }
    }

    public int dip2px(float dipValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
