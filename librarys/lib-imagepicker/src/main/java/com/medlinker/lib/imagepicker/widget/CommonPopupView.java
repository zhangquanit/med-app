package com.medlinker.lib.imagepicker.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import androidx.core.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;

import com.medlinker.lib.imagepicker.R;

/**
 * @author <a href="mailto:yangjiantao@medlinker.net">Jiantao.Yang</a>
 * @version 3.0
 * @description 弹出框 PopupWindow 喊透明度背景。 默认动画：底部弹出
 * @time 2015/11/19 16:43
 */
public class CommonPopupView extends PopupWindow implements View.OnTouchListener {

    protected Context mContext;

    private int mAnimIn;
    private int mAnimOut;
    private boolean mIsInterceptTouch = true;

    public CommonPopupView(Context context) {
        this.mContext = context;
        //默认动画：底部弹出
//        mAnimIn = R.anim.anim_push_bottom_in;
//        mAnimOut = R.anim.anim_push_bottom_out;
        init(context);
    }

    public void setIsInterceptTouch(boolean isInterceptTouch){
        mIsInterceptTouch = isInterceptTouch;
    }

    private void init(Context context) {
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setAnimationStyle(R.style.popup_style);
        ColorDrawable drawable = new ColorDrawable(context.getResources().getColor(R.color.translucence));
        this.setBackgroundDrawable(drawable);
    }

    @Override
    public boolean onTouch(View v, MotionEvent ev) {
        if (!mIsInterceptTouch){
            return false;
        }
        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE) {
            dismiss();
            return true;
        } else if (action == MotionEvent.ACTION_DOWN) {
            return true;
        }
        return false;
    }

    @Override
    public void setContentView(View contentView) {
        super.setContentView(contentView);
        if (contentView != null) {
            contentView.setOnTouchListener(this);
        }
    }

    public void setContentView(View view, ViewGroup.LayoutParams params) {
        if (view != null) {
            view.setLayoutParams(params);
            setContentView(view);
        }
    }

    public void setAnimationRes(int animIn, int animOut) {
        this.mAnimIn = animIn;
        this.mAnimOut = animOut;
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        startShowAnimation();
    }

    @Override
    public void showAsDropDown(View anchor) {
        super.showAsDropDown(anchor);
        startShowAnimation();
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        super.showAsDropDown(anchor, xoff, yoff);
        startShowAnimation();
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        super.showAsDropDown(anchor, xoff, yoff, gravity);
        startShowAnimation();
    }

    void startShowAnimation() {
        if (mAnimIn != 0) {
            Animation animation = AnimationUtils.loadAnimation(mContext, mAnimIn);
            animation.setFillAfter(true);
            getContentView().setAnimation(animation);
            animation.start();
        }
    }

    @Override
    public void dismiss() {
        if (!isShowing()) {
            return;
        }
        if (mAnimOut != 0) {
            Animation animation = AnimationUtils.loadAnimation(mContext, mAnimOut);
            animation.setFillAfter(true);
            getContentView().clearAnimation();
            animation.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    new Handler().post(new Runnable() {

                        @Override
                        public void run() {
                            CommonPopupView.super.dismiss();
                        }
                    });
                }
            });
            getContentView().setAnimation(animation);
            getContentView().invalidate();
            animation.startNow();
        } else {
            super.dismiss();
        }
    }
}
