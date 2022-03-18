package com.medlinker.widget.dialog;

import android.view.View;
import android.widget.FrameLayout;

import androidx.fragment.app.FragmentManager;

import com.medlinker.widget.R;
import com.medlinker.widget.dialog.base.MLBaseBottomDialogFragment;

public class MLBottomCustomViewDialog extends MLBaseBottomDialogFragment {
    FrameLayout frameLayout;
    View content;

    @Override
    public int getDialogLayoutId() {
        return R.layout.dialog_bottom_custom_view;
    }

    @Override
    public void setupViews(View contentView) {
        frameLayout = contentView.findViewById(R.id.content);
        frameLayout.addView(content);
    }

    @Override
    public void updateViews() {
    }

    public MLBottomCustomViewDialog setView(View view) {
        content = view;
        return this;
    }

    @Override
    public void showDialog(FragmentManager manager) {
        show(manager, "CustomViewDialog");

    }

    /**
     * 创建
     *
     * @return
     */
    public static MLBottomCustomViewDialog newInstance() {
        MLBottomCustomViewDialog mlBottomCustomViewDialog = new MLBottomCustomViewDialog();
        return mlBottomCustomViewDialog;
    }
}
