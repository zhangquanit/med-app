package com.medlinker.reactnative.codepush.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RNRouteEntity {
    private String baseVersion;
    private ArrayList<RNModuleEntity> modules;
    private ArrayList<RNModuleEntity> deletedModules;

    public RNRouteEntity() {

    }

    public RNRouteEntity(JSONObject jsonObject) {
        if (jsonObject != null) {
            baseVersion = jsonObject.optString("baseVersion");
            JSONArray jsonArray = jsonObject.optJSONArray("modules");
            if (jsonArray != null) {
                modules = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject moduleJson = (JSONObject) jsonArray.get(i);
                        modules.add(new RNModuleEntity(moduleJson));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            jsonArray = jsonObject.optJSONArray("deletedModules");
            if (jsonArray != null) {
                deletedModules = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject moduleJson = (JSONObject) jsonArray.get(i);
                        deletedModules.add(new RNModuleEntity(moduleJson));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public String getBaseVersion() {
        return baseVersion;
    }

    public void setBaseVersion(String baseVersion) {
        this.baseVersion = baseVersion;
    }

    public ArrayList<RNModuleEntity> getModules() {
        return modules;
    }

    public void setModules(ArrayList<RNModuleEntity> modules) {
        this.modules = modules;
    }

    public ArrayList<RNModuleEntity> getDeletedModules() {
        return deletedModules;
    }

    public void setDeletedModules(ArrayList<RNModuleEntity> deletedModules) {
        this.deletedModules = deletedModules;
    }
}
