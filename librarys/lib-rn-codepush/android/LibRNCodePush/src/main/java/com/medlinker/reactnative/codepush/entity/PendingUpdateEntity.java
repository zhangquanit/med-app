package com.medlinker.reactnative.codepush.entity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author hmy
 * @time 2019-10-29 14:44
 */
public class PendingUpdateEntity extends MetaInfoEntity {

    private boolean isLoading;

    public PendingUpdateEntity() {

    }

    public PendingUpdateEntity(JSONObject jsonObject) {
        super(jsonObject);
        if (jsonObject != null) {
            isLoading = jsonObject.optBoolean("isLoading");
        }
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = super.toJSONObject();
        try {
            jsonObject.put("isLoading", isLoading);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }
}
