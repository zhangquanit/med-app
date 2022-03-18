package com.medlinker.widget.tab;

import android.content.Context;
import android.content.res.Resources;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import androidx.viewpager.widget.ViewPager;

import com.medlinker.widget.R;
import com.medlinker.widget.tab.indicator.UiTabPageIndicator;
import com.medlinker.widget.tab.sliding.UiTabBg1SlidingTabStrip;
import com.medlinker.widget.tab.slidingtab.UiSlidingTabLayout;
import com.medlinker.widget.util.UiUtil;


/**
 * @author gengyunxiao
 * @time 2021/3/16
 */
public class TabCompatHelper {


    public static void dealTabPageIndicatorStandard(UiTabPageIndicator tabPageIndicator) {
        if (tabPageIndicator == null) {
            return;
        }
        Resources resources = tabPageIndicator.getResources();
        tabPageIndicator.setTabTextSizePx(resources.getDimension(R.dimen.Tab_standard_text_size));
        tabPageIndicator.setTabTextColorRes(R.color.tab_text_standard_selector);
        tabPageIndicator.setIsTextBoldSelected(true);
        tabPageIndicator.setDrawHorizontalIndicatorColorRes(R.color.tab_slide_line);
        tabPageIndicator.setTabDividerWidth(resources.getDimensionPixelSize(R.dimen.Tab_slide_line_width));
        tabPageIndicator.setTabDividerHeight(resources.getDimensionPixelSize(R.dimen.Tab_slide_line_height));
        //tabPageIndicator.setPadding(0, DimenUtil.dip2px(1.0), 0, DimenUtil.dip2px(1.0));
    }

    public static void dealSlidingTabLayoutBg1(final UiSlidingTabLayout slidingTabLayout, final ViewPager vp, final ViewPager.OnPageChangeListener opl) {
        if (slidingTabLayout == null) {
            return;
        }
        Context context = slidingTabLayout.getContext();
        //处理Tab样式
        slidingTabLayout.setBackgroundResource(R.drawable.tab_bg1_oval_out_bg);

        HorizontalScrollView.LayoutParams params = new HorizontalScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        slidingTabLayout.setTabStrip(new UiTabBg1SlidingTabStrip(context), params);

        slidingTabLayout.setDrawBottomUnderLine(false);
        slidingTabLayout.setBottomIndicatorHeight(1);
        slidingTabLayout.setDrawVerticalIndicator(false);
        slidingTabLayout.setDrawHorizontalIndicator(true);
        slidingTabLayout.setBoldTextView(true);


        slidingTabLayout.setCustomTabView(R.layout.tab_view_bg1_module, R.id.tv_title);
        slidingTabLayout.setSelectIndicatorHeight(0);
        slidingTabLayout.setSelectRelativeTextColorsRes(R.color.tab_text_bg1_selected, R.color.tab_text_bg1);
        slidingTabLayout.setSelectedIndicatorColors(
                context.getResources().getColor(R.color.tab_slide_line));
        if (vp != null) {
            slidingTabLayout.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    slidingTabLayout.toogleSelect(vp.getCurrentItem());
                    if (opl != null) {
                        opl.onPageSelected(position);
                    }
                }
            });
        }
    }


    /*public static void dealTabPageIndicatorBg1(TabPageIndicator tabPageIndicator) {
        if(tabPageIndicator==null){
            return;
        }
        //处理Tab样式
        Resources resources = tabPageIndicator.getResources();
        tabPageIndicator.setDraHorizontalIndicator(false);
        tabPageIndicator.setIsTabViewChildFill(true);
        tabPageIndicator.setIsTextBoldSelected(true);
        tabPageIndicator.setTabTextSizePx(resources.getDimensionPixelSize(R.dimen.Tab_bg1_text_size));
        tabPageIndicator.setTabTextColorRes(R.color.tab_text_bg1_selector);
        tabPageIndicator.setCustomTabsBackgroundRes(R.drawable.tab_bg1_oval_selector);
        tabPageIndicator.setTabGroupBackgroundRes(R.drawable.tab_bg1_oval_out_bg);

    }*/

    public static void dealTabPageIndicatorBg2(UiTabPageIndicator tabPageIndicator) {
        if (tabPageIndicator == null) {
            return;
        }
        Context context = tabPageIndicator.getContext();
        //处理Tab样式
        Resources resources = tabPageIndicator.getResources();
        tabPageIndicator.setDraHorizontalIndicator(false);
        tabPageIndicator.setIsTextBoldSelected(false);
        tabPageIndicator.setPadding(UiUtil.dip2px(context,0), UiUtil.dip2px(context,4), UiUtil.dip2px(context,0), UiUtil.dip2px(context,4));
        tabPageIndicator.setTabTextSizePx(resources.getDimensionPixelSize(R.dimen.Tab_bg2_text_size));
        tabPageIndicator.setTabTextColorRes(R.color.tab_text_bg2_selector);
        tabPageIndicator.setCustomTabsBackgroundRes(R.drawable.tab_bg2_oval_selector);
        tabPageIndicator.setIsTabViewChildFill(true);
    }


}
