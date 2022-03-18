package com.medlinker.widget.dialog.base.impl;

import android.view.View;

import androidx.fragment.app.FragmentManager;

import com.medlinker.widget.dialog.base.IMLDealDialog;
import com.medlinker.widget.dialog.base.MLBaseBottomDialogFragment;

/**
 * 抽象基类的实现类
 * <p>
 * 因为抽象的Fragment不能用匿名方式创建
 * Fragment null must be a public static class to be  properly recreated from instance state
 *
 * @author gengyunxiao
 * @time 2021/6/9
 */
public class MLBaseBottomDialogFragmentImpl extends MLBaseBottomDialogFragment {

    private IMLDealDialog mDealDialog;

    public static MLBaseBottomDialogFragmentImpl newInstance(IMLDealDialog imlDealDialog) {
        return new MLBaseBottomDialogFragmentImpl(imlDealDialog);
    }

    public MLBaseBottomDialogFragmentImpl(IMLDealDialog imlDealDialog) {
        this.mDealDialog = imlDealDialog;
    }

    @Override
    public int getDialogLayoutId() {
        if (mDealDialog != null)
            return mDealDialog.getDialogLayoutId();
        return 0;
    }

    @Override
    public void setupViews(View contentView) {
        if (mDealDialog != null)
            mDealDialog.setupViews(contentView);
    }

    @Override
    public void updateViews() {
        if (mDealDialog != null)
            mDealDialog.updateViews();
    }

    @Override
    public void showDialog(FragmentManager manager) {
        if (mDealDialog != null)
            mDealDialog.showDialog(manager);
    }
}
