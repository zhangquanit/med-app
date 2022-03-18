package com.medlinker.widget.dialog.base;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.medlinker.widget.R;

/**
 * 底部弹窗基类
 * 支持多窗口分屏弹出
 *
 * @author gengyunxiao
 * @time 2021/4/21
 */
public abstract class MLBaseBottomDialogFragment extends MLBaseDialogFragment {

    protected Animation mPushOutAnim;
    protected Animation mPushInAnim;
    protected Animation mAlphaOutAnim;
    protected Animation mAlphaInAnim;

    private int[] mTmpPoint = new int[2];

    private boolean mIsSetDialogDefaultBg = true;

    protected View.OnClickListener mCancelClickListener;//底部弹窗主动取消监听

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initAnim();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (isSetDialogBg())
            setBottomDefaultBg();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void dealWindowManagerLayOutParams(WindowManager.LayoutParams attributes) {
        attributes.width = ViewGroup.LayoutParams.MATCH_PARENT;
        attributes.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        attributes.gravity = Gravity.BOTTOM;
    }

    /**
     * 是否设置底部弹窗的左右上角的圆角背景；拓展其他背景弹窗使用，默认设置
     *
     * @return
     */
    public boolean isSetDialogBg() {
        return mIsSetDialogDefaultBg;
    }

    public void setIsSetDialogBg(boolean isSet) {
        this.mIsSetDialogDefaultBg = isSet;
    }

    /**
     * 设置底部内容区域View的默认BackGroundResource
     */
    private void setBottomDefaultBg() {
        if (mContentView != null) {
            mContentView.setBackgroundResource(R.drawable.dialog_bottom_bg);
        }
    }

    /**
     * 底部弹窗主动取消监听
     * @param cancelClickListener
     * @return
     */
    public MLBaseBottomDialogFragment setCancelClickListener(View.OnClickListener cancelClickListener) {
        this.mCancelClickListener = cancelClickListener;
        return this;
    }


    private void initAnim() {
        mPushInAnim = getInAnimation();
        mPushOutAnim = getOutAnimation();

        mAlphaInAnim = AnimationUtils.loadAnimation(getContext(), R.anim.dialog_fade_in);
        mAlphaOutAnim = AnimationUtils.loadAnimation(getContext(), R.anim.dialog_fade_out);

        mPushInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (mContentView != null && mContentView.getParent() != null && mContentView.getParent() instanceof View) {
                    View rootView = (View) mContentView.getParent();
                    rootView.startAnimation(mAlphaInAnim);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mPushOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (mContentView != null && mContentView.getParent() != null && mContentView.getParent() instanceof View) {
                    View rootView = (View) mContentView.getParent();
                    rootView.startAnimation(mAlphaOutAnim);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mContentView != null) {
                    mContentView.post(new Runnable() {
                        @Override
                        public void run() {
                            MLBaseBottomDialogFragment.super.dismiss();
                        }
                    });
                } else {
                    MLBaseBottomDialogFragment.super.dismiss();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private Animation getInAnimation() {
        return AnimationUtils.loadAnimation(getActivity(), R.anim.dialog_bottom_push_in);
    }

    private Animation getOutAnimation() {
        return AnimationUtils.loadAnimation(getActivity(), R.anim.dialog_bottom_push_out);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    @Override
    public void onStart() {
        super.onStart();

        //支持分屏的动画
        if (mContentView != null)
            mContentView.startAnimation(mPushInAnim);
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(false);
            View rootView = getView().getRootView();
            if (mIsCanceledOnTouchOutside) {
                rootView.setOnTouchListener(onCancelableTouchListener);
            } else {
                rootView.setOnTouchListener(null);
            }
        }
    }

    @Override
    public void dismiss() {
        runOutAnimation();
    }

    /**
     * Called when the user touch on black overlay, in order to dismiss the dialog.
     */
    private final View.OnTouchListener onCancelableTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (!isContain(getView(), event.getRawX(), event.getRawY())) {
                    dismissDialog();
                }

            }
            return false;
        }
    };

    private void runOutAnimation() {
        if (mContentView != null)
            mContentView.startAnimation(mPushOutAnim);
    }

    private boolean isContain(View view, float x, float y) {
        final int[] point = mTmpPoint;
        view.getLocationOnScreen(point);

        if (x >= point[0] && x <= (point[0] + view.getWidth()) && y >= point[1] && y <= (point[1] + view.getHeight())) {
            return true;
        }
        return false;
    }
}
