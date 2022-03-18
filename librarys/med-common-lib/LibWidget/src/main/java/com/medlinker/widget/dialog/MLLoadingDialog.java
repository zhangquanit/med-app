package com.medlinker.widget.dialog;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.medlinker.widget.R;

/**
 * @author <a href="mailto:ganyu@medlinker.net">ganyu</a>
 * @version 3.0
 * @description 加载提示对话框
 * @time 2015/12/21 11:13
 */
public class MLLoadingDialog extends DialogFragment {

    private static final String DEFAULT_TAG = "dialog_loading";
    private boolean mCancelableOnTouchOutside;
    private static final int DIALOG_WHAT = 100;
    private static final int DELAY_TIME = 1000;
    private FragmentManager mManager;

    protected boolean mIsLoading = false;

    protected void setNoTitle() {
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.requestFeature(Window.FEATURE_NO_TITLE);
        }
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            clearHandler();
            int key = msg.what;
            if (key == DIALOG_WHAT) {
                if (null != mManager) {
                    show(mManager);
                }
            }
        }
    };

    /**
     *
     */
    public static MLLoadingDialog getInstance(boolean cancelable) {
        MLLoadingDialog dialog = new MLLoadingDialog();
        dialog.setCancelable(cancelable);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.dialog_ui_loading);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_ui_loading, container);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (getDialog() == null) {
            //Tells DialogFragment to not use the fragment as a dialog, and so won't try to use mDialog
            setShowsDialog(false);
        }
        super.onActivityCreated(savedInstanceState);
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(mCancelableOnTouchOutside);
        }
    }

    /**
     *
     */
    public void show(FragmentManager manager) {
        this.show(manager, DEFAULT_TAG);
    }

    @Override
    public int show(FragmentTransaction ft, String tag) {
        clearHandler();
        if (mIsLoading) {
            return -1;
        }
        try {
            if (!this.isAdded()) {
                ft.add(this, tag);
            } else {
                ft.show(this);
            }
            mIsLoading = true;
            ft.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        clearHandler();
        if (mIsLoading) {
            return;
        }
        try {
            FragmentTransaction ft = manager.beginTransaction();
            if (!this.isAdded() && null == manager.findFragmentByTag(tag)) {
                ft.add(this, tag);
            } else {
                ft.show(this);
            }
            mIsLoading = true;
            ft.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void showNow(@NonNull FragmentManager manager, @Nullable String tag) {
        clearHandler();
        super.showNow(manager, tag);
    }

    /**
     * 延迟线上加载框
     */
    public void showDelay(FragmentManager manager) {
        mManager = manager;
        clearHandler();
        if (mIsLoading) {
            return;
        }
        mHandler.sendEmptyMessageDelayed(DIALOG_WHAT, DELAY_TIME);
    }

    @Override
    public void dismiss() {
        clearHandler();
        if (!mIsLoading) {
            return;
        }
        try {
            super.dismiss();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        mIsLoading = false;
    }

    @Override
    public void dismissAllowingStateLoss() {
        clearHandler();
        if (!mIsLoading) {
            return;
        }
        try {
            super.dismissAllowingStateLoss();
        } catch (Exception ignored) {
        }
    }

    private void clearHandler() {
        mHandler.removeMessages(DIALOG_WHAT);
    }

    public void setCanceledOnTouchOutside(boolean cancelable) {
        mCancelableOnTouchOutside = cancelable;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clearHandler();
    }
}
