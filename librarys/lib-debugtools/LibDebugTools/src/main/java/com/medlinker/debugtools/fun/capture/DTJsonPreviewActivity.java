package com.medlinker.debugtools.fun.capture;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.didichuxing.doraemonkit.widget.jsonviewer.JsonRecyclerView;
import com.medlinker.debugtools.R;
import com.medlinker.debugtools.base.DTBaseActivity;

public class DTJsonPreviewActivity extends DTBaseActivity {

    private JsonRecyclerView mJsonRecyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_json_preview);
        String data = getIntent().getStringExtra("jsonData");
        mJsonRecyclerView = findViewById(R.id.jsonPreview);
        mJsonRecyclerView.setValueNullColor(Color.RED);
        mJsonRecyclerView.setTextSize(16F);
        mJsonRecyclerView.setScaleEnable(true);
        mJsonRecyclerView.bindJson(data);
    }
}
