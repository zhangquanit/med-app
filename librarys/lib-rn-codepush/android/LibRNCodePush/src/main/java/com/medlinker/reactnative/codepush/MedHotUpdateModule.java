package com.medlinker.reactnative.codepush;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.medlinker.reactnative.codepush.entity.MetaInfoEntity;

import org.json.JSONObject;

import java.util.Iterator;

/**
 * @author hmy
 * @time 2019-10-17 14:35
 */
public class MedHotUpdateModule extends ReactContextBaseJavaModule {

    private RNSettingsManager mSettingsManager;

    public MedHotUpdateModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mSettingsManager = new RNSettingsManager(reactContext);
    }

    @Override
    public String getName() {
        return "MedHotUpdateModule";
    }

    /**
     * 拉取当前更新的元数据
     *
     * @param param
     * @param promise
     */
    @ReactMethod
    public void getCurrentHotUpdatedMetaInfo(ReadableMap param, Promise promise) {
        try {
            WritableMap map = Arguments.createMap();
            MetaInfoEntity infoEntity = RNCodePush.getInstance().getCurrentLoadPackageMetaInfo();
            JSONObject jsonObject = infoEntity.toJSONObject();
            Iterator it = jsonObject.keys();
            while (it.hasNext()) {
                String key = (String) it.next();
                Object value = jsonObject.get(key);
                if (value instanceof String) {
                    map.putString(key, (String) value);
                } else if (value instanceof Double) {
                    map.putDouble(key, (Double) value);
                } else if (value instanceof Long) {
                    map.putDouble(key, (Long) value);
                } else if (value instanceof Integer) {
                    map.putInt(key, (Integer) value);
                } else if (value instanceof Boolean) {
                    map.putBoolean(key, (Boolean) value);
                }
            }
            promise.resolve(map);
        } catch (Exception e) {
            e.printStackTrace();
            promise.reject(e);
        }
    }

    /**
     * 通知native更新成功
     */
    @ReactMethod
    public void updateSuccess(ReadableMap param, Promise promise) {
        RNCodePush.getInstance().removeCurrentFailedUpdates();
    }
}
