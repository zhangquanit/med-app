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

public class BusinessActivity extends AppCompatActivity {

    @BindView(R.id.et_url)
    EditText etUrl;
    @BindView(R.id.et_par)
    EditText etPar;
    @BindView(R.id.tv_par)
    TextView tvPar;
    @BindView(R.id.et_msg)
    EditText etMsg;
    @BindView(R.id.tv_msg)
    TextView tvMsg;
    @BindView(R.id.tv_end)
    TextView tvEnd;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.et_page)
    EditText etPage;

    private boolean isParStr = true;
    private boolean isMsgStr = true;
    private RadioButton mMethod;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus);
        ButterKnife.bind(this);

        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("业务返回失败");

        mMethod = findViewById(R.id.rb_m_1);
        etPage.setText(this.getClass().getName());
    }

    public void doReport(View view) {
        Object par;
        if (isParStr) {
            par = etPar.getText().toString().trim();
        } else {
            par = BaseApplication.bean;
        }
        Object msg;
        if (isMsgStr) {
            msg = etMsg.getText().toString().trim();
        } else {
            msg = BaseApplication.bean;
        }
        LogReport.getInstance().reportBusinessError(
                etUrl.getText().toString().trim(),
                par,
                mMethod.getText().toString().trim().toUpperCase(),
                Integer.parseInt(etCode.getText().toString().trim()),
                msg,
                etPage.getText().toString().trim());
        Toast.makeText(this, "操作成功", Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.rb_par_1, R.id.rb_par_2, R.id.rb_m_1, R.id.rb_m_2, R.id.rb_m_3, R.id.rb_m_4, R.id.rb_msg_1, R.id.rb_msg_2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rb_par_1:
                isParStr = true;
                etPar.setVisibility(View.VISIBLE);
                tvPar.setVisibility(View.GONE);
                break;
            case R.id.rb_par_2:
                isParStr = false;
                etPar.setVisibility(View.GONE);
                tvPar.setVisibility(View.VISIBLE);
                break;
            case R.id.rb_m_1:
            case R.id.rb_m_2:
            case R.id.rb_m_3:
            case R.id.rb_m_4:
                mMethod = (RadioButton) view;
                break;
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