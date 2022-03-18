package com.medlinker.widget.dialog.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.medlinker.widget.R;
import com.medlinker.widget.dialog.constant.MLDialogConstant;
import com.medlinker.widget.dialog.listener.MLOnDialogDismissListener;

/**
 * MyDialogFragment基类
 *
 * @author gengyunxiao
 * @time 2021/4/21
 */
public abstract class MLBaseDialogFragment extends DialogFragment implements IMLDialog {
    private boolean mIsLoading = false;
    protected View mContentView;

    private MLOnDialogDismissListener mlOnDialogDismissListener;

    /**
     * 区域外取消Dialog
     */
    protected boolean mIsCanceledOnTouchOutside = true;


    protected void setNoTitle() {
        Window window = this.getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(0));
            window.requestFeature(1);
        }

    }

    public void show(FragmentManager manager, String tag) {
        if (!this.mIsLoading) {
            try {
                FragmentTransaction ft = manager.beginTransaction();
                if (!this.isAdded() && null == manager.findFragmentByTag(tag)) {
                    ft.add(this, tag);
                } else {
                    ft.show(this);
                }

                this.mIsLoading = true;
                ft.commitAllowingStateLoss();
            } catch (Exception var4) {
                var4.printStackTrace();
            }

        }
    }

    public int show(FragmentTransaction ft, String tag) {
        if (this.mIsLoading) {
            return -1;
        } else {
            try {
                if (!this.isAdded()) {
                    ft.add(this, tag);
                } else {
                    ft.show(this);
                }

                this.mIsLoading = true;
                ft.commitAllowingStateLoss();
            } catch (Exception var4) {
                var4.printStackTrace();
            }

            return -1;
        }
    }

    /**
     * 区域外是否可以取消Dialog
     */
    @Override
    public void setCanceledOnTouchOutside(boolean isCanceledOnTouchOutside) {
        this.mIsCanceledOnTouchOutside = isCanceledOnTouchOutside;
    }


    @Override
    public void setOnDialogDismissListener(MLOnDialogDismissListener mlOnDialogDismissListener) {
        this.mlOnDialogDismissListener = mlOnDialogDismissListener;
    }

    protected void dealWindowManagerLayOutParams(WindowManager.LayoutParams attributes) {
        attributes.windowAnimations = R.style.dialog_ui_center_animation;
        attributes.gravity = Gravity.CENTER;
        attributes.width = ViewGroup.LayoutParams.MATCH_PARENT;
        attributes.height = ViewGroup.LayoutParams.WRAP_CONTENT;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(getDialogLayoutId(), container);
        }

        ViewGroup parent = (ViewGroup) mContentView.getParent();
        if (parent != null) {
            parent.removeView(mContentView);
        }

        return mContentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupViews(mContentView);
        updateViews();

        //区域外点击是否取消
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(mIsCanceledOnTouchOutside);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {

                window.setBackgroundDrawableResource(R.color.ui_transparent);

                window.setDimAmount(MLDialogConstant.DIALOG_SHADOW_BG_ALPHA);

                View decorView = window.getDecorView();
                if (decorView != null) {
                    decorView.setPadding(0, 0, 0, 0);
                }

                WindowManager.LayoutParams attributes = window.getAttributes();
                if (attributes != null) {
                    dealWindowManagerLayOutParams(attributes);
                    window.setAttributes(attributes);
                }
            }
        }
    }

    public void dismiss() {
        this.dismissAllowingStateLoss();
    }

    @Override
    public void dismissDialog() {
        dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        this.mIsLoading = false;
        if (mlOnDialogDismissListener != null) {
            mlOnDialogDismissListener.onDismiss();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.dialog_ui_style);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (getDialog() == null) {  // Returns mDialog
            // Tells DialogFragment to not use the fragment as a dialog, and so won't try to use mDialog
            setShowsDialog(false);
        }
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

}
