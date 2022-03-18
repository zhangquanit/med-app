package com.medlinker.abtest.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.medlinker.abtest.ABTest;
import com.medlinker.abtest.ABTestModel;
import com.medlinker.abtest.ResultCallback;
import com.medlinker.analytics.MedAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SecondFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MedAnalytics.set("setFragmentXPageName", this, "第二页");
        MedAnalytics.track("SecondFragment");
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            JSONObject value = new JSONObject();
            value.put("test", "this is a test data");
            value.put("action", "click");
            jsonObject.put("data", value.toString());
        } catch (JSONException e) {

        }
        MedAnalytics.track(null, jsonObject);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MedAnalytics.track("SecondFragment-onViewCreated");
        view.findViewById(R.id.button_second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }
}