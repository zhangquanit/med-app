package com.medlinker.debugtools.fun.capture;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.medlinker.debugtools.DTModule;
import com.medlinker.debugtools.base.CallBack;
import com.medlinker.debugtools.config.DTConfig;
import com.medlinker.debugtools.entity.DTNetworkCaptureEntity;
import com.medlinker.debugtools.storage.PreferencesKey;
import com.medlinker.debugtools.storage.PreferencesManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/4 3:12 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class MedApiCaptureManager {

    private static final MedApiCaptureManager mManager = new MedApiCaptureManager();
    private CallBack<List<DTNetworkCaptureEntity>> mCallBack;
    private static final int MAX_RECORD_NUM = 100;
    private Gson mGson = new Gson();

    private MedApiCaptureManager(){
        String s = PreferencesManager.getString(DTModule.app(), PreferencesKey.MED_WHITE_LIST);
        if (!TextUtils.isEmpty(s)){
            List<String> whiteList = mGson.fromJson(s,new TypeToken<List<String>>() {} .getType());
            if (null != whiteList && whiteList.size() > 0){
                mWhiteList.addAll(whiteList);
            }
        }
    }

    private ArrayList<DTNetworkCaptureEntity> mCache = new ArrayList<>(25);

    public static MedApiCaptureManager instance() {
        return mManager;
    }

    public void addData(DTNetworkCaptureEntity data) {
        int size = mCache.size();
        if (size > MAX_RECORD_NUM) {
            mCache.remove(0);
        }
        mCache.add(data);
        notice();
    }

    public ArrayList<DTNetworkCaptureEntity> getCache() {
        return mCache;
    }

    public void notice() {
        if (null != mCallBack) {
            mCallBack.value(mCache);
        }
    }

    public void register(CallBack<List<DTNetworkCaptureEntity>> callBack) {
        mCallBack = callBack;
    }

    public void clear() {
        mCache.clear();
        notice();
    }

    public DTNetworkCaptureEntity getData(String path) {
        for (DTNetworkCaptureEntity e: mCache) {
            if (e.equals(path)){
                return e;
            }
        }
        return new DTNetworkCaptureEntity();
    }

    private List<String> mWhiteList = new ArrayList<>();
    public void addWhiteList(String host){
        if (TextUtils.isEmpty(host)){
            return;
        }
        if (mWhiteList.contains(host)){
            return;
        }
        mWhiteList.add(host);
        PreferencesManager.putString(DTModule.app(), PreferencesKey.MED_WHITE_LIST,mGson.toJson(mWhiteList));
    }

    public void removeWhiteList(String host){
        if (TextUtils.isEmpty(host)){
            return;
        }
        if (!mWhiteList.contains(host)){
            return;
        }
        mWhiteList.remove(host);
        PreferencesManager.putString(DTModule.app(), PreferencesKey.MED_WHITE_LIST,mGson.toJson(mWhiteList));
    }

    public void clearWhiteList(){
        mWhiteList.clear();
        PreferencesManager.removeData(DTModule.app(), PreferencesKey.MED_WHITE_LIST);
    }

    public boolean isCapture(String host){
        if (TextUtils.isEmpty(host) || mWhiteList.size() < 1){
            return true;
        }
        return mWhiteList.contains(host);
    }

    public List<String> getWhiteList() {
        return mWhiteList;
    }
}
