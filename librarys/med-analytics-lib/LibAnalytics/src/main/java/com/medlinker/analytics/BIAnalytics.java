package com.medlinker.analytics;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.medlinker.analytics.http.HttpClient;
import com.medlinker.analytics.http.Request;
import com.medlinker.analytics.http.RequestMethod;

import org.json.JSONException;
import org.json.JSONObject;

public class BIAnalytics implements IAnalytics {
    private final static String TAG = "BIAnalytics";
    private final static String API_SERVER = "https://buriedpoint-interface-qa.medlinker.com/v1/buried/point";
    private static final String PLATFORM = "Android";
    private boolean disable = false;
    private Configuration configuration;
    private User user;

    @Override
    public void init(Context application, Configuration configuration) {
        this.configuration = configuration;
        user = new User();
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void clearUser() {
        this.user = null;
    }

    @Override
    public void disable() {
        disable = true;
    }

    @Override
    public void resume() {
        disable = false;
    }

    @Override
    public void track(String eventName) {
        if (disable) return;
    }

    @Override
    public void track(String eventName, JSONObject eventVariables) {
        if (disable) return;
        send(eventVariables);
    }

    @Override
    public void set(String methodName, Object... values) {

    }

    @Override
    public void registerDynamicSuperProperties(DynamicSuperProperties dynamicSuperProperties) {

    }

    @Override
    public void trackAppInstall(String channel) {

    }

    private void send(JSONObject eventVariables){
        Request request = new Request.Builder()
                .url(API_SERVER)
                .method(RequestMethod.POST)
                .addHeader("Content-Type", "application/json")
                .addHeader("X-Medlinker-St",getXHead())
                .body(eventVariables.toString())
                .build();
        HttpClient.getDefault().request(request, new HttpClient.ResultCallback() {
            @Override
            public void onResponse(String result) {
                Log.d(TAG, "result :" + result);
            }

            @Override
            public void onFailure(String errMsg) {
                Log.d(TAG, "send error = " + errMsg);
            }
        });
    }

    private String getXHead(){
        JSONObject jsonObject = new JSONObject();
        try {
            if (user != null && !TextUtils.isEmpty(user.getToken())) {
                jsonObject.put("token", user.getToken());
            }
            if (user != null && !TextUtils.isEmpty(user.getToken())) {
                jsonObject.put("userID", user.getUserID());
            }
            if (configuration!=null&&!TextUtils.isEmpty(configuration.getChannel())) {
                jsonObject.put("channel", configuration.getChannel());
            }
            if (configuration!=null&&!TextUtils.isEmpty(configuration.getAppCode())) {
                jsonObject.put("appCode", configuration.getAppCode());
            }
            jsonObject.put("platform", PLATFORM);
           // JSONObject extra = new JSONObject();
            if (user != null && !TextUtils.isEmpty(user.getIdCard())) {
                jsonObject.put("idCard", user.getIdCard());
            }
            if (user != null && !TextUtils.isEmpty(user.getPhoneNO())) {
                jsonObject.put("phone", user.getPhoneNO());
            }
           // jsonObject.put("extra",extra);
        } catch (JSONException e){

        }

        return jsonObject.toString();
    }
}
