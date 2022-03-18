package com.medlinker.reactnative.codepush.dialog;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.medlinker.reactnative.codepush.R;

/**
 * @author hmy
 * @time 2020-01-16 10:26
 */
public class RestartRNDialog extends AlertDialog {

    private boolean mIsForceRestart;

    public RestartRNDialog(Context context, boolean isForceRestart) {
        super(context);
        setCancelable(!isForceRestart);
        mIsForceRestart = isForceRestart;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_restart_rn, null);
        setView(view);

        view.findViewById(R.id.positive_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                restartApp();
            }
        });
        if (!mIsForceRestart) {
            view.findViewById(R.id.negative_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        } else {
            view.findViewById(R.id.v_line_center).setVisibility(View.GONE);
            view.findViewById(R.id.negative_button).setVisibility(View.GONE);
        }
        super.onCreate(savedInstanceState);
    }

    private void restartApp() {
        Intent intent = getContext().getPackageManager().getLaunchIntentForPackage(getContext().getPackageName());
        PendingIntent restartIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        if (mgr != null) {
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent); // 1秒钟后重启应用
        }
        // 退出程序
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}
