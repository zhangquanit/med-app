package com.arouter.demo.module.librarya;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;

/**
 * @author zhangquan
 */
@Route(path = "/test/libA_classC")
public class LibA_ClassC extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liba_classa);
        TextView textView = findViewById(R.id.textView);
        textView.setText("LibA_ClassC");

        int value1 = getIntent().getIntExtra("key1", 0);
        String value2 = getIntent().getStringExtra("key2");
        System.out.println("value1=" + value1 + ",value2=" + value2);
    }
}
