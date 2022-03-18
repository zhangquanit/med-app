package com.medlinker.debugtools.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.medlinker.debugtools.manager.ActivityStashManager;
import com.medlinker.debugtools.widget.DtLoadingDialog;

/**
 * @author: pengdaosong
 * @CreateTime: 2021/3/4 1:42 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class DTBaseActivity extends AppCompatActivity {

    private DtLoadingDialog mLoadingDialog;

    /**
     *
     */
    public void showDialogLoading(boolean cancelable) {
        if (null == mLoadingDialog) {
            mLoadingDialog = DtLoadingDialog.getInstance(cancelable);
        }
        if (isDestroyed() || mLoadingDialog.isAdded() || mLoadingDialog.isVisible()) {
            return;
        }
        mLoadingDialog.show(getSupportFragmentManager());
    }

    /**
     *
     */
    public void hideDialogLoading() {
        if (mLoadingDialog != null && mLoadingDialog.isVisible() && mLoadingDialog.isAdded()) {
            mLoadingDialog.dismissAllowingStateLoss();
            mLoadingDialog = null;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStashManager.onActivityCreated(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityStashManager.onActivityDestroied(this);
    }
}
