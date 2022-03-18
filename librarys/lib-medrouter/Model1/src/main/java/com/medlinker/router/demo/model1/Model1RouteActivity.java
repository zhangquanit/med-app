package com.medlinker.router.demo.model1;

import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;

import java.io.Serializable;

/**
 * @author zhangquan
 */
@Route(path = "/model/route")
public class Model1RouteActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        TextView textView = findViewById(R.id.textView);
        textView.setText("Model1RouteActivity");
        String value1 = getIntent().getStringExtra("key1");
        String value2 = getIntent().getStringExtra("key2");
        Serializable seriableData = getIntent().getSerializableExtra("seriable");
        Parcelable parcelabelData = getIntent().getParcelableExtra("parcelabel");
        System.out.println("value1=" + value1 + ",value2=" + value2 + ",seriableData=" + seriableData + ",parcelabelData=" + parcelabelData);

    }
}
