package com.medlinker.reactnative.codepush;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.medlinker.reactnative.codepush.entity.MetaInfoEntity;
import com.medlinker.reactnative.codepush.entity.PendingUpdateEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author hmy
 * @time 2019-10-17 17:24
 */
public class RNSettingsManager {

    private SharedPreferences mSettings;

    public RNSettingsManager(Context applicationContext) {
        mSettings = applicationContext.getSharedPreferences(RNCodePushConstants.CODE_PUSH_PREFERENCES, 0);
    }

    public JSONArray getFailedUpdates() {
        String failedUpdatesString = mSettings.getString(RNCodePushConstants.FAILED_UPDATES_KEY, null);
        if (failedUpdatesString == null) {
            return new JSONArray();
        }

        try {
            return new JSONArray(failedUpdatesString);
        } catch (JSONException e) {
            // Unrecognized data format, clear and replace with expected format.
            JSONArray emptyArray = new JSONArray();
            mSettings.edit().putString(RNCodePushConstants.FAILED_UPDATES_KEY, emptyArray.toString()).apply();
            return emptyArray;
        }
    }

    public boolean isFailedHash(String packageHash) {
        if (TextUtils.isEmpty(packageHash)) {
            return false;
        }
        JSONArray failedUpdates = getFailedUpdates();
        for (int i = 0; i < failedUpdates.length(); i++) {
            try {
                JSONObject failedPackage = failedUpdates.getJSONObject(i);
                String failedPackageHash = failedPackage.getString(MetaInfoEntity.KEY_HASH);
                if (packageHash.equals(failedPackageHash)) {
                    return true;
                }
            } catch (JSONException e) {
                RNCodePush.getRnCodePushLog().setFailedHash(e.getMessage());
            }
        }

        return false;
    }

    public void saveFailedUpdate(JSONObject failedMetaInfo) {
        try {
            if (isFailedHash(failedMetaInfo.getString(MetaInfoEntity.KEY_HASH))) {
                // Do not need to add the package if it is already in the failedUpdates.
                return;
            }
        } catch (JSONException e) {
        }

        String failedUpdatesString = mSettings.getString(RNCodePushConstants.FAILED_UPDATES_KEY, null);
        JSONArray failedUpdates;
        if (failedUpdatesString == null) {
            failedUpdates = new JSONArray();
        } else {
            try {
                failedUpdates = new JSONArray(failedUpdatesString);
            } catch (JSONException e) {
                failedUpdates = new JSONArray();
            }
        }

        failedUpdates.put(failedMetaInfo);
        mSettings.edit().putString(RNCodePushConstants.FAILED_UPDATES_KEY, failedUpdates.toString()).apply();
    }

    public void removeFailedUpdates(String packageHash) {
        if (TextUtils.isEmpty(packageHash)) {
            return;
        }
        JSONArray failedUpdates = getFailedUpdates();
        for (int i = 0; i < failedUpdates.length(); i++) {
            try {
                JSONObject failedPackage = failedUpdates.getJSONObject(i);
                String failedPackageHash = failedPackage.getString(MetaInfoEntity.KEY_HASH);
                if (packageHash.equals(failedPackageHash)) {
                    failedUpdates.remove(i);
                    break;
                }
            } catch (JSONException e) {
            }
        }
        mSettings.edit().putString(RNCodePushConstants.FAILED_UPDATES_KEY, failedUpdates.toString()).apply();
    }

    public void removeFailedUpdates() {
        mSettings.edit().remove(RNCodePushConstants.FAILED_UPDATES_KEY).apply();
    }

    public PendingUpdateEntity getPendingUpdate() {
        String pendingUpdateString = mSettings.getString(RNCodePushConstants.PENDING_UPDATE_KEY, null);
        if (TextUtils.isEmpty(pendingUpdateString)) {
            return new PendingUpdateEntity();
        }
        try {
            return new PendingUpdateEntity(new JSONObject(pendingUpdateString));
        } catch (JSONException e) {
            e.printStackTrace();
            return new PendingUpdateEntity();
        }
    }

    public void savePendingUpdate(MetaInfoEntity packageHash, boolean isLoading) {
        PendingUpdateEntity entity = new PendingUpdateEntity(packageHash.toJSONObject());
        entity.setLoading(isLoading);
        mSettings.edit().putString(RNCodePushConstants.PENDING_UPDATE_KEY, entity.toJSONObject().toString()).apply();
    }

    public void removePendingUpdate() {
        mSettings.edit().remove(RNCodePushConstants.PENDING_UPDATE_KEY).apply();
    }

    public void saveUnloadUpdate(MetaInfoEntity entity) {
        mSettings.edit().putString(RNCodePushConstants.UNLOAD_UPDATE_KEY, entity.getHash()).apply();
    }

    public boolean isUnloadUpdate(MetaInfoEntity entity) {
        String value = mSettings.getString(RNCodePushConstants.UNLOAD_UPDATE_KEY, "");
        if (TextUtils.isEmpty(value)) {
            return false;
        }
        return value.equals(entity.getHash());
    }

    public void removeUnloadUpdate() {
        mSettings.edit().remove(RNCodePushConstants.UNLOAD_UPDATE_KEY).apply();
    }

    public void saveBaseMetaHash(String hash) {
        mSettings.edit().putString(RNCodePushConstants.BASE_META_HASH_KEY, hash).apply();
    }

    public String getBaseMetaHash() {
        return mSettings.getString(RNCodePushConstants.BASE_META_HASH_KEY, "");
    }
}
