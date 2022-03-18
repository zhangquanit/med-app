package com.medlinker.abtest.app;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.medlinker.abtest.ABTest;
import com.medlinker.abtest.ABTestModel;
import com.medlinker.abtest.SingleResultCallback;
import com.medlinker.analytics.MedAnalytics;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static String TAG = "MainActivity-ABTest";
    private String expId = "580077133875908660";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        List<String> expIds = new ArrayList<>();
        expIds.add(expId);
        ABTest.fetchABTest(expId, true, new SingleResultCallback() {
            @Override
            public void onSuccess(ABTestModel abTestModel) {
                Log.d(TAG, "fetchABTest expId = " + abTestModel.getExpId() + " , 事件变量=" + abTestModel.getVariableKey() + " ,事件值 = " + abTestModel.getVariableValue());
            }

            @Override
            public void onError(String errMsg) {

            }
        });
        /*ABTest.fetchABTestWithCacheFirst(expId, true, new SingleResultCallback() {
            @Override
            public void onSuccess(ABTestModel abTestModel) {
                Log.d(TAG, "fetchABTestWithCacheFirst expId = " + abTestModel.getExpId() + " , 事件变量=" + abTestModel.getVariableKey() + " ,事件值 = " + abTestModel.getVariableValue());
                ABTestModel model = ABTest.fetchCacheABTest(expId);
                if (model != null) {
                    Log.d(TAG, "fetchCacheABTest expId = " + model.getExpId() + " , 事件变量=" + model.getVariableKey() + " ,事件值 = " + model.getVariableValue());
                }
            }

            @Override
            public void onError(String errMsg) {

            }
        });*/
        MedAnalytics.set("setActivityPageName", this, "主页");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}