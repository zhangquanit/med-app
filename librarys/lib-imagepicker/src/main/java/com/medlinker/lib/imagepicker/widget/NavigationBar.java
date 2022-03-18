package com.medlinker.lib.imagepicker.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.cn.glidelib.glide.GlideApp;
import com.medlinker.lib.imagepicker.R;

/**
 * @author <a href="mailto:yangjiantao@medlinker.net">Jiantao.Yang</a>
 * @version 3.0
 * @description 统一导航栏样式
 * @time 2015/10/27 11:11
 */
public class NavigationBar extends FrameLayout {

    private TextView mTitle;

    private TextView mLeftText;

    private ImageView mLeftIcon;

    private TextView mRightText;

    private ImageView mRightIcon;

    private View mLeftButtonLayout;
    private View mRightButtonLayout;
    private ViewGroup mRightExtraViewLayout;

    public NavigationBar(Context context) {
        super(context);
        initView();
    }

    public NavigationBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public NavigationBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View content = LayoutInflater.from(getContext()).inflate(R.layout.pick_navigation_bar, this);
        mLeftButtonLayout = content.findViewById(R.id.left_button_layout);
        mRightButtonLayout = content.findViewById(R.id.right_button_layout);
        mTitle = content.findViewById(R.id.title);

        mLeftIcon = content.findViewById(R.id.left_icon);
        mLeftText = content.findViewById(R.id.left_text);

        mRightIcon = content.findViewById(R.id.right_icon);
        mRightText = content.findViewById(R.id.right_text);
        mRightExtraViewLayout = content.findViewById(R.id.ll_right_extra_view_layout);
        this.setBackgroundColor(getResources().getColor(R.color.white));
    }

    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            mTitle.setText(title);
        }
    }

    public void setTitleGravity(int gravity) {
        mTitle.setGravity(gravity);
    }

    public void setTitle(int resId) {
        setTitle(getContext().getString(resId));
    }

    public void setLeftIcon(int resId) {
        mLeftIcon.setImageResource(resId);

        isShowLeftIcon(true);
    }

    public void setLeftIcon(Bitmap bitmap) {
        this.mLeftIcon.setImageBitmap(bitmap);
        isShowLeftIcon(true);
    }

    public void setLeftText(String leftStr) {
        if (!TextUtils.isEmpty(leftStr)) {
            mLeftText.setText(leftStr);
        }

        isShowLeftIcon(false);
    }

    public void setLeftText(Spanned leftCharse) {
        if (!TextUtils.isEmpty(leftCharse)) {
            mLeftText.setText(leftCharse);
        }

        isShowLeftIcon(false);
    }

    public void setRightGone() {
        mRightButtonLayout.setVisibility(GONE);
        mRightText.setVisibility(GONE);
        mRightIcon.setVisibility(GONE);
    }

    public void setRightVisible() {
        mRightButtonLayout.setVisibility(VISIBLE);
        mRightText.setVisibility(VISIBLE);
        mRightIcon.setVisibility(VISIBLE);
    }

    public void setLeftGone() {
        mLeftButtonLayout.setVisibility(GONE);
        mLeftText.setVisibility(GONE);
        mLeftIcon.setVisibility(GONE);
    }

    public void setLeftInvisiable() {
        mLeftIcon.setVisibility(INVISIBLE);
    }

    public void setLeftIconInvisiable() {
        mLeftIcon.setVisibility(INVISIBLE);
    }

    public void setLeftVisible() {
        mLeftButtonLayout.setVisibility(VISIBLE);
        mLeftText.setVisibility(VISIBLE);
        mLeftIcon.setVisibility(VISIBLE);
    }


    public void setLeftText(int resId) {
        setLeftText(getContext().getString(resId));
    }

    public void setRightIcon(int resId) {
        mRightIcon.setImageResource(resId);

        isShowRightIcon(true);
    }

    public void setRightIconRotation(int rotation) {
        mRightIcon.setRotation(rotation);
    }

    public void setRightIcon(Bitmap bitmap) {
        this.mRightIcon.setImageBitmap(bitmap);
        isShowRightIcon(true);
    }

    public void setRightText(String rightStr) {
        if (!TextUtils.isEmpty(rightStr)) {
            mRightText.setText(rightStr);
        }

        isShowRightIcon(false);
    }

    public void setRightText(int resId) {
        setRightText(getContext().getString(resId));
    }

    public void setRightTextColor(int resId) {
        mRightText.setTextColor(getResources().getColor(resId));
    }

    public void setLeftButtonOnClickListener(OnClickListener listener) {
        mLeftButtonLayout.setOnClickListener(listener);
    }

    public void setRightButtonOnClickListener(OnClickListener listener) {
        mRightButtonLayout.setOnClickListener(listener);
    }

    public void setTitleOnClickListener(OnClickListener listener) {
        mTitle.setOnClickListener(listener);
    }

    public void setRightButtonEnable(boolean enabled) {
        mRightButtonLayout.setEnabled(enabled);
        mRightIcon.setEnabled(enabled);
        mRightText.setEnabled(enabled);
    }

    private void isShowLeftIcon(boolean isShow) {
        mLeftText.setVisibility(isShow ? View.GONE : View.VISIBLE);
        mLeftIcon.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    private void isShowRightIcon(boolean isShow) {
        mRightText.setVisibility(isShow ? View.GONE : View.VISIBLE);
        mRightIcon.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 新增一个iconview，注意点击事件
     */
    public void insertIconViewInRightExtraLayout(Uri uri, OnClickListener listener) {
        if (uri != null) {
            ImageView iconView = new ImageView(getContext());
            iconView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            iconView.setLayoutParams(
                    new LinearLayout.LayoutParams(ConvertUtils.dp2px(34), ConvertUtils.dp2px(34)));
            int paddingValue = ConvertUtils.dp2px(2);
            iconView.setPadding(paddingValue, paddingValue, paddingValue, paddingValue);
            mRightExtraViewLayout.addView(iconView, 0);
            GlideApp.with(getContext()).load(uri)
                    .apply(new RequestOptions().transform(new CircleCrop())).into(iconView);
            iconView.setOnClickListener(listener);
        }
    }

    public void resetRightExtraViewLayout() {
        mRightExtraViewLayout.removeAllViews();
    }

    public View getRightButtonLayout() {
        return mRightButtonLayout;
    }

    public View getLeftButtonLayout() {
        return mLeftButtonLayout;
    }

    public TextView getTitle() {
        return mTitle;
    }

    public TextView getLeftText() {
        return mLeftText;
    }

    public ImageView getLeftIcon() {
        return mLeftIcon;
    }

    public TextView getRightText() {
        return mRightText;
    }

    public ImageView getRightIcon() {
        return mRightIcon;
    }

}
