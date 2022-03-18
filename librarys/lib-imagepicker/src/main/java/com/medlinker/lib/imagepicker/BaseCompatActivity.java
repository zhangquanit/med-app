package com.medlinker.lib.imagepicker;/**
 * Created by kuiwen on 2015/10/30.
 */

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;


import com.blankj.utilcode.util.ConvertUtils;
import com.medlinker.widget.navigation.CommonNavigationBar;

import net.medlinker.base.base.BaseActivity;


/**
 * @author <a href="mailto:zhangkuiwen@medlinker.net">Kuiwen.Zhang</a>
 * @version 1.0
 * @description 功能描述
 * @time 2015/10/30 11:05
 **/
public class BaseCompatActivity extends BaseActivity {

    /*protected NavigationBar mNavigationBar;*/
    protected CommonNavigationBar mNavigationBar;
    private View mContent;
    private View mShadowView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*mNavigationBar = new NavigationBar(this);
        mNavigationBar.setId(R.id.base_navigation_bar);
        mNavigationBar.setLeftIcon(R.mipmap.pick_icon_naviback_gray);
        mNavigationBar.setLeftButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        initActionBar(mNavigationBar);*/
        mNavigationBar = new CommonNavigationBar(this);
        mNavigationBar.setId(R.id.base_navigation_bar);
        mNavigationBar.showBackIcon(v -> {
            onBackPressed();
        });
        initActionBar(mNavigationBar);
    }

    @Override
    public void setContentView(int layoutResID) {
        setContentView(getLayoutInflater().inflate(layoutResID, null));
    }

    protected boolean needShadow() {
        return true;
    }

    @Override
    public void setContentView(View view) {
        if (needShadow()) {
            mContent = view;
            FrameLayout mFrameLayout = new FrameLayout(this);
            mFrameLayout.addView(this.mNavigationBar, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            params.topMargin = ConvertUtils.dp2px(48);
            mFrameLayout.addView(mContent, params);

            mShadowView = new View(this);
            mShadowView.setBackgroundResource(R.mipmap.bg_shadow);
            FrameLayout.LayoutParams shadowParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ConvertUtils.dp2px(5));
            shadowParams.topMargin = ConvertUtils.dp2px(48);
            mFrameLayout.addView(mShadowView, shadowParams);
            super.setContentView(mFrameLayout);
        } else {
            mContent = view;
            FrameLayout mFrameLayout = new FrameLayout(this);
            mFrameLayout.addView(this.mNavigationBar, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            params.topMargin = ConvertUtils.dp2px(48);
            mFrameLayout.addView(mContent, params);
            super.setContentView(mFrameLayout);
        }
    }

    public CommonNavigationBar getNavigationBar() {
        return mNavigationBar;
    }

    protected void initActionBar(CommonNavigationBar navigationBar) {
    }

    public void hideNavigationBar() {
        if (mNavigationBar.getVisibility() != View.GONE) {
            mNavigationBar.setVisibility(View.GONE);
            if (mShadowView != null) {
                mShadowView.setVisibility(View.GONE);
            }
            if (mContent != null) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mContent.getLayoutParams();
                layoutParams.topMargin = 0;
                mContent.setLayoutParams(layoutParams);
            }
        }
    }

    public void showNavigationBar() {
        if (mNavigationBar.getVisibility() != View.VISIBLE) {
            mNavigationBar.setVisibility(View.VISIBLE);
            mShadowView.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mContent.getLayoutParams();
            layoutParams.topMargin = ConvertUtils.dp2px(48);
            mContent.setLayoutParams(layoutParams);
        }
    }

    public void setNavigationDarkTheme() {
        mNavigationBar.setBackgroundColor(0xff2a2c31);
        /*mNavigationBar.setLeftIcon(R.mipmap.pick_icon_return_white);
        mNavigationBar.getRightText().setTextColor(Color.WHITE);
        mNavigationBar.getLeftText().setTextColor(Color.WHITE);*/
        mNavigationBar.showBackWhiteIcon(null);
        mNavigationBar.setRightTextColor(R.color.navigation_text_white_selector);
        mNavigationBar.setLeftTextColor(R.color.navigation_text_white_selector);
    }
}
