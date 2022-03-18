package com.medlinker.lib.pay.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.medlinker.lib.pay.PayAPI;
import com.medlinker.lib.pay.R;



public class WXPayEntryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_entry);//透明处理

        try {
            if (PayAPI.getInstance().handleIntent(getIntent())) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        try {
            if (PayAPI.getInstance().handleIntent(intent)) {
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        PayAPI.getInstance().reset();
        super.onDestroy();
    }
}
