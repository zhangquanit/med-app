package com.medlinker.widget.dialog.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.medlinker.widget.R;

/**
 * 中间弹窗基类
 *
 * @author gengyunxiao
 * @time 2021/5/17
 */
public abstract class MLBaseCenterDialogFragment extends MLBaseDialogFragment {

    private ImageView closeIv;

    /**
     * 是否显示关闭Icon
     */
    private boolean mIsShowClose = false;


    /**
     * 设置是否显示CloseIcon
     *
     * @param isShowClose
     * @return
     */
    public void setShowClose(boolean isShowClose) {
        this.mIsShowClose = isShowClose;
    }

    /**
     * 是否设置中间弹窗的圆角背景；拓展其他背景弹窗使用，默认设置
     *
     * @return
     */
    public boolean isSetDialogBg() {
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(R.layout.dialog_center_base, container, false);
            FrameLayout contentFl = mContentView.findViewById(R.id.dialog_fl_content_base);
            inflater.inflate(getDialogLayoutId(), contentFl, true);
        }

        ViewGroup parent = (ViewGroup) mContentView.getParent();
        if (parent != null) {
            parent.removeView(mContentView);
        }

        return mContentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        closeIv = mContentView.findViewById(R.id.dialog_iv_close);
        super.onViewCreated(view, savedInstanceState);
        //是否显示弹窗背景
        if (isSetDialogBg()) {
            FrameLayout baseFl = mContentView.findViewById(R.id.dialog_fl_content_base);
            baseFl.setBackgroundResource(R.drawable.dialog_center_bg);
        }
        //close icon
        if (mIsShowClose) {
            closeIv.setVisibility(View.VISIBLE);
            closeIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissDialog();
                }
            });
        }
    }
}
