package com.medlinker.dt.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;

import androidx.appcompat.widget.SwitchCompat;

import com.medlinker.debugtools.base.DTBaseActivity;
import com.medlinker.dt.R;

import net.medlinker.base.storage.KVUtil;

/**
 * 骨科app测试联调
 *
 * @author zhangquan
 */
public class MockActivity extends DTBaseActivity {
    public static void start(Context context) {
        context.startActivity(new Intent(context, MockActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_page);
        initView();
    }

    private void initView() {
        SwitchCompat actionReportSwitch = findViewById(R.id.action_report_toggle);
        actionReportSwitch.setChecked(KVUtil.getBoolean("mock_action_report"));
        actionReportSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                KVUtil.set("mock_action_report", isChecked);
            }
        });


    }
}
