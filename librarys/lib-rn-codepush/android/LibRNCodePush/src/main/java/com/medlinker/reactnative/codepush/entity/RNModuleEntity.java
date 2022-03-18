package com.medlinker.reactnative.codepush.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author hmy
 */
public class RNModuleEntity {
    private String name;
    private ArrayList<String> routes;

    public RNModuleEntity() {

    }

    public RNModuleEntity(JSONObject jsonObject) {
        if (jsonObject != null) {
            name = jsonObject.optString("name");
            JSONArray jsonArray = jsonObject.optJSONArray("routes");
            if (jsonArray != null) {
                routes = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        routes.add((String) jsonArray.get(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<String> routes) {
        this.routes = routes;
    }
}
