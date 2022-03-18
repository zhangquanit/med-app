package com.medlinker.router.demo.model1;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.medlinker.router.MedRouterHelper;
import com.medlinker.router.demo.model1.entity.DataEntity;
import com.medlinker.router.demo.model1.entity.DataEntity2;

/**
 * @author zhangquan
 */
//@Route(path = "/model/page1/page2")
public class Model1Activity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        TextView textView = findViewById(R.id.textView);
        textView.setText("Model1Activity");

        findViewById(R.id.textView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataEntity dataEntity = new DataEntity();
                dataEntity.param1 = "DataEntity value";

                DataEntity2 dataEntity2 = new DataEntity2();
                dataEntity2.param1 = "DataEntity2 value";

                MedRouterHelper.withUrl("/model/page2")
                        .withString("key1", "value1")
                        .withString("key2", "222")
                        .withParcelable("parcelabel", dataEntity2)
                        .withSeriablele("seriable", dataEntity)
                        .queryTarget()
                        .navigation(Model1Activity.this);
            }
        });
    }
}
