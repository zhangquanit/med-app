package com.medlinker.debugtools.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.FragmentManager;
import com.medlinker.debugtools.R;

/**
 * @author <a href="mailto:ganyu@medlinker.net">ganyu</a>
 * @version 3.0
 * @description 加载提示对话框
 * @time 2015/12/21 11:13
 */
public class DtLoadingDialog extends DtBaseDialogFragment {
    private static final String DEFAULT_TAG = "dialog_loading";
    private boolean mCancelableOnTouchOutside;

    /**
     *
     */
    public static DtLoadingDialog getInstance(boolean cancelable) {
        DtLoadingDialog dialog = new DtLoadingDialog();
        dialog.setCancelable(cancelable);
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.dt_dialog_loading_style);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dt_view_dialog_loading, container);
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

    public void setCanceledOnTouchOutside(boolean cancelable) {
        mCancelableOnTouchOutside = cancelable;
    }
}
