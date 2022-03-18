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
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * To be used with ViewPager to provide a tab indicator component which give constant feedback as to
 * the user's scroll progress.
 * <p>
 * To use the component, simply add it to your view hierarchy. Then in your
 * {@link android.app.Activity} or {@link Fragment} call
 * {@link #setViewPager(ViewPager)} providing it the ViewPager this layout is being used for.
 * <p>
 * The colors can be customized in two ways. The first and simplest is to provide an array of colors
 * via {@link #setSelectedIndicatorColors(int...)} and {@link #setDividerColors(int...)}. The
 * alternative is via the {@link TabColorizer} interface which provides you complete control over
 * which color is used for any individual position.
 * <p>
 * The views used as tabs can be customized by calling {@link #setCustomTabView(int, int)},
 * providing the layout ID of your custom layout.
 */
public class UiSlidingTabLayout extends HorizontalScrollView {

    private boolean boldTextView;

    private boolean enableClick = true;

    public void setBoldTextView(boolean boldTextView) {
        this.boldTextView = boldTextView;
    }

    /**
     * Allows complete control over the colors drawn in the tab layout. Set with
     * {@link #setCustomTabColorizer(TabColorizer)}.
     */
    public interface TabColorizer {

        /**
         * @param position the position
         * @return return the color of the indicator used when {@code position} is selected.
         */
        int getIndicatorColor(int position);

        /**
         * @param position the position
         * @return return the color of the divider drawn to the right of {@code position}.
         */
        int getDividerColor(int position);

    }

    public static abstract class AbsTabColorizer implements TabColorizer {

        private final Context context;

        public AbsTabColorizer(Context context) {
            this.context = context;
        }

        @Override
        public int getIndicatorColor(int position) {
            return context.getResources().getColor(getIndicatorColorRes(position));
        }

        protected abstract int getIndicatorColorRes(int position);

        protected abstract int getDividerColorRes(int position);

        @Override
        public int getDividerColor(int position) {
            return context.getResources().getColor(getDividerColorRes(position));
        }
    }

    private static final int TITLE_OFFSET_DIPS = 24;
    private static final int TAB_VIEW_PADDING_DIPS = 16;
    private static final int TAB_VIEW_TEXT_SIZE_SP = 12;

    private int mTitleOffset;

    private int mTabViewLayoutId;
    private int mTabViewTextViewId;

    private ViewPager mViewPager;
    private ViewPager.OnPageChangeListener mViewPagerPageChangeListener;

    private UiSlidingTabStrip mTabStrip;

    private List<TextView> mTitleTextViews;
    private List<View> mTabViews;
    private OnTabListener mInternalTabListener;
    private OnInitTitleListener mInitTitleListener;

    private int mSelectTextColor;
    private int mUnSelectTextColor;
    private OnClickRefreshListener mOnClickRefreshListener;

    public int getmSelectPosition() {
        return mSelectPosition;
    }

    /**
     * the select position
     */
    private int mSelectPosition;

    public UiSlidingTabLayout(Context context) {
        this(context, null);
    }

    public UiSlidingTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UiSlidingTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // Disable the Scroll Bar
        setHorizontalScrollBarEnabled(false);
        // Make sure that the Tab Strips fills this View
        setFillViewport(true);

        mTitleOffset = (int) (TITLE_OFFSET_DIPS * getResources().getDisplayMetrics().density);
        mTabStrip = tabStrip(context);
        initTabStrip(null);
    }

    public void setOnClickRefreshListener(OnClickRefreshListener onClickRefreshListener) {
        this.mOnClickRefreshListener = onClickRefreshListener;
    }

    public void setEnableClick(boolean enableClick) {
        this.enableClick = enableClick;
    }

    protected UiSlidingTabStrip tabStrip(Context context){
        return new UiSlidingTabStrip(context);
    }

    public void setTabStrip(UiSlidingTabStrip tabStrip) {
        setTabStrip(tabStrip,null);
    }

    public void setTabStrip(UiSlidingTabStrip tabStrip, LayoutParams params) {
        if (null != mTabStrip){
            removeView(mTabStrip);
        }
        mTabStrip = tabStrip;
        initTabStrip(params);
    }

    private void initTabStrip(LayoutParams params){
        setOnTabListener(mTabStrip);
        if (null == params){
            params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
        params.gravity = Gravity.CENTER_VERTICAL;
        addView(mTabStrip, params);
    }


    /**
     * you should use the weight of {@link android.widget.LinearLayout}
     *
     * @param splitEquality true to equality
     */
    @Deprecated
    public void setSplitWidthEquality(boolean splitEquality) {
        /*if(splitEquality){
            mTabStrip.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    //let child split width
                    final int width = getMeasuredWidth();
                    for (int i = 0, size = mTabStrip.getChildCount(); i < size; i++) {
                        mTabStrip.getChildAt(i).getLayoutParams().width = width / size;
                    }
                    mTabStrip.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }*/
    }

    /**
     * 点击刷新接口
     */
    public interface OnClickRefreshListener {
        void onClickRefresh(int index);
    }

    /**
     * Set the text on the specified tab's TextView.
     *
     * @param index the index of the tab whose TextView you want to update
     * @param text  the text to display on the specified tab's TextView
     */
    public void setTabText(int index, String text) {
        TextView tv = mTitleTextViews.get(index);

        if (tv != null) {
            tv.setText(text);
        }
    }

    public void toogleSelect(int position) {
        if (mSelectPosition == position) return;
        mSelectPosition = position;

        if (mTitleTextViews == null) {
            for (int i = 0, size = mTabStrip.getChildCount(); i < size; i++) {
                ((TextView) mTabStrip.getChildAt(i)).setTextColor(position == i ? mSelectTextColor : mUnSelectTextColor);
                if (boldTextView) {
                    ((TextView) mTabStrip.getChildAt(i)).getPaint().setFakeBoldText(position == i);
                }
            }
        } else {
            for (int i = 0, size = mTitleTextViews.size(); i < size; i++) {
                mTitleTextViews.get(i).setTextColor(position == i ? mSelectTextColor : mUnSelectTextColor);
                if (boldTextView) {
                    mTitleTextViews.get(i).getPaint().setFakeBoldText(position == i);
                }
            }
        }
    }

    public void setSelectIndicatorHeight(int height) {
        mTabStrip.mSelectedIndicatorThickness = height;
    }

    public void setBottomIndicatorHeight(int height) {
        mTabStrip.mBottomBorderThickness = height;
    }

    public void setBottomBorderIndicatorRatio(float ratio) {
        mTabStrip.mBottomBorderIndicatorRatio = ratio;
    }

    /**
     * 设置是否显示圆角
     */
    public void setIsShowRound(boolean isShowRound) {
        mTabStrip.setIsShowRound(isShowRound);
    }

    public void setDrawBottomUnderLine(boolean draw) {
        mTabStrip.mDrawBottomUnderline = draw;
    }

    /**
     * set the select text color and unselect text color.
     *
     * @param selectColor   the color of select
     * @param unSelectColor the color of unselect
     */
    public void setSelectRelativeTextColors(int selectColor, int unSelectColor) {
        mSelectTextColor = selectColor;
        mUnSelectTextColor = unSelectColor;
    }

    public void setSelectRelativeTextColorsRes(int selectColorId, int unSelectColorId) {
        mSelectTextColor = getContext().getResources().getColor(selectColorId);
        mUnSelectTextColor = getContext().getResources().getColor(unSelectColorId);
    }

    /**
     * Set the custom {@link TabColorizer} to be used.
     * <p>
     * If you only require simple custmisation then you can use
     * {@link #setSelectedIndicatorColors(int...)} and {@link #setDividerColors(int...)} to achieve
     * similar effects.
     *
     * @param tabColorizer the Tab Colorizer
     */
    public void setCustomTabColorizer(TabColorizer tabColorizer) {
        mTabStrip.setCustomTabColorizer(tabColorizer);
    }

    public void setDrawHorizontalIndicator(boolean drawHorizontalIndicator) {
        mTabStrip.setDrawHorizontalIndicator(drawHorizontalIndicator);
    }

    public void setDrawVerticalIndicator(boolean drawVerticalIndicator) {
        mTabStrip.setDrawVerticalIndicator(drawVerticalIndicator);
    }

    public List<TextView> getTitleTextViews() {
        return mTitleTextViews;
    }

    public List<View> getTabViews() {
        return mTabViews;
    }


    /**
     * Sets the colors to be used for indicating the selected tab. These colors are treated as a
     * circular array. Providing one color will mean that all tabs are indicated with the same color.
     *
     * @param colors the colors
     */
    public void setSelectedIndicatorColors(int... colors) {
        mTabStrip.setSelectedIndicatorColors(colors);
    }

    /**
     * Sets the colors to be used for tab dividers. These colors are treated as a circular array.
     * Providing one color will mean that all tabs are indicated with the same color.
     *
     * @param colors the colors
     */
    public void setDividerColors(int... colors) {
        mTabStrip.setDividerColors(colors);
    }

    /**
     * Set the {@link ViewPager.OnPageChangeListener}. When using {@link UiSlidingTabLayout} you are
     * required to set any {@link ViewPager.OnPageChangeListener} through this method. This is so
     * that the layout can update it's scroll position correctly.
     *
     * @param listener the listener
     * @see ViewPager#setOnPageChangeListener(ViewPager.OnPageChangeListener)
     */
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mViewPagerPageChangeListener = listener;
    }

    /**
     * Set the custom layout to be inflated for the tab views.
     *
     * @param layoutResId Layout id to be inflated
     * @param textViewId  id of the {@link TextView} in the inflated view
     */
    public void setCustomTabView(int layoutResId, int textViewId) {
        mTabViewLayoutId = layoutResId;
        mTabViewTextViewId = textViewId;
    }

    /**
     * 设置这个之后，会以textview的宽度为准
     *
     * @param textViewId
     */
    public void setTextViewId(int textViewId) {
        mTabStrip.setTextViewId(textViewId);
    }

    /**
     * Sets the associated view pager. Note that the assumption here is that the pager content
     * (number of tabs and tab titles) does not change after this call has been made.
     *
     * @param viewPager the view pager
     */
    public void setViewPager(ViewPager viewPager) {
        mTabStrip.removeAllViews();

        mViewPager = viewPager;
        if (viewPager != null) {
            viewPager.addOnPageChangeListener(new InternalViewPagerListener());
            populateTabStrip();
        }
    }

    public void setViewPagerWithSingleListner(ViewPager viewPager) {
        mTabStrip.removeAllViews();
        mViewPager = viewPager;
        if (viewPager != null) {
            viewPager.setOnPageChangeListener(new InternalViewPagerListener());
            populateTabStrip();
        }
    }

    /*
     * Create a default view to be used for tabs. This is called if a custom tab view is not set via
     * {@link #setCustomTabView(int, int)}.
     *
     */
    protected TextView createDefaultTabView(Context context) {
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TAB_VIEW_TEXT_SIZE_SP);
        textView.setTypeface(Typeface.DEFAULT_BOLD);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // If we're running on Honeycomb or newer, then we can use the Theme's
            // selectableItemBackground to ensure that the View has a pressed state
            TypedValue outValue = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground,
                    outValue, true);
            textView.setBackgroundResource(outValue.resourceId);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // If we're running on ICS or newer, enable all-caps to match the Action Bar tab style
            textView.setAllCaps(true);
        }

        int padding = (int) (TAB_VIEW_PADDING_DIPS * getResources().getDisplayMetrics().density);
        textView.setPadding(padding, padding, padding, padding);

        return textView;
    }

    private void populateTabStrip() {
        final PagerAdapter adapter = mViewPager.getAdapter();
        final OnClickListener tabClickListener = new TabClickListener();
        if (mTitleTextViews != null)
            mTitleTextViews.clear();
        if (mTabViews != null)
            mTabViews.clear();

        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            View tabView = null;
            TextView tabTitleView = null;

            boolean initSuccess = false;
            if (mTabViewLayoutId != 0) {
                // If there is a custom tab view layout id set, try and inflate it
                if (mTitleTextViews == null)
                    mTitleTextViews = new ArrayList<>();
                if (mTabViews == null)
                    mTabViews = new ArrayList<>();
                tabView = LayoutInflater.from(getContext()).inflate(mTabViewLayoutId, mTabStrip,
                        false);
                tabTitleView = tabView.findViewById(mTabViewTextViewId);
                if (mInitTitleListener != null) {
                    initSuccess = mInitTitleListener.onInitTitle(i, tabTitleView);
                }
                mTitleTextViews.add(tabTitleView);
                mTabViews.add(tabView);
            }

            if (tabView == null) {
                tabView = createDefaultTabView(getContext());
                if (mInitTitleListener != null) {
                    initSuccess = mInitTitleListener.onInitTitle(i, (TextView) tabView);
                }
            }

            if (!initSuccess) {
                if (tabTitleView == null) {
                    if (TextView.class.isInstance(tabView)) {
                        tabTitleView = (TextView) tabView;
                    } else {
                        throw new IllegalStateException("tabView not intanceof TextView");
                    }
                }
                tabTitleView.setText(adapter.getPageTitle(i));
            }

            tabView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!enableClick){
                        return;
                    }
                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        if (v == mTabStrip.getChildAt(i)) {
                            if (mSelectPosition == i && mOnClickRefreshListener != null) {
                                mOnClickRefreshListener.onClickRefresh(mSelectPosition);
                            }
                            mViewPager.setCurrentItem(i);
                            mSelectPosition = i;
                            return;
                        }
                    }
                }
            });

            mTabStrip.addView(tabView);
            if (mSelectTextColor != 0) {
                tabTitleView.setTextColor(i == 0 ? mSelectTextColor : mUnSelectTextColor);
            }
            if (boldTextView) {
                tabTitleView.getPaint().setFakeBoldText(i == 0);
            }

        }
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mViewPager != null) {
            scrollToTab(mViewPager.getCurrentItem(), 0);
        }
    }

    private void scrollToTab(int tabIndex, int positionOffset) {
        final int tabStripChildCount = mTabStrip.getChildCount();
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return;
        }

        View selectedChild = mTabStrip.getChildAt(tabIndex);
        if (selectedChild != null) {
            int targetScrollX = selectedChild.getLeft() + positionOffset;

            if (tabIndex > 0 || positionOffset > 0) {
                // If we're not at the first child and are mid-scroll, make sure we obey the offset
                targetScrollX -= mTitleOffset;
            }
            scrollTo(targetScrollX, 0);
            if (mInternalTabListener != null) {
                mInternalTabListener.onScroll(tabIndex, positionOffset, targetScrollX);
            }
        }
    }

    private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        private int mScrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int tabStripChildCount = mTabStrip.getChildCount();
            if ((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount)) {
                return;
            }

            mTabStrip.onViewPagerPageChanged(position, positionOffset);

            View selectedTitle = mTabStrip.getChildAt(position);
            int extraOffset = (selectedTitle != null)
                    ? (int) (positionOffset * selectedTitle.getWidth())
                    : 0;
            scrollToTab(position, extraOffset);

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrolled(position, positionOffset,
                        positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                mTabStrip.onViewPagerPageChanged(position, 0f);
                scrollToTab(position, 0);
            }

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageSelected(position);
            }
            if (mSelectPosition != position)
                mSelectPosition = position;
        }

    }

    private class TabClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {

        }
    }

    private void setOnTabListener(OnTabListener l) {
        mInternalTabListener = l;
    }

    public void setOnInitTitleListener(OnInitTitleListener mInitTitleListener) {
        this.mInitTitleListener = mInitTitleListener;
    }

    public interface OnTabListener {
        void onScroll(int tabIndex, int positionOffset, int targetScrollX);
    }

    /**
     * this must be called
     */
    public interface OnInitTitleListener {

        /**
         * init title
         *
         * @param position the position of this tab
         * @param title    the tab text/title
         * @return true  while init title  success!
         */
        boolean onInitTitle(int position, TextView title);
    }

}
