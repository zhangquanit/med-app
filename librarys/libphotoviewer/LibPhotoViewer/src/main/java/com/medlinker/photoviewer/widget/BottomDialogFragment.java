package com.medlinker.photoviewer.widget;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.fragment.app.DialogFragment;

import com.blankj.utilcode.util.SizeUtils;
import com.medlinker.photoviewer.R;


/**
 * @author <a href="mailto:tql@medlinker.net">tqlmorepassion</a>
 * @version 1.0
 * @description 功能描述
 * @time 2015/11/27 16:09
 */
public class BottomDialogFragment extends BaseDialogFragment {

    private int mResLayoutId;
    private int mType = 0;
    private InitViewListener mListener;
    private DialogInterface.OnDismissListener mDismissListener;

    public BottomDialogFragment() {
    }

    /**
     * F
     */
    public static BottomDialogFragment newInstance(int layoutId) {
        BottomDialogFragment dialogFragment = new BottomDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("layout", layoutId);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    /**
     * @param layoutId
     * @param type     0,底部对话框，1居中对话框
     * @return
     */
    public static BottomDialogFragment newInstance(int layoutId, int type) {
        BottomDialogFragment dialogFragment = new BottomDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("layout", layoutId);
        bundle.putInt("type", type);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() == null) {
            return;
        }
        final DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        if (mType == 0) {
            layoutParams.windowAnimations = R.style.dialog_animation;
            layoutParams.width = dm.widthPixels;
            layoutParams.gravity = Gravity.BOTTOM;
        } else if (mType == 1) {
            layoutParams.width = dm.widthPixels * 4 / 5;
            layoutParams.height = dm.widthPixels * 3 / 5;
            layoutParams.gravity = Gravity.CENTER;
            getDialog().setCanceledOnTouchOutside(false);
        } else if (mType == 3) {
            layoutParams.windowAnimations = R.style.dialog_animation;
            layoutParams.width = dm.widthPixels;
            layoutParams.height = dm.heightPixels;
            layoutParams.gravity = Gravity.BOTTOM;
            getDialog().setCanceledOnTouchOutside(false);
            getDialog().setCancelable(false);
        } else if (mType == 4) {
            layoutParams.width = dm.widthPixels * 4 / 5;
//            layoutParams.height = dm.widthPixels * 3 / 5;
            layoutParams.gravity = Gravity.CENTER;
            getDialog().setCanceledOnTouchOutside(false);
        } else if (mType == 5) {
            layoutParams.width = SizeUtils.dp2px(270);
            layoutParams.height = SizeUtils.dp2px(265);
            layoutParams.gravity = Gravity.CENTER;
            getDialog().setCanceledOnTouchOutside(false);
        } else if (mType == 6) {
            layoutParams.width = SizeUtils.dp2px(270);
            layoutParams.height = SizeUtils.dp2px(372.5f);
            layoutParams.gravity = Gravity.CENTER;
            getDialog().setCanceledOnTouchOutside(true);
        } else if (mType == 7) {
            layoutParams.width = SizeUtils.dp2px(302f);
            layoutParams.height = SizeUtils.dp2px(216.5f);
            layoutParams.gravity = Gravity.CENTER;
            getDialog().setCanceledOnTouchOutside(true);
        }
        getDialog().getWindow().setAttributes(layoutParams);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        unpackBundle();
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Dialog_NoActionBar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(mResLayoutId, container);
        if (mListener != null) {
            mListener.setupView(view);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void unpackBundle() {
        Bundle args = getArguments();
        if (args == null) {
            return;
        }
        mResLayoutId = args.getInt("layout");
        mType = args.getInt("type");
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mDismissListener != null) {
            mDismissListener.onDismiss(dialog);
        }
    }

    public void setDismissListener(DialogInterface.OnDismissListener dismissListener) {
        this.mDismissListener = dismissListener;
    }

    /**
     *
     */
    public interface InitViewListener {
        /**
         *
         */
        void setupView(View view);
    }

    public void setInitViewListener(InitViewListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (getDialog() == null) {  // Returns mDialog
            // Tells DialogFragment to not use the fragment as a dialog, and so won't try to use mDialog
            setShowsDialog(false);
        }
        super.onActivityCreated(savedInstanceState);
    }
}
