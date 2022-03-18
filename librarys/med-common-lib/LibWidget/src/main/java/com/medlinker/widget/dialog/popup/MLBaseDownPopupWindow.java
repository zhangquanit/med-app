package com.medlinker.widget.dialog.popup;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;

import com.medlinker.widget.R;

/**
 * 带下拉动画的PopupWindow基类
 *
 * @author gengyunxiao
 * @time 2021/5/11
 */
public abstract class MLBaseDownPopupWindow extends MLBasePopupWindow {


    private Animation mAlphaOpenAnimation;
    private Animation mAlphaCloseAnimation;
    private Animation mPopOpenAnimation;
    private Animation mPopCloseAnimation;


    private int mDuration = 300;
    private boolean mIsDismissed = true;

    public MLBaseDownPopupWindow(Context context) {
        super(context);
    }

    @Override
    protected void initView() {
        super.initView();
        if (getShadowType() == ShadowType.BOTTOM) {
            mDuration = mContext.getResources().getInteger(R.integer.dialog_animation_default_duration);
            initAnim();
        }
    }

    @Override
    protected ShadowType getShadowType() {
        return ShadowType.BOTTOM;
    }

    @Override
    public void dismiss() {
        if (getShadowType() == ShadowType.BOTTOM) {
            executeExitAnim();
        } else {
            super.dismiss();
        }
    }

    private void executeExitAnim() {
        if (!mIsDismissed) {
            if (mContentView != null) {
                mContentView.startAnimation(mPopCloseAnimation);
            }
        }
    }

    @Override
    public void showDialog(View anchor, int xoff, int yoff) {
        if (getShadowType() == ShadowType.BOTTOM) {
            if (mIsDismissed) {
                if (mContentView != null) {
                    mContentView.startAnimation(mPopOpenAnimation);
                }
                super.showDialog(anchor, xoff, yoff);
            }
        } else {
            super.showDialog(anchor, xoff, yoff);
        }
    }

    private void initAnim() {
        mPopOpenAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation
                .RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0);
        mPopOpenAnimation.setDuration(mDuration);
        mPopCloseAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
        mPopCloseAnimation.setDuration(mDuration);
        mPopCloseAnimation.setFillAfter(true);

        mPopOpenAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mIsDismissed = false;
                mRootView.startAnimation(mAlphaOpenAnimation);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mPopCloseAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mContentView != null) {
                    mContentView.post(mDismissRunnable);
                }
            }

            @Override
            public void onAnimationStart(Animation animation) {
                mIsDismissed = true;
                mRootView.startAnimation(mAlphaCloseAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mAlphaOpenAnimation = AnimationUtils.loadAnimation(mContext, R.anim.dialog_fade_in);
        mAlphaCloseAnimation = AnimationUtils.loadAnimation(mContext, R.anim.dialog_fade_out);
    }

    private Runnable mDismissRunnable = new Runnable() {
        @Override
        public void run() {
            MLBaseDownPopupWindow.super.dismiss();
        }
    };


}
