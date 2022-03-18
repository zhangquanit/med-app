package net.medlinker.base.base;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.medlinker.lib.utils.MedImmersiveModeUtil;
import com.medlinker.widget.dialog.MLLoadingDialog;

import net.medlinker.base.common.DispatchTouchEventListener;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {
    protected FragmentActivity mContext;  //当前activity的下上文对象
    protected MLLoadingDialog mLoadingDialog;
    private List<DispatchTouchEventListener> mTouchEventListeners = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        configImmersiveMode();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTouchEventListeners.clear();
        mTouchEventListeners = null;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        for (DispatchTouchEventListener touchEventListener : mTouchEventListeners) {
            touchEventListener.dispatchTouchEvent(event);
        }
        return super.dispatchTouchEvent(event);
    }

    public void addDispatchTouchEventListener(DispatchTouchEventListener touchEventListener) {
        mTouchEventListeners.add(touchEventListener);
    }


    public void showDialogLoading() {
        showDialogLoading(true);
    }

    public void showDialogLoading(boolean cancelable) {
        if (checkDialog(cancelable)) {
            return;
        }
        mLoadingDialog.show(getSupportFragmentManager());
    }

    public void showDialogLoadingDelay() {
        showDialogLoadingDelay(true);
    }

    public void showDialogLoadingDelay(boolean cancelable) {
        if (checkDialog(cancelable)) {
            return;
        }
        mLoadingDialog.showDelay(getSupportFragmentManager());
    }

    private boolean checkDialog(boolean cancelable) {
        if (null == mLoadingDialog) {
            mLoadingDialog = MLLoadingDialog.getInstance(cancelable);
        }
        if (isDestroyed() || mLoadingDialog.isAdded() || mLoadingDialog.isVisible()) {
            return true;
        }
        return false;
    }

    public void hideDialogLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismissAllowingStateLoss();
            mLoadingDialog = null;
        }
    }

    /**
     * 配置沉浸式模式
     */
    protected void configImmersiveMode() {
        MedImmersiveModeUtil.setDefaultImmersiveMode(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1) {
            getResources();
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        if (null != res && res.getConfiguration().fontScale != 1) { //非默认值
            Configuration newConfig = new Configuration();
            newConfig.fontScale = 1.0f;
//            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }
}
