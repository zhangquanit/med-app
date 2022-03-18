package com.medlinker.widget.dialog.popup;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import androidx.fragment.app.FragmentManager;

import com.medlinker.widget.R;
import com.medlinker.widget.dialog.base.IMLDialog;
import com.medlinker.widget.dialog.constant.MLDialogConstant;
import com.medlinker.widget.dialog.listener.MLOnDialogDismissListener;

/**
 * MyPopupWindow基类
 * PopupWindow统一管理（如遮罩的透明度）
 * ShadowType.NO,//没有遮罩
 * ShadowType.ALL,//全部遮罩
 * ShadowType.BOTTOM;//底部遮罩
 *
 * @author gengyunxiao
 * @time 2021/4/28
 */
public abstract class MLBasePopupWindow extends PopupWindow implements IMLDialog {

    private ShadowType mShadowType;
    protected Context mContext;
    protected FrameLayout mRootView;
    protected View mContentView;
    private View mBottomShadowView;

    private MLOnDialogDismissListener mlOnDialogDismissListener;
    private boolean mIsCanceledOnTouchOutside = true;


    private int mTmpWindowPos[] = new int[2];
    private int mTmpAnchorLoc[] = new int[2];
    private int mTmpAppScreenLoc[] = new int[2];


    public MLBasePopupWindow(Context context) {
        this.mContext = context;
        initView();

        setCanceledOnTouchOutside(true);

        setContentView(mRootView);

        setAnimationStyle(R.style.dialog_ui_center_animation);

        setupViews(mContentView);

        if (isUpdateViews())
            updateViews();
    }

    /**
     * 获得此PopupWindow的遮罩类型
     *
     * @return
     */
    abstract protected ShadowType getShadowType();

    /**
     * 子类构造函数有新参数，可以手动updateViews
     *
     * @return
     */
    protected boolean isUpdateViews() {
        return true;
    }

    /**
     * ShadowType.Bottom时背景是否显示底部圆角
     *
     * @return
     */
    protected boolean isShowBgBottomRadius() {
        return true;
    }

    public View getDialogContentView() {
        return mContentView;
    }

    protected void initView() {
        mShadowType = getShadowType();
        if (mShadowType == null) {
            mShadowType = ShadowType.BOTTOM;
        }

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        mRootView = new FrameLayout(mContext);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mContentView = layoutInflater.inflate(getDialogLayoutId(), mRootView, false);
        if (mShadowType == ShadowType.ALL) {
            mRootView.setBackgroundResource(R.color.ui_transparent);
            mRootView.addView(mContentView, layoutParams);
            this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

            mRootView.setBackgroundColor(MLDialogConstant.DIALOG_SHADOW_BG_COLOR);
            mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mIsCanceledOnTouchOutside) {
                        dismiss();
                    }
                }
            });
            this.setClippingEnabled(false);

        } else if (mShadowType == ShadowType.BOTTOM) {
            mRootView.setBackgroundResource(R.color.ui_transparent);
            mBottomShadowView = new View(mContext);
            mBottomShadowView.setBackgroundColor(MLDialogConstant.DIALOG_SHADOW_BG_COLOR);
            ViewGroup.LayoutParams layoutParams2 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mRootView.addView(mBottomShadowView, layoutParams2);

            //底部圆边背景
            if (isShowBgBottomRadius())
                mContentView.setBackgroundResource(R.drawable.dialog_top_bg);

            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;

            mRootView.addView(mContentView, layoutParams);
            this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            mBottomShadowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mIsCanceledOnTouchOutside) {
                        dismiss();
                    }
                }
            });
        } else if (mShadowType == ShadowType.NO) {
            mRootView.setBackgroundResource(R.color.ui_transparent);
            mRootView.addView(mContentView, layoutParams);
            this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        this.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));// 这样设置才能铺满屏幕，去掉这句话会出现缝隙
    }

    @Override
    public void setOnDialogDismissListener(MLOnDialogDismissListener onDialogDismissListener) {

        this.mlOnDialogDismissListener = onDialogDismissListener;
    }

    @Override
    public void setCanceledOnTouchOutside(boolean isCanceledOnTouchOutside) {
        this.mIsCanceledOnTouchOutside = isCanceledOnTouchOutside;
        this.setFocusable(isCanceledOnTouchOutside);
        this.setOutsideTouchable(isCanceledOnTouchOutside);
    }

    @Deprecated
    @Override
    public void showDialog(FragmentManager manager) {

    }

    public void showDialog(View anchor) {
        showDialog(anchor, 0, 0);
    }

    public void showDialog(View anchor, int xoff, int yoff) {

        if (mShadowType == ShadowType.ALL) {
            int[] windowPos = calculatePopWindowPos(anchor, mContentView);
            super.showAtLocation(anchor, Gravity.START | Gravity.TOP, 0, 0);

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mContentView.getLayoutParams();
            layoutParams.leftMargin = windowPos[0] + xoff;
            layoutParams.topMargin = windowPos[1] + yoff;
            mRootView.requestLayout();

        } else if (mShadowType == ShadowType.NO) {
            super.showAsDropDown(anchor, xoff, yoff);
        } else if (mShadowType == ShadowType.BOTTOM) {
            int mInitHeight = 0;
            Rect visibleFrame = new Rect();
            anchor.getGlobalVisibleRect(visibleFrame);
            if (mInitHeight == 0) {
                mInitHeight = getHeight();
            }
            int height = mInitHeight - visibleFrame.bottom;
            setHeight(height);
            super.showAsDropDown(anchor, xoff, yoff);
        }
    }

    /**
     * 计算出来的位置，y方向就在anchorView的上面和下面对齐显示，x方向就是与屏幕右边对齐显示
     * 如果anchorView的位置有变化，就可以适当自己额外加入偏移来修正
     *
     * @param anchorView  呼出window的view
     * @param contentView window的内容布局
     * @return window显示的左上角的xOff, yOff坐标
     */
    private int[] calculatePopWindowPos(View anchorView, View contentView) {
        final int windowPos[] = mTmpWindowPos;
        final int anchorLoc[] = mTmpAnchorLoc;
        final int appScreenLoc[] = mTmpAppScreenLoc;

        //多窗口分屏模式兼容
        View rootView = anchorView.getRootView();
        rootView.getLocationOnScreen(appScreenLoc);

        //获取锚点View在屏幕上的左上角坐标位置
        anchorView.getLocationOnScreen(anchorLoc);
        int anchorHeight = anchorView.getHeight();
        int anchorWidth = anchorView.getWidth();
        // 获取屏幕的高宽
        int screenHeight = getScreenHeight(anchorView.getContext());
        int screenWidth = getScreenWidth(anchorView.getContext());
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 计算contentView的高宽
        int windowHeight = contentView.getMeasuredHeight();
        int windowWidth = contentView.getMeasuredWidth();

        //计算出分屏的内相对位置
        anchorLoc[0] = anchorLoc[0] - appScreenLoc[0];
        anchorLoc[1] = anchorLoc[1] - appScreenLoc[1];

        // 判断需要向上弹出还是向下弹出显示
        boolean isNeedShowUp = (screenHeight - anchorLoc[1] - anchorHeight < windowHeight);
        if (isNeedShowUp) {
            windowPos[1] = anchorLoc[1] - windowHeight;
        } else {
            windowPos[1] = anchorLoc[1] + anchorHeight;
        }
        // 判断需要向上弹出还是向下弹出显示
        boolean isNeedShowLeft = (screenWidth - anchorLoc[0] - anchorWidth < windowWidth);
        if (isNeedShowLeft) {
            windowPos[0] = anchorLoc[0] - windowWidth;
        } else {
            windowPos[0] = anchorLoc[0] + anchorWidth;
        }

        return windowPos;
    }

    @Override
    public void dismissDialog() {

        dismiss();

    }

    @Override
    public void dismiss() {
        if (mlOnDialogDismissListener != null) {
            mlOnDialogDismissListener.onDismiss();
        }
        super.dismiss();
    }

    @Deprecated
    @Override
    public void showAsDropDown(View anchor) {
        this.showDialog(anchor);
    }

    @Deprecated
    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        this.showDialog(anchor, xoff, yoff);
    }


    public enum ShadowType {
        NO,//没有遮罩
        ALL,//全部遮罩
        BOTTOM;//底部遮罩
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }
}
