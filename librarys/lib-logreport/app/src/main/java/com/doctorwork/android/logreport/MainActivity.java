package com.doctorwork.android.logreport;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void reportHttp(View view) {
        startActivity(new Intent(this, HttpActivity.class));
    }

    public void reportBusiness(View view) {
        startActivity(new Intent(this, BusinessActivity.class));
    }

    public void reportImg(View view) {
        startActivity(new Intent(this, ImgActivity.class));
    }

    public void reportCustom(View view) {
        startActivity(new Intent(this, CustomActivity.class));
    }

    public void reportPoint(View view) {
        startActivity(new Intent(this, PointActivity.class));
    }


}