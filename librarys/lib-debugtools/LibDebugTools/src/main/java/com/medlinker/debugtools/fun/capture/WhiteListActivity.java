package com.medlinker.debugtools.fun.capture;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medlinker.debugtools.R;
import com.medlinker.debugtools.base.DTBaseActivity;
import com.medlinker.debugtools.config.DTConfig;
import com.medlinker.debugtools.fun.lane.DTLaneStorage;
import com.medlinker.debugtools.storage.PreferencesKey;
import com.medlinker.debugtools.storage.PreferencesManager;
import com.medlinker.debugtools.vlayout.VLayoutRecycleView;
import com.medlinker.debugtools.vlayout.adapter.VLayoutListAdapter;
import com.medlinker.debugtools.vlayout.viewhold.ViewHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class WhiteListActivity extends DTBaseActivity {

    private VLayoutRecycleView mVLayoutRecycleView;
    private VLayoutRecycleView mVLayoutRecycleView1;
    private EditText mHost;
    private List<String> mDefaultHost;
    private List<String> mCustomHost = new ArrayList<>();
    private Gson mGson = new Gson();
    private EditText mSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_white_list);
        mVLayoutRecycleView = findViewById(R.id.recycleView);
        mVLayoutRecycleView1 = findViewById(R.id.recycleView1);

        mVLayoutRecycleView.getDelegateAdapter().addAdapter(mListAdapter);
        mVLayoutRecycleView1.getDelegateAdapter().addAdapter(mListAdapter1);
        mHost = findViewById(R.id.et_host);
        findViewById(R.id.add).setOnClickListener(v -> {
            String host = mHost.getText().toString();
            if (TextUtils.isEmpty(host)) {
                return;
            }
            if (!mCustomHost.contains(host)){
                mCustomHost.add(host);
                PreferencesManager.putString(this,PreferencesKey.MED_CUSTOM_WHITE_LIST,mGson.toJson(mCustomHost));
            }
            MedApiCaptureManager.instance().addWhiteList(host);
            mListAdapter.setDataList(MedApiCaptureManager.instance().getWhiteList());
        });
        findViewById(R.id.clear).setOnClickListener(v -> {
            MedApiCaptureManager.instance().clearWhiteList();
            mListAdapter.setDataList(MedApiCaptureManager.instance().getWhiteList());
        });
        mListAdapter.setDataList(MedApiCaptureManager.instance().getWhiteList());
        setDefaultHostList();
        findViewById(R.id.clearCustom).setOnClickListener(v -> {
            String s = PreferencesManager.getString(this,PreferencesKey.MED_CUSTOM_WHITE_LIST);
            if (!TextUtils.isEmpty(s)){
                List<String> custom = mGson.fromJson(s,new TypeToken<List<String>>() {} .getType());
                if (null != custom && custom.size() > 0){
                    mDefaultHost.removeAll(custom);
                }
            }
            mListAdapter1.setDataList(mDefaultHost);
            PreferencesManager.removeData(this,PreferencesKey.MED_CUSTOM_WHITE_LIST);
        });

        findViewById(R.id.tv_search).setOnClickListener(v -> doSearch());
        mSearch = findViewById(R.id.et_search);
        mSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId== EditorInfo.IME_ACTION_SEND|| (event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)) {
               doSearch();
                return true;
            }
            return false;
        });
    }

    private void doSearch() {
        String s = mSearch.getText().toString();
        if (TextUtils.isEmpty(s) || null == mDefaultHost){
            mListAdapter1.setDataList(mDefaultHost);
            return;
        }
        List<String> newList = new ArrayList<>();
        for (String host:mDefaultHost) {
            if (TextUtils.isEmpty(host)){
                continue;
            }
            String pt = s.toLowerCase();
            String ht = host.toLowerCase();
            if (ht.contains(pt)){
                newList.add(host);
            }
        }
        mListAdapter1.setDataList(newList);
    }

    private void setDefaultHostList(){
        Map<String, String>  onlineDomains = DTConfig.instance().getLaneConfig().getOnlineDomains();
        if (null == onlineDomains || onlineDomains.size() < 1){
            return;
        }
        Collection<String> values = onlineDomains.keySet();
        if (values.size() < 1){
            return;
        }
        mDefaultHost = new ArrayList<>(values);
        String s = PreferencesManager.getString(this,PreferencesKey.MED_CUSTOM_WHITE_LIST);
        if (!TextUtils.isEmpty(s)){
            List<String> custom = mGson.fromJson(s,new TypeToken<List<String>>() {} .getType());
            if (null != custom && custom.size() > 0){
                mDefaultHost.addAll(custom);
            }
        }

       String lane = DTLaneStorage.getLaneDomains();
        Map<String, String> maps = mGson.fromJson(lane, Map.class);
        if (null != maps && maps.size() >0) {
            Collection<String> collection = maps.values();
            for (String host:collection) {
                if (host.startsWith("@")){
                    host = host.substring(1);
                }
                mDefaultHost.add(0,host);
            }
        }
        mListAdapter1.setDataList(mDefaultHost);
    }

    private VLayoutListAdapter<String> mListAdapter = new VLayoutListAdapter<String>(
            R.layout.item_white_list) {

        @Override
        public void onBindView(ViewHolder holder, int position, String data) {
            holder.itemView.setTag(data);
            holder.setText(R.id.host, data);
            holder.setTextColorRes(R.id.host,R.color.ff007aff);
            holder.setImageResource(R.id.action, R.mipmap.ic_move);
            holder.itemView.setOnClickListener(mListener);
        }

        private View.OnClickListener mListener = v -> {
            String host = (String) v.getTag();
            if (TextUtils.isEmpty(host)) {
                return;
            }

            MedApiCaptureManager.instance().removeWhiteList(host);
            mListAdapter.setDataList(MedApiCaptureManager.instance().getWhiteList());
            if (!mDefaultHost.contains(host)){
                mDefaultHost.add(0,host);
                mListAdapter1.setDataList(mDefaultHost);
            }
        };
    };

    private VLayoutListAdapter<String> mListAdapter1 = new VLayoutListAdapter<String>(
            R.layout.item_white_list) {

        @Override
        public void onBindView(ViewHolder holder, int position, String data) {
            holder.itemView.setTag(data);
            holder.setTextColorRes(R.id.host,R.color.dk_color_D26282);
            holder.setText(R.id.host, data);
            holder.setImageResource(R.id.action, R.mipmap.ic_add);
            holder.itemView.setOnClickListener(mListener);
        }

        private View.OnClickListener mListener = v -> {
            String host = (String) v.getTag();
            if (TextUtils.isEmpty(host)) {
                return;
            }
            MedApiCaptureManager.instance().addWhiteList(host);
            mListAdapter.setDataList(MedApiCaptureManager.instance().getWhiteList());

            mDefaultHost.remove(host);
            mListAdapter1.setDataList(mDefaultHost);
        };
    };
}
