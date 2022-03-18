package com.medlinker.widget.tab;

import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

/**
 * @author gengyunxiao
 * @time 2021/3/16
 */
public interface ITab {
    void initTab(int tab_type);
    void setViewPager(ViewPager viewPager);
    void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener);
    TextView getTitleTextView(int index);
    int getSelectedPosition();
}
