package com.medlinker.debugtools.fun.capture;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.medlinker.debugtools.R;
import com.medlinker.debugtools.base.DTBaseActivity;
import com.medlinker.debugtools.entity.DTNetworkCaptureEntity;
import com.medlinker.debugtools.utils.JsonUtils;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/4 7:30 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class DTNetworkDetailActivity extends DTBaseActivity {

    private TextView mRequestTextView;
    private TextView mResponseTextView;
    private DTNetworkCaptureEntity mDTNetworkCaptureEntity;
    private TextView mRequestTitle;
    private TextView mResponseTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_info);
        mRequestTextView = findViewById(R.id.requestContent);
        mResponseTextView = findViewById(R.id.responseContent);
        mRequestTitle = findViewById(R.id.request);
        mResponseTitle = findViewById(R.id.response);
        mDTNetworkCaptureEntity = getIntent().getParcelableExtra("data");
        if (null == mDTNetworkCaptureEntity) {
            return;
        }
        mRequestTextView.setText(Html.fromHtml(buildRequestText()));
        mResponseTextView.setText(Html.fromHtml(buildResponseText()));

        mResponseTextView.setOnClickListener(v -> {
            Intent intent = new Intent(DTNetworkDetailActivity.this, DTJsonPreviewActivity.class);
            intent.putExtra("jsonData",mDTNetworkCaptureEntity.responseBody);
            startActivity(intent);
        });
    }

    private String buildRequestText() {
        return buildHtmlStr("protocol：", mDTNetworkCaptureEntity.protocol) +
                buildHtmlStr("requestUrl：", mDTNetworkCaptureEntity.requestUrl) +
                buildHtmlStr("path：", mDTNetworkCaptureEntity.path) +
                buildHtmlStr("requestStartMessage：", mDTNetworkCaptureEntity.requestStartMessage) +
                buildHtmlStr("queries：", mDTNetworkCaptureEntity.queries) +
                buildHtmlStr("headers：", mDTNetworkCaptureEntity.headers) +
                buildHtmlStr("requestBody：", mDTNetworkCaptureEntity.requestBody);
    }

    private String buildResponseText() {
        return buildHtmlStr("responseState：",
                mDTNetworkCaptureEntity.responseCode + mDTNetworkCaptureEntity.responseMsg) +
                buildHtmlStr("responseUrl：", buildUrl(mDTNetworkCaptureEntity.responseUrl)) +
                buildHtmlStr("responseBody：",
                        JsonUtils.formatString(mDTNetworkCaptureEntity.responseBody, "<br>")) +
                buildHtmlStr("responseHeaders：", mDTNetworkCaptureEntity.responseHeaders);
    }

    private String buildUrl(String s) {
        if (null == s) {
            return "br";
        }

        int index = s.indexOf("?");
        if (index < 0) {
            return s;
        }

        String url = s.substring(0, index);
        String para = s.substring(index + 1);

        if (TextUtils.isEmpty(para)) {
            return s;
        }

        StringBuffer buffer = new StringBuffer();
        String[] paras = para.split("&");
        for (int i = 0; i < paras.length; i++) {
            String item = paras[i];
            String[] q = item.split("=");
            if (q.length > 1) {
                buffer.append(buildParaHtmlStr((i == 0 ? "?" : "&") + q[0] + "=", q[1]));
            } else {
                buffer.append(buildParaHtmlStr((i == 0 ? "?" : "&") + q[0] + "=", ""));
            }

        }
        return url + buffer.toString();
    }

    private String buildHtmlStr(String title, String content) {
        return "<b><font color=\"red\">" + title + "</font></b>" + content + "<br>";
    }

    private String buildParaHtmlStr(String title, String content) {
        return "<b><font color=\"blue\">" + title + "</font></b>" + content + "<br>";
    }

    public void doRequest(View view) {
        view.setBackgroundResource(R.color.ff007aff);
        mResponseTitle.setBackgroundResource(R.color.ffa8afc3);
        mRequestTextView.setVisibility(View.VISIBLE);
        mResponseTextView.setVisibility(View.GONE);
    }

    public void doResponse(View view) {
        view.setBackgroundResource(R.color.ff007aff);
        mRequestTitle.setBackgroundResource(R.color.ffa8afc3);
        mRequestTextView.setVisibility(View.GONE);
        mResponseTextView.setVisibility(View.VISIBLE);
    }
}
