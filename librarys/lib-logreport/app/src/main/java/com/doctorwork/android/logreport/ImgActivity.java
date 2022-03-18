package com.doctorwork.android.logreport;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImgActivity extends AppCompatActivity {

    @BindView(R.id.et_url)
    EditText etUrl;
    @BindView(R.id.et_msg)
    EditText etMsg;
    @BindView(R.id.tv_msg)
    TextView tvMsg;
    @BindView(R.id.et_page)
    EditText etPage;
    @BindView(R.id.tv_end)
    TextView tvEnd;

    private boolean isMsgStr = true;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img);
        ButterKnife.bind(this);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("图片加载失败");

        etPage.setText(this.getClass().getName());
    }

    public void doReport(View view) {
        Object msg;
        if (isMsgStr) {
            msg = etMsg.getText().toString().trim();
        } else {
            msg = BaseApplication.bean;
        }
        LogReport.getInstance().reportImageError(
                etUrl.getText().toString().trim(),
                msg,
                etPage.getText().toString().trim());
        Toast.makeText(this, "操作成功", Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.rb_msg_1, R.id.rb_msg_2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rb_msg_1:
                isMsgStr = true;
                etMsg.setVisibility(View.VISIBLE);
                tvMsg.setVisibility(View.GONE);
                break;
            case R.id.rb_msg_2:
                isMsgStr = false;
                etMsg.setVisibility(View.GONE);
                tvMsg.setVisibility(View.VISIBLE);
                break;
        }
    }
}