package com.medlinker.debugtools.fun.lane;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.medlinker.debugtools.R;
import com.medlinker.debugtools.base.CallBack;
import com.medlinker.debugtools.base.DTBaseActivity;
import com.medlinker.debugtools.config.DTConfig;
import com.medlinker.debugtools.config.DTLaneConfig;
import com.medlinker.debugtools.entity.DTEnvLaneEntity;
import com.medlinker.debugtools.entity.DTLaneDomainEntity;
import com.medlinker.debugtools.entity.DTLaneListEntity;
import com.medlinker.debugtools.log.DTLog;
import com.medlinker.debugtools.utils.ImmersiveModeUtil;
import com.medlinker.debugtools.vlayout.VLayoutRecycleView;
import com.medlinker.debugtools.vlayout.adapter.VLayoutListAdapter;
import com.medlinker.debugtools.vlayout.viewhold.ViewHolder;
import com.medlinker.debugtools.widget.DtDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author: pengdaosong
 * @CreateTime: 2021/3/3 2:42 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class DTEnvLaneActivity extends DTBaseActivity {

    private static final String TAG = "DTEnvLaneActivity";

    private SwipeRefreshLayout mRefreshLayout;
    private VLayoutRecycleView mRecyclerView;
    private VLayoutListAdapter<DTLaneListEntity> mVLayoutListAdapter;
    private List<DTLaneListEntity> mList;

    private Gson mGson = new Gson();
    private EditText mEditText;
    private TextView mLaneName;
    private String mCurrentLaneName = DTLaneConfig.QA;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private String mLaneDomain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_env_lane);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ImmersiveModeUtil.setStatusBarTransparent(this);
        } else {
            ImmersiveModeUtil.setDefaultImmersiveMode(this);
        }
        mLaneDomain = DTConfig.instance().getLaneConfig().getLaneDomain();
        mCurrentLaneName = DTLaneStorage.getLaneName();
        if (TextUtils.isEmpty(mCurrentLaneName)){
            mCurrentLaneName = "QA";
        }
        initView();
        loadData();
        setLaneName();
    }

    private void initView() {
        mRefreshLayout = findViewById(R.id.refresh_layout);
        mLaneName = findViewById(R.id.lane_name);
        mEditText = findViewById(R.id.et_search);
        mRecyclerView = findViewById(R.id.recycle_view);
        mRecyclerView.getDelegateAdapter().addAdapter(
                mVLayoutListAdapter = new VLayoutListAdapter<DTLaneListEntity>(
                        R.layout.act_env_list_item) {
                    @Override
                    public void onBindView(ViewHolder holder, int position, DTLaneListEntity data) {
                        holder.setText(R.id.item_name, data.getName());
                        holder.itemView.setOnClickListener((v) -> showSwitchDialog(data));
                    }
                });

        mRefreshLayout.setOnRefreshListener(this::loadData);
        findViewById(R.id.tv_search).setOnClickListener((v) -> loadData());
        findViewById(R.id.iv_back).setOnClickListener((v) -> finish());
        findViewById(R.id.btn_reset_online).setOnClickListener((v) -> {
            Map<String, String> online = DTConfig.instance().getLaneConfig().getOnlineDomains();
            String onlineJson = mGson.toJson(online);
            if (null == onlineJson) {
                Toast.makeText(this, "线上域名为空", Toast.LENGTH_SHORT).show();
                return;
            }
            // qa环境，把切换线上当成特殊的泳道
            showQAToOnlineDialog(onlineJson,DTLaneConfig.ONLINE);
        });
        findViewById(R.id.btn_reset_qa).setOnClickListener((v) -> {
            Map<String, String> qa = DTConfig.instance().getLaneConfig().getQADomains();
            String qaJson = null == qa ? null : mGson.toJson(qa);
            showQAToOnlineDialog(qaJson,DTLaneConfig.QA);
        });

        findViewById(R.id.btn_reset_Pre).setOnClickListener(v -> {
            Map<String, String> pre = DTConfig.instance().getLaneConfig().getPreDomains();
            String qaJson = null == pre ? null : mGson.toJson(pre);
            showQAToOnlineDialog(qaJson,DTLaneConfig.PRE);
        });

        int onlineV = null == DTConfig.instance().getLaneConfig().getOnlineDomains() ? View.GONE : View.VISIBLE;
        findViewById(R.id.btn_reset_online).setVisibility(onlineV);
        findViewById(R.id.lineQa).setVisibility(onlineV);

        mEditText.setOnEditorActionListener((v, actionId, event) -> {
            Log.d(TAG, ""+actionId+","+event);
            if (actionId== EditorInfo.IME_ACTION_SEND|| (event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)) {
                loadData();
                return true;
            }
            return false;
        });
    }

    private void setLaneName() {
        if (TextUtils.isEmpty(mCurrentLaneName)) {
            mLaneName.setVisibility(View.GONE);
        } else {
            mLaneName.setVisibility(View.VISIBLE);
        }
        mLaneName.setText("当前环境->" + mCurrentLaneName);
    }

    private void showSwitchDialog(DTLaneListEntity entity) {
        DtDialog dtDialog = new DtDialog(this);
        dtDialog.setCancelable(false);
        dtDialog.setContent("是否使用该泳道的配置域名")
                .setTitle(entity.getName())
                .setLeft("取消")
                .setRight("确定")
                .setConsumeBackPressed(true)
                .setLeftClickListener(v -> dtDialog.dismiss()).setRightClickListener(v -> {
            dtDialog.dismiss();
            requestDomain(entity.getName());
        });
        dtDialog.show();
    }

    private void showQAToOnlineDialog(String laneJson,String laneName) {
        DtDialog dtQAToOnlineDialog = new DtDialog(this);
        dtQAToOnlineDialog.setCancelable(false);
        dtQAToOnlineDialog.setContent("请杀掉进程重启重启应用")
                .setTitle("提示")
                .setLeft("")
                .setRight("确定重置")
                .setConsumeBackPressed(true)
                .setLeftClickListener(v -> dtQAToOnlineDialog.dismiss())
                .setRightClickListener(v -> {
                    dtQAToOnlineDialog.dismiss();
                    doSuccess(laneName,laneJson);
                });
        dtQAToOnlineDialog.show();
    }

    private Call mCall;

    private void loadData() {
        String name = mEditText.getText().toString();
        showDialogLoading(true);
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                //对象的创建
                .build();
        Request.Builder requestBuild = new Request.Builder()
                .addHeader("authorization", DTConfig.instance().getLaneConfig().getAuthorization());
        HttpUrl.Builder urlBuilder = Objects
                .requireNonNull(HttpUrl.parse(mLaneDomain + "/project/list-simple"))
                .newBuilder();
        urlBuilder.addQueryParameter("name", name);
        urlBuilder.addQueryParameter("size", "50");
        requestBuild.url(urlBuilder.build());
        mCall = client.newCall(requestBuild.build());
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, IOException e) {
                runOnUiThread(() -> {
                    hideDialogLoading();
                    mRefreshLayout.setRefreshing(false);
                    Toast.makeText(DTEnvLaneActivity.this, "获取泳道列表失败！", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                runOnUiThread(() -> {
                    hideDialogLoading();
                    mRefreshLayout.setRefreshing(false);
                });
                if (null == response.body()) {
                    runOnUiThread(() -> Toast
                            .makeText(DTEnvLaneActivity.this, "接口响应异常！", Toast.LENGTH_SHORT)
                            .show());
                    return;
                }
                try {
                    String data = response.body().string();
                    if (TextUtils.isEmpty(data)) {
                        runOnUiThread(() -> Toast
                                .makeText(DTEnvLaneActivity.this, "泳道列表数据异常！", Toast.LENGTH_SHORT)
                                .show());
                        return;
                    }
                    DTEnvLaneEntity envLaneEntity = mGson.fromJson(data, DTEnvLaneEntity.class);
                    DTEnvLaneEntity.DataBean dataBean = envLaneEntity.getData();
                    if (null == dataBean || null == dataBean.getList()) {
                        runOnUiThread(() -> Toast
                                .makeText(DTEnvLaneActivity.this, envLaneEntity.getErrmsg(),
                                        Toast.LENGTH_SHORT)
                                .show());
                        return;
                    }
                    mList = dataBean.getList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(() -> mVLayoutListAdapter.setDataList(mList));
            }
        });
    }

    private void requestDomain(String name) {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS);
        OkHttpClient client = clientBuilder.build();
        JSONObject para = new JSONObject();
        JSONArray array = new JSONArray(DTConfig.instance().getLaneConfig().getLaneAuthority());
        try {
            para.put("domains", array);
            para.put("projectName", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(JSON, para.toString());
        Request request = new Request.Builder()
                .header("authorization", DTConfig.instance().getLaneConfig().getAuthorization())
                .url(mLaneDomain + "/project/app-domains")
                .post(body)
                .build();
        showDialogLoading(true);
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    hideDialogLoading();
                    runOnUiThread(() -> Toast
                            .makeText(DTEnvLaneActivity.this, "泳道域名获取失败！", Toast.LENGTH_SHORT)
                            .show());
                });
            }

            @Override
            public void onResponse(Call call, Response response) {
                runOnUiThread(() -> hideDialogLoading());
                try {
                    String data = response.body().string();
                    Log.d(TAG, "origin domain data = " + data);
                    if (TextUtils.isEmpty(data)) {
                        runOnUiThread(() -> Toast
                                .makeText(DTEnvLaneActivity.this, "泳道域名数据异常！", Toast.LENGTH_SHORT)
                                .show());
                        return;
                    }
                    DTLaneDomainEntity domainEntity = mGson.fromJson(data, DTLaneDomainEntity.class);
                    Map<String, String> dataBean = domainEntity.getData();
                    if (null == dataBean || dataBean.size() < 1) {
                        runOnUiThread(() -> Toast
                                .makeText(DTEnvLaneActivity.this, "泳道域名数据空！", Toast.LENGTH_SHORT)
                                .show());
                        return;
                    }

                    String dataStr = mGson.toJson(dataBean);
                    DTLog.d(TAG, "domain data = " + dataStr);
                    runOnUiThread(() -> doSuccess(name,dataStr));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void doSuccess(String name,String data){
        try {
            mCurrentLaneName = name;
//            if (DTLaneConfig.QA.equals(mCurrentLaneName)){
//                DTLaneStorage.reset();
//            }else {
//                DTLaneStorage.save(mCurrentLaneName,data);
//            }

            DTLaneStorage.reset();
            if (!DTLaneConfig.QA.equals(mCurrentLaneName)){
                DTLaneStorage.save(mCurrentLaneName,data);
            }
            setLaneName();
            CallBack<DTLaneStorage.LaneData> callBack = DTConfig.instance().getLaneConfig().getCallBack();
            if (null != callBack){
                callBack.value(DTLaneStorage.getLaneData());
            }
            finish();
            return;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        // DTLaneSwitcherUtils.setLane(dataStr);
        // DTLaneSwitcherUtils.setLaneName(name);
        Toast.makeText(DTEnvLaneActivity.this,"设置失败!",Toast.LENGTH_SHORT).show();
    }
}
