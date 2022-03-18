package com.medlinker.router.demo.model1;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.medlinker.router.MedRouterHelper;
import com.medlinker.router.demo.model1.entity.DataEntity;
import com.medlinker.router.demo.model1.entity.DataEntity2;

import java.io.Serializable;

/**
 * @author zhangquan
 */
public class Model11Activity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        TextView textView = findViewById(R.id.textView);
        textView.setText("Model11Activity");


        String value1 = getIntent().getStringExtra("key1");
        String value2 = getIntent().getStringExtra("key2");
        Serializable seriableData = getIntent().getSerializableExtra("seriable");
        Parcelable parcelabelData = getIntent().getParcelableExtra("parcelabel");
        System.out.println("value1=" + value1 + ",value2=" + value2 + ",seriableData=" + seriableData + ",parcelabelData=" + parcelabelData);

        findViewById(R.id.textView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataEntity dataEntity = new DataEntity();
                dataEntity.param1 = "DataEntity value";

                DataEntity2 dataEntity2 = new DataEntity2();
                dataEntity2.param1 = "DataEntity2 value";

                MedRouterHelper.withUrl("/model/route?key1=value1&key2=222")
                        .withParcelable("parcelabel", dataEntity2)
                        .withSeriablele("seriable", dataEntity)
                        .queryTarget()
                        .navigation(Model11Activity.this);
            }
        });
    }
}
