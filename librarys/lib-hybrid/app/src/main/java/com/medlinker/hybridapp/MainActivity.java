package com.medlinker.hybridapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.medlinker.hybrid.core.ui.WVHybridWebViewActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.btn_ok);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickOk();
            }
        });

//        button.setTextColor(Color.argb(80, Integer.parseInt("f6", 16), Integer.parseInt("70", 16), Integer.parseInt("5e", 16)));
//        button.setTextColor(Color.parseColor("red"));

        String regex = ".+\\.medlinker\\.com$";

        boolean is = "medhybrid.medinker.com".matches(regex);
        Log.i("MedHybrid", "matches =" + is);
        is = "web.medlinker.com".matches(regex);
        Log.d("MedHybrid", "matches =" + is);
        is = "web-qa.medlinker.com".matches(regex);
        Log.i("MedHybrid", "matches =" + is);
        is = "web-gukeapp.medlinker.com".matches(regex);
        Log.d("MedHybrid", "matches =" + is);
        String url = "https://web-diabetes-app.medlinker.com/ih-v2/";
        Uri uri = Uri.parse(url);
        String host = uri.getHost();
        Log.d("MedHybrid", "host =" + host);
        is = host.matches(regex);
        Log.d("MedHybrid", "matches =" + is);
    }

    private void onClickOk() {
        EditText et = findViewById(R.id.et_url);
        String url = et.getText().toString();
        Intent intent = new Intent(this, WVHybridWebViewActivity.class);
        intent.putExtra(WVHybridWebViewActivity.URL, url);
        startActivity(intent);
    }
}