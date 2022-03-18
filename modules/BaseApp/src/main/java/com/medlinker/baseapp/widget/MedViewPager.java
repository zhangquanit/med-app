package com.medlinker.baseapp.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Interpolator;

import java.lang.reflect.Field;

/**
 * 扩展的ViewPager
 * <ul>
 * <li>支持3.0以下的PageTransformer</li>
 * <li>支持是否滑动切换页面:setPagingEnabled()</li>
 * </ul>
 *
 * @author zhangquan
 */
public class MedViewPager extends androidx.viewpager.widget.ViewPager {
    public CustomDurationScroller mScroller = null;

    public MedViewPager(Context context) {
        super(context);
        initCustomScroller();
    }

    public MedViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCustomScroller();
    }

    /**
     * 设置自定义的Scroller，用来控制滑动时长
     */
    private void initCustomScroller() {
        try {
            Class<?> viewpager = androidx.viewpager.widget.ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            Field interpolator = viewpager.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);

            mScroller = new CustomDurationScroller(getContext(),
                    (Interpolator) interpolator.get(null));
            scroller.set(this, mScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置滚动时长因子
     *
     * @param scrollFactor
     */
    public void setScrollDurationFactor(float scrollFactor) {
        if (null != mScroller) {
            mScroller.setScrollDurationFactor(scrollFactor);
        }
    }

    // ##################################################
    private boolean pagerEnabled = true;// 是否可以滑动翻页
    private boolean smoothScroll = true;

    /**
     * 是否可以滑动翻页
     *
     * @param pagerEnabled
     */
    public void setPagingEnabled(boolean pagerEnabled) {
        this.pagerEnabled = pagerEnabled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (pagerEnabled) {
            return super.onTouchEvent(event);
        } else {
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (pagerEnabled) {
            return super.onInterceptTouchEvent(event);
        } else {
            return false;
        }

    }

    @Override
    public void setCurrentItem(int paramInt) {
        if (this.smoothScroll) {
            super.setCurrentItem(paramInt);
            return;
        }
        super.setCurrentItem(paramInt, false);
    }

    public void setSmoothScroll(boolean smoothScroll) {
        this.smoothScroll = smoothScroll;
    }

}
