package com.medlinker.videoplayer;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.launcher.ARouter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private final String mUrl = "http://pub-voice-video.v.medlinker.net/app_improve_image_brand_hd.mp4";
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditText = findViewById(R.id.et_url);
        mEditText.setText(mUrl);
        mEditText.setSelection(mUrl.length());
    }


    public void openPlayer(View view) {
        String url = mEditText.getText().toString();
        if (TextUtils.isEmpty(url)) {
            url = mUrl;
        }
        String title = "视频标题";
        String extra = null;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("seekTo", 10);
            jsonObject.put("autoClose", 1);
            extra = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray componentsArray = new JSONArray();
        componentsArray.put(ExtraComponent.class.getName());

        ARouter.getInstance().build("/videoplayer/vod")
                .withString("title", title)
                .withString("url", url)
                .withBoolean("autoPlay", true)
                .withString("extra", extra)
                .withString("components", componentsArray.toString())
                .navigation();
    }

    public void openPlayerView(View view) {
        String url = mEditText.getText().toString();
        if (TextUtils.isEmpty(url)) {
            url = mUrl;
        }
        PlayerViewDemo.start(this, url);
    }
}