package com.medlinker.reactnative.codepush.entity;

import com.medlinker.reactnative.codepush.RNCodePushConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author hmy
 */
public class MetaInfoEntity {

    public static final String KEY_HASH = "hash";

    private String baseVersion;
    private String version;
    private int versionCode;
    private String downloadFile;
    private String bundleFile;
    private String manifestFile;
    private String hash;
    private long buildTime;
    private boolean confirm;
    private String tagId;//tagId 区分业务，泳道切换后需要判断

    private long updateTime;

    public MetaInfoEntity() {

    }

    public MetaInfoEntity(JSONObject jsonObject) {
        if (jsonObject != null) {
            this.baseVersion = jsonObject.optString("baseVersion");
            this.version = jsonObject.optString("version");
            this.versionCode = jsonObject.optInt("versionCode");
            this.downloadFile = jsonObject.optString("downloadFile");
            this.bundleFile = RNCodePushConstants.DEFAULT_JS_BUNDLE_FILE_NAME;
            this.manifestFile = RNCodePushConstants.DEFAULT_MANIFEST_FILE_NAME;
            this.hash = jsonObject.optString("hash");
            this.buildTime = jsonObject.optLong("buildTime");
            this.confirm = jsonObject.optBoolean("confirm");
            this.updateTime = jsonObject.optLong("updateTime");
            this.tagId = jsonObject.optString("tagId");
        }
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("baseVersion", baseVersion);
            jsonObject.put("version", version);
            jsonObject.put("versionCode", versionCode);
            jsonObject.put("downloadFile", downloadFile);
            jsonObject.put("hash", hash);
            jsonObject.put("buildTime", buildTime);
            jsonObject.put("confirm", confirm);
            jsonObject.put("updateTime", updateTime);
            jsonObject.put("tagId", tagId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public boolean isExist() {
        return hash != null && !hash.isEmpty();
    }

    public String getBaseVersion() {
        return baseVersion;
    }

    public String getVersion() {
        return version;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public String getDownloadFile() {
        return downloadFile;
    }

    public String getBundleFile() {
        return bundleFile;
    }

    public String getManifestFile() {
        return manifestFile;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getBuildTime() {
        return buildTime;
    }

    public boolean isConfirm() {
        return confirm;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public String getTagId() {
        return tagId;
    }

    public String getMetaInfo() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        String time = format.format(new Date(buildTime));
        return String.format("baseVersion：%s\nversion：%s\nversionCode：%s\nhash：%s\ntagId：%s\nbuildTime：%s\n%s",
                baseVersion, version, versionCode, hash, tagId, buildTime, time);
    }
}
