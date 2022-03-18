package com.medlinker.debugtools.fun.capture;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.medlinker.debugtools.R;
import com.medlinker.debugtools.base.DTBaseActivity;
import com.medlinker.debugtools.entity.DTNetworkCaptureEntity;
import com.medlinker.debugtools.storage.PreferencesKey;
import com.medlinker.debugtools.storage.PreferencesManager;
import com.medlinker.debugtools.vlayout.VLayoutRecycleView;
import com.medlinker.debugtools.vlayout.adapter.VLayoutListAdapter;
import com.medlinker.debugtools.vlayout.viewhold.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/4 2:20 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class DTNetworkCaptureActivity extends DTBaseActivity {

    private VLayoutRecycleView mVLayoutRecycleView;
    private CheckBox mCheckBox;
    private EditText mSearchET;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_med_api);
        mVLayoutRecycleView = findViewById(R.id.recycleView);
        mCheckBox = findViewById(R.id.all_switch);
        mVLayoutRecycleView.getDelegateAdapter().addAdapter(mListAdapter);
        mListAdapter.setDataList(MedApiCaptureManager.instance().getCache());
        mCheckBox.setOnCheckedChangeListener((v, isChecked) -> PreferencesManager.putBoolean(this, PreferencesKey.MED_API_CAPTURE, isChecked));

        MedApiCaptureManager.instance().register(data -> {
            String filter = mSearchET.getText().toString();
            mListAdapter.setDataList(filter(data,filter));
        });

        boolean state = PreferencesManager.getBoolean(this, PreferencesKey.MED_API_CAPTURE, true);
        mCheckBox.setChecked(state);
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());
        mSearchET = findViewById(R.id.et_search);
        findViewById(R.id.tv_search).setOnClickListener(v -> doSearch());

        findViewById(R.id.whiteList).setOnClickListener(v -> {
            Intent intent = new Intent(this, WhiteListActivity.class);
            startActivity(intent);
        });
    }

    private void doSearch() {
        String filter = mSearchET.getText().toString();
        if (TextUtils.isEmpty(filter)){
            mListAdapter.setDataList(MedApiCaptureManager.instance().getCache());
            return;
        }
        mListAdapter.setDataList(filter(MedApiCaptureManager.instance().getCache(),filter));
    }

    private List<DTNetworkCaptureEntity> filter(List<DTNetworkCaptureEntity> list,String filterStr){
        if (null == list || list.size() < 1 || TextUtils.isEmpty(filterStr)){
            return list;
        }
        List<DTNetworkCaptureEntity> newList = new ArrayList<>();
        for (DTNetworkCaptureEntity item : list) {
            String path = item.path;
            if (TextUtils.isEmpty(path)){
                continue;
            }
            String pt = path.toLowerCase();
            String fs = filterStr.toLowerCase();
            if (pt.contains(fs)){
                newList.add(item);
            }
        }
        return newList;
    }

    private VLayoutListAdapter<DTNetworkCaptureEntity> mListAdapter = new VLayoutListAdapter<DTNetworkCaptureEntity>(
            R.layout.item_api) {

        @Override
        public void onBindView(ViewHolder holder, int position, DTNetworkCaptureEntity data) {
            holder.itemView.setTag(data);
            holder.itemView.setOnClickListener(mListener);
            holder.setText(R.id.method, data.method);
            if (!TextUtils.isEmpty(data.path)){
                holder.setText(R.id.path, data.path);
            }
            holder.setText(R.id.time, milliToDateNine(data.time));
            String code = data.responseCode;
            holder.setVisible(R.id.code,false);
            if (!"200".equals(code)){
                holder.setVisible(R.id.code,true);
                holder.setText(R.id.code,code);
            }
        }
    };

    private OnClickListener mListener = v -> {
        DTNetworkCaptureEntity data = (DTNetworkCaptureEntity) v.getTag();
        Intent intent = new Intent(this, DTNetworkDetailActivity.class);
        intent.putExtra("data", data);
        startActivity(intent);
    };

    public void doClear(View view) {
        MedApiCaptureManager.instance().clear();
    }

    public static String milliToDateNine(long time) {
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss", Locale.CHINA);
        try {
            return dateFormat1.format(new Date(time));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
