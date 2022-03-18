package com.medlinker.debugtools.fun.router;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRouter;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorHistoryAdapter;
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorManager;
import com.didichuxing.doraemonkit.widget.recyclerview.DividerItemDecoration;
import com.didichuxing.doraemonkit.widget.titlebar.HomeTitleBar;
import com.didichuxing.doraemonkit.zxing.activity.CaptureActivity;
import com.medlinker.debugtools.R;
import com.medlinker.debugtools.base.CallBack;
import com.medlinker.debugtools.config.DTConfig;
import com.medlinker.router.MedRouterHelper;

import java.util.List;

public class DTRouterActivity extends AppCompatActivity {

    private static final String TAG = "DTRouterActivity";
    private EditText mWebAddressInput;
    private TextView mUrlExplore;
    private RecyclerView mHistoryList;
    private WebDoorHistoryAdapter mWebDoorHistoryAdapter;
    private static final int REQUEST_CAMERA = 2;
    private static final int REQUEST_QR_CODE = 3;
    private static final String[] PERMISSIONS_CAMERA = {Manifest.permission.CAMERA};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dt_router);
        HomeTitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setListener(() -> finish());
        mWebAddressInput = findViewById(R.id.web_address_input);
        mWebAddressInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkInput()) {
                    mUrlExplore.setEnabled(true);
                } else {
                    mUrlExplore.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mUrlExplore = findViewById(R.id.url_explore);
        findViewById(R.id.clear).setOnClickListener(v -> {
            WebDoorManager.getInstance().clearHistory();
            mWebDoorHistoryAdapter.clear();
        });
        findViewById(R.id.qr_code).setOnClickListener(v -> qrCode());
        mUrlExplore.setOnClickListener(v -> doSearch(mWebAddressInput.getText().toString()));
        mHistoryList = findViewById(R.id.history_list);
        mHistoryList.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mHistoryList.setLayoutManager(layoutManager);
        List<String> historyItems = WebDoorManager.getInstance().getHistory();

        mWebDoorHistoryAdapter = new WebDoorHistoryAdapter(this);
        mWebDoorHistoryAdapter.setData(historyItems);
        mWebDoorHistoryAdapter.setOnItemClickListener((view, data) -> doSearch(data));
        mHistoryList.setAdapter(mWebDoorHistoryAdapter);
        DividerItemDecoration decoration = new DividerItemDecoration(DividerItemDecoration.VERTICAL);
        decoration.setDrawable(getResources().getDrawable(R.drawable.dk_divider));
        mHistoryList.addItemDecoration(decoration);
    }

    private void doSearch(String url) {
        WebDoorManager.getInstance().saveHistory(url);
        try {
            doRouter(url);
        }catch (Exception e){
            doCallback(url);
            e.printStackTrace();
        }
        mWebDoorHistoryAdapter.setData(WebDoorManager.getInstance().getHistory());
    }

    private boolean checkInput() {
        return !TextUtils.isEmpty(mWebAddressInput.getText());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_QR_CODE) {
            Bundle bundle = data.getExtras();
            String result = bundle.getString(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN);
            if (!TextUtils.isEmpty(result)) {
                doSearch(result);
            }
        }
    }

    private void qrCode() {
        if (!ownPermissionCheck()) {
            ActivityCompat.requestPermissions(this,PERMISSIONS_CAMERA, REQUEST_CAMERA);
            return;
        }
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_QR_CODE);
    }

    private boolean ownPermissionCheck() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            for (int grantResult : grantResults) {
                if (grantResult == -1) {
                    Toast.makeText(this,"请进行授权才可以使用该功能",Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void doRouter(String url) {
        MedRouterHelper.withUrl(url).queryTarget().navigation(this);
    }

    private void doCallback(String url){
        CallBack<String> callBack = DTConfig.instance().getRouterConfig().getCallBack();
        if (null != callBack){
            callBack.value(url);
        }
    }
}
