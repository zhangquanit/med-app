package com.arouter.demo.module.librarya;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;

/**
 * @author zhangquan
 */
@Route(path = "/libA_B/classB")
public class LibA_ClassB extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.liba_classa);
        TextView textView=findViewById(R.id.textView);
        textView.setText("LibA_ClassB");
    }
}
