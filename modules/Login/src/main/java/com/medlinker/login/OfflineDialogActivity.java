package com.medlinker.login;

import android.view.View;


import com.alibaba.android.arouter.facade.annotation.Route;
import com.medlinker.baseapp.route.RoutePath;
import com.medlinker.widget.dialog.MLConfirmDialog;

import net.medlinker.base.base.BaseDialogActivity;

/**
 * 离线弹窗
 *
 * @author hmy
 * @time 2020-03-03 14:29
 */
@Route(path = RoutePath.LOGIN_OFFLINE_DIALOG_ACTIVITY)
public class OfflineDialogActivity extends BaseDialogActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_offline;
    }

    @Override
    protected void onCreated() {
        String msg = getIntent().getStringExtra("DATA_KEY");
        MLConfirmDialog mlConfirmDialog = MLConfirmDialog.newInstance()
                .setTitle(getString(R.string.user_login_offline))
                .setMessage(msg)
                .setOneButton(getString(R.string.user_relogin))
                .setOneButtonListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoginHelper.INSTANCE.logoutIm(v.getContext());
                        RoutePath.INSTANCE.startMainActivityForRelogin(OfflineDialogActivity.this, "relogin");
                        finish();
                    }
                });
        mlConfirmDialog.setCancelable(false);
        mlConfirmDialog.setCanceledOnTouchOutside(false);
        mlConfirmDialog.showDialog(getSupportFragmentManager(), "offline_dialog");
    }

    @Override
    protected boolean canCancelOutside() {
        return false;
    }

    @Override
    protected void onFullScreen() {

    }

    @Override
    public void onBackPressed() {

    }

}
