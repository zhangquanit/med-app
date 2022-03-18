package com.medlinker.abtest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.IntDef;

import com.medlinker.analytics.http.HttpClient;
import com.medlinker.analytics.http.Request;
import com.medlinker.analytics.http.RequestMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ABTest {
    /**
     * 用户类型
     */
    public static final int DOCTOR = 1;
    public static final int PATIENT = 2;
    public static final int BROKER = 3;

    @Retention(RetentionPolicy.SOURCE)
    @Target(ElementType.PARAMETER)
    @IntDef(value = {DOCTOR, PATIENT, BROKER})
    private @interface UserType {
    }

    private static String TAG = "AbTest";
    private static final String SP_AB_TEST_NAME = "med_ab_test";
    private static final String QA_BASE_URL = "https://ab-web-qa.medlinker.com";
    private static final String ONLINE_BASE_URL = "https://ab-web.medlinker.com";
    private static final String X_HEAD_SESSION_XP = "X-Sess-Xp";
    private static final String X_HEAD_SESSION_NAME = "X-Sess-Name";
    private static final String FETCH_AB_TEST = "/v1/get_flags_async";
    private static String appId;
    private static String sessionName;
    private static boolean online = false;
    private static Context context;
    private static Handler mHandler;
    private static int userType = DOCTOR;


    /**
     * SDK 初始化
     *
     * @param appContext 应用上下文对象
     * @param abTestId   应用Id
     * @param onLineEnv  是否是线上环境
     */
    public static void init(Context appContext, String abTestId, boolean onLineEnv) {
        init(appContext, abTestId, onLineEnv, userType);
    }

    /**
     * SDK 初始化
     *
     * @param appContext 应用上下文对象
     * @param abTestId   应用Id
     * @param onLineEnv  是否是线上环境
     * @param type       用户类型，默认医生，1 医生 2 患者 3 经纪人
     */
    public static void init(Context appContext, String abTestId, boolean onLineEnv, @UserType int type) {
        userType = type;
        appId = abTestId;
        online = onLineEnv;
        context = appContext;
        mHandler = new Handler();
    }

    /**
     * 设置鉴权token
     *
     * @param session 登录成功时的sessionName
     */
    public static void setVerifyCode(String session) {
        sessionName = session;
    }

    /**
     * 从缓存中获取 一个实验的 ABTest 实验值
     *
     * @param expId 实验 Id
     * @return 实验结果值
     */
    public static ABTestModel fetchCacheABTest(String expId) {
        return getCachedABTestModel(expId);
    }

    /**
     * 获取 ABTest 实验值
     *
     * @param expIds   实验 Id 列表
     * @param callback 结果回调
     */
    public static void fetchABTest(List<String> expIds, final ResultCallback callback) {
        fetchABTest(expIds, false, callback);
    }

    /**
     * 获取 ABTest 实验值
     *
     * @param expIds        实验 Id 列表
     * @param isAnonymousId 是否是匿名用户Id
     * @param callback      结果回调
     */
    public static void fetchABTest(List<String> expIds, boolean isAnonymousId, final ResultCallback callback) {
        if (TextUtils.isEmpty(appId) && callback != null) {
            callback.onError(ABTestErrorCode.APP_ID_EMPTY.getReason());
            return;
        }
        if (!isAnonymousId && TextUtils.isEmpty(sessionName) && callback != null) {
            callback.onError(ABTestErrorCode.SESSION_EMPTY.getReason());
            return;
        }
        if ((expIds == null || expIds.size() == 0) && callback != null) {
            callback.onError(ABTestErrorCode.EXP_ID_EMPTY.getReason());
            return;
        }
        String json = "";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("appId", appId);
            jsonObject.put("userType", userType);
            if (isAnonymousId) {
                jsonObject.put("anonymousId", UUID.randomUUID().toString());
            }
            JSONArray jsonArray = new JSONArray(expIds);
            jsonObject.put("expIds", jsonArray);
            json = jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Request.Builder request = new Request.Builder();
        request.url(online ? ONLINE_BASE_URL + FETCH_AB_TEST : QA_BASE_URL + FETCH_AB_TEST);
        request.method(RequestMethod.POST);
        request.header(X_HEAD_SESSION_XP, "app");
        if (!TextUtils.isEmpty(sessionName)) {
            request.header(X_HEAD_SESSION_NAME, sessionName);
        }
        request.body(json);
        HttpClient.getDefault().request(request.build(), new HttpClient.ResultCallback() {
            @Override
            public void onResponse(String result) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(result);
                    final int errCode = jsonObject.getInt("errcode");
                    final String errMsg = jsonObject.getString("errmsg");
                    if (errCode == 0) {
                        JSONArray array = jsonObject.getJSONArray("data");
                        final List<ABTestModel> expIdResult = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject json = array.getJSONObject(i);
                            ABTestModel model = analyzeABTestModel(json);
                            expIdResult.add(analyzeABTestModel(json));
                            saveResult(model.getExpId(), json.toString());
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (callback != null) {
                                    callback.onSuccess(expIdResult);
                                }
                            }
                        });
                        Log.d(TAG, "fetchABTest success");
                    } else {
                        Log.d(TAG, "fetchABTest err = " + errMsg);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (callback != null) {
                                    if (errCode == ABTestErrorCode.NOT_LOGIN.getValue()) {
                                        callback.onError(ABTestErrorCode.NOT_LOGIN.getReason());
                                    } else if (errCode == ABTestErrorCode.SERVICE_ERROR.getValue()) {
                                        callback.onError(ABTestErrorCode.SERVICE_ERROR.getReason());
                                    } else {
                                        callback.onError("errCode = " + errCode + ", " + errMsg);
                                    }
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.d(TAG, "fetchABTest Exception = " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(final String errMsg) {
                Log.d(TAG, "fetchABTest onFailure = " + errMsg);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.onError(errMsg);
                        }
                    }
                });
            }
        });
    }

    /**
     * 获取 一个实验的 ABTest 实验值
     *
     * @param expId    实验 Id
     * @param callback 结果回调
     */
    public static void fetchABTest(String expId, final SingleResultCallback callback) {
        if ((TextUtils.isEmpty(appId) || TextUtils.isEmpty(expId)) && callback != null) {
            callback.onError(ABTestErrorCode.EXP_ID_EMPTY.getReason());
            return;
        }
        List<String> list = new ArrayList<>();
        list.add(expId);
        fetchABTest(list, new ResultCallback() {
            @Override
            public void onSuccess(List<ABTestModel> aBTestResultList) {
                if (callback != null) {
                    if (aBTestResultList != null && aBTestResultList.size() == 1) {
                        callback.onSuccess(aBTestResultList.get(0));
                    } else {
                        callback.onSuccess(null);
                    }
                }
            }

            @Override
            public void onError(String errMsg) {
                if (callback != null) {
                    callback.onError(errMsg);
                }
            }
        });
    }

    /**
     * 获取 一个实验的 ABTest 实验值
     *
     * @param expId         实验 Id
     * @param isAnonymousId 是否是匿名用户Id
     * @param callback      结果回调
     */
    public static void fetchABTest(String expId, boolean isAnonymousId, final SingleResultCallback callback) {
        if ((TextUtils.isEmpty(appId) || TextUtils.isEmpty(expId)) && callback != null) {
            callback.onError(ABTestErrorCode.EXP_ID_EMPTY.getReason());
            return;
        }
        List<String> list = new ArrayList<>();
        list.add(expId);
        fetchABTest(list, isAnonymousId, new ResultCallback() {
            @Override
            public void onSuccess(List<ABTestModel> aBTestResultList) {
                if (callback != null) {
                    if (aBTestResultList != null && aBTestResultList.size() == 1) {
                        callback.onSuccess(aBTestResultList.get(0));
                    } else {
                        callback.onSuccess(null);
                    }
                }
            }

            @Override
            public void onError(String errMsg) {
                if (callback != null) {
                    callback.onError(errMsg);
                }
            }
        });
    }

    /**
     * 先从缓存中获取ABTest结果,再从网络获取
     *
     * @param expId                    实验 Id
     * @param netWorkFetchIfCacheExist 缓存获取成功后是否继续网络获取
     * @param callback                 结果回调
     * @return 实验结果值
     */
    public static ABTestModel fetchABTestWithCacheFirst(String expId, boolean netWorkFetchIfCacheExist, final SingleResultCallback callback) {
        if ((TextUtils.isEmpty(appId) || TextUtils.isEmpty(expId)) && callback != null) {
            callback.onError(ABTestErrorCode.EXP_ID_EMPTY.getReason());
            return null;
        }
        ABTestModel model = getCachedABTestModel(expId);
        if (model == null || netWorkFetchIfCacheExist) {
            fetchABTest(expId, callback);
        }
        return model;
    }

    /**
     * 先从缓存中获取ABTest结果,再从网络获取,如果缓存中可以获取到不进行网络请求
     *
     * @param expId    实验 Id
     * @param callback 结果回调
     * @return 实验结果值
     */
    public static ABTestModel fetchABTestWithCacheFirst(String expId, final SingleResultCallback callback) {
        if ((TextUtils.isEmpty(appId) || TextUtils.isEmpty(expId)) && callback != null) {
            callback.onError(ABTestErrorCode.EXP_ID_EMPTY.getReason());
            return null;
        }
        ABTestModel model = getCachedABTestModel(expId);
        if (model == null) {
            fetchABTest(expId, callback);
        }
        return model;
    }

    private static ABTestModel getCachedABTestModel(String expId) {
        if (context == null || TextUtils.isEmpty(expId)) return null;
        String result = getABTestCache(expId);
        if (result == null) return null;
        try {
            JSONObject json = new JSONObject(result);
            if (appId.equals(json.getString("appId")) && expId.equals(json.getString("expId"))) {
                return analyzeABTestModel(json);
            }
        } catch (JSONException jsonException) {
            Log.d(TAG, "getCachedABTestModel Exception = " + jsonException.getMessage());
        }
        return null;
    }

    private static ABTestModel analyzeABTestModel(JSONObject json) {
        try {
            ABTestModel model = new ABTestModel();
            model.setAppId(json.getString("appId"));
            model.setExpId(json.getString("expId"));
            model.setGroupId(json.getString("groupId"));
            model.setVariableKey(json.getString("variableKey"));
            model.setVariableType(json.getString("variableType"));
            model.setVariableValue(json.getString("variableValue"));
            model.setSensorEventKey(json.getString("sensorEventKey"));
            return model;
        } catch (JSONException jsonException) {
            Log.d(TAG, "analyzeABTestModel Exception = " + jsonException.getMessage());
        }
        return null;
    }

    private static void saveResult(String expId, String abTestResult) {
        if (context == null || TextUtils.isEmpty(appId)) {
            Log.d(TAG, "errors,ABTest SDK must init.");
            return;
        }
        SharedPreferences sp = context.getSharedPreferences(SP_AB_TEST_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(expId, abTestResult);
        editor.apply();
    }

    private static String getABTestCache(String expId) {
        if (context == null || TextUtils.isEmpty(appId)) {
            Log.d(TAG, "errors,ABTest SDK must init.");
            return null;
        }
        SharedPreferences sp = context.getSharedPreferences(SP_AB_TEST_NAME, Context.MODE_PRIVATE);
        return sp.getString(expId, "");
    }

    /**
     * 清除本地缓存数据
     */
    public static void clearABTestCache() {
        if (context == null || TextUtils.isEmpty(appId)) {
            Log.d(TAG, "errors,ABTest SDK must init.");
            return;
        }
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(SP_AB_TEST_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }
}