package com.medlinker.widget.tab;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.medlinker.widget.R;
import com.medlinker.widget.tab.indicator.UiTabPageIndicator;
import com.medlinker.widget.tab.slidingtab.UiSlidingTabLayout;

import java.util.List;

/**
 * 通用的TAB
 *
 * @author gengyunxiao
 * @time 2021/3/16
 */
public class CommonTab extends FrameLayout implements ITab {

    public static final int TAB_TYPE_STANDARD = 0;
    public static final int TAB_TYPE_BG1_NUM2 = 1;
    public static final int TAB_TYPE_BG1_NUM3 = 2;
    public static final int TAB_TYPE_BG2 = 3;

    private int mTabType = TAB_TYPE_STANDARD;

    private ViewPager.OnPageChangeListener mOnPageChangeListener;

    private ViewPager mViewPager;

    private UiTabPageIndicator tabPageIndicator;
    private UiSlidingTabLayout slidingTabLayout;

    public CommonTab(@NonNull Context context) {
        super(context);
        initTab(mTabType);
    }

    public CommonTab(@NonNull Context context, int tab_type) {
        super(context);
        mTabType = tab_type;
        initTab(tab_type);
    }

    public CommonTab(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttr(context, attrs);
        initTab(mTabType);
    }


    public CommonTab(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        initTab(mTabType);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.CommonTab, 0, 0);
        if (arr != null) {
            mTabType = arr.getInt(R.styleable.CommonTab_tab_type, TAB_TYPE_STANDARD);
            arr.recycle();
        }
    }

    @Override
    public void initTab(int tab_type) {
        switch (tab_type) {
            case CommonTab.TAB_TYPE_STANDARD:
                tabPageIndicator = new UiTabPageIndicator(getContext());
                this.addView(tabPageIndicator);
                TabCompatHelper.dealTabPageIndicatorStandard(tabPageIndicator);
                break;
            case CommonTab.TAB_TYPE_BG1_NUM2:
                View content2 = LayoutInflater.from(getContext()).inflate(R.layout.tab_common_bg1_num2, this);
                slidingTabLayout = content2.findViewById(R.id.slidingTabLayout);
                TabCompatHelper.dealSlidingTabLayoutBg1(slidingTabLayout, mViewPager, mOnPageChangeListener);
                break;
            case CommonTab.TAB_TYPE_BG1_NUM3:
                View content3 = LayoutInflater.from(getContext()).inflate(R.layout.tab_common_bg1_num3, this);
                slidingTabLayout = content3.findViewById(R.id.slidingTabLayout);
                TabCompatHelper.dealSlidingTabLayoutBg1(slidingTabLayout, mViewPager, mOnPageChangeListener);
                break;
            case CommonTab.TAB_TYPE_BG2:
                tabPageIndicator = new UiTabPageIndicator(getContext());
                this.addView(tabPageIndicator);
                TabCompatHelper.dealTabPageIndicatorBg2(tabPageIndicator);
                break;
        }

    }

    @Override
    public void setViewPager(ViewPager viewPager) {
        this.mViewPager = viewPager;
        switch (mTabType) {
            case CommonTab.TAB_TYPE_STANDARD:
            case CommonTab.TAB_TYPE_BG2:
                if (tabPageIndicator != null) {
                    tabPageIndicator.setViewPager(viewPager);
                }
                break;
            case CommonTab.TAB_TYPE_BG1_NUM2:
            case CommonTab.TAB_TYPE_BG1_NUM3:
                if (slidingTabLayout != null) {
                    slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            if (mOnPageChangeListener != null) {
                                mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                            }
                        }

                        @Override
                        public void onPageSelected(int position) {
                            if (mViewPager != null)
                                slidingTabLayout.toogleSelect(mViewPager.getCurrentItem());
                            if (mOnPageChangeListener != null) {
                                mOnPageChangeListener.onPageSelected(position);
                            }
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {
                            if (mOnPageChangeListener != null) {
                                mOnPageChangeListener.onPageScrollStateChanged(state);
                            }
                        }
                    });
                    slidingTabLayout.setViewPager(viewPager);
                }
                break;
        }
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.mOnPageChangeListener = onPageChangeListener;
        switch (mTabType) {
            case CommonTab.TAB_TYPE_STANDARD:
            case CommonTab.TAB_TYPE_BG2:
                if (tabPageIndicator != null) {
                    tabPageIndicator.setOnPageChangeListener(mOnPageChangeListener);
                }
                break;
            case CommonTab.TAB_TYPE_BG1_NUM2:
            case CommonTab.TAB_TYPE_BG1_NUM3:
                break;
        }
    }

    @Override
    public TextView getTitleTextView(int index) {
        TextView titleTv = null;
        switch (mTabType) {
            case CommonTab.TAB_TYPE_STANDARD:
            case CommonTab.TAB_TYPE_BG2:
                if (tabPageIndicator != null) {
                    UiTabPageIndicator.TabView tabView = tabPageIndicator.getTabViewAtPositon(index);
                    if (tabView != null)
                        titleTv = tabView.getTextView();
                }
                break;
            case CommonTab.TAB_TYPE_BG1_NUM2:
            case CommonTab.TAB_TYPE_BG1_NUM3:
                if (slidingTabLayout != null) {
                    List<TextView> titleTextViews = slidingTabLayout.getTitleTextViews();
                    if (titleTextViews != null && index < titleTextViews.size())
                        titleTv = titleTextViews.get(index);
                }
                break;
        }
        return titleTv;
    }

    @Override
    public int getSelectedPosition() {
        switch (mTabType) {
            case CommonTab.TAB_TYPE_STANDARD:
            case CommonTab.TAB_TYPE_BG2:
                if (tabPageIndicator != null) {
                    return tabPageIndicator.getSelectedTabIndex();
                }
                break;
            case CommonTab.TAB_TYPE_BG1_NUM2:
            case CommonTab.TAB_TYPE_BG1_NUM3:
                if (slidingTabLayout != null) {
                    return slidingTabLayout.getmSelectPosition();
                }
                break;
        }
        return 0;
    }

    /**
     * 是否等分Tabs
     *
     * @param isTabEquality
     */
    public void setIsTabEquality(boolean isTabEquality) {
        switch (mTabType) {
            case CommonTab.TAB_TYPE_STANDARD:
                if (tabPageIndicator != null) {
                    tabPageIndicator.setIsTabEquality(isTabEquality);
                }
                break;
            case CommonTab.TAB_TYPE_BG2:
            case CommonTab.TAB_TYPE_BG1_NUM2:
            case CommonTab.TAB_TYPE_BG1_NUM3:
                break;
        }
    }


}
