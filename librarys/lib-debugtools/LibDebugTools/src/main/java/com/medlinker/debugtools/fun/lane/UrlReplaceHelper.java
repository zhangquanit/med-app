package com.medlinker.debugtools.fun.lane;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.medlinker.debugtools.config.DTConfig;
import com.medlinker.debugtools.log.DTLog;

import java.util.Map;

import okhttp3.HttpUrl;

public class UrlReplaceHelper {
    private static final String TAG = "UrlReplaceHelper";

    public static String urlInterceptor(String url) {
        DTLog.d(TAG, "origin url =" + url);
        String lane = DTLaneStorage.getLaneDomains();
        if (TextUtils.isEmpty(lane) || TextUtils.isEmpty(url)) {
            return url;
        }
        //需要替换的域名map
        Map<String, String> maps = new Gson().fromJson(lane, Map.class);
        if (null == maps || maps.size() < 1) {
            return url;
        }

        Uri uri = Uri.parse(url);
        String host = uri.getHost();
        int port = uri.getPort();
        String scheme = uri.getScheme();

        String laneDomain = DTConfig.instance().getLaneConfig().getLaneDomain();
        String apiLaneHost = Uri.parse(laneDomain).getHost();
        if (TextUtils.equals(host, apiLaneHost)) { //泳道接口
            return url;
        }

        String replaceHost = null;
        int replacePort = -1;

        for (Map.Entry<String, String> entry : maps.entrySet()) {
            String keyHost = getHost(entry.getKey());
            int keyPort = getHostPort(entry.getKey());
            DTLog.d(TAG, "old port = " + port + "key port = " + keyPort);
            if (TextUtils.equals(keyHost, host) && keyPort == port) {
                replaceHost = getHost(entry.getValue());
                replacePort = getHostPort(entry.getValue());
                break;
            }
        }

        if (TextUtils.isEmpty(replaceHost)) {
            return url;
        }

        String realReplaceHost = replaceHost;
        HttpUrl.Builder builder = HttpUrl.parse(url).newBuilder();
        if (replaceHost.startsWith("@")) {
            scheme = "https";
            realReplaceHost = replaceHost.substring(1);
            builder.scheme(scheme);
        }
        builder.host(realReplaceHost);

        if (port != replacePort){
            int defaultPort = "http".equals(scheme) ? 80 : 443;
            builder.port(-1 == replacePort ? defaultPort : replacePort);
        }

        String replaceUrl = builder.build().toString();
        DTLog.i(TAG, "replace url = " + replaceUrl);
        return replaceUrl;
    }

    private static int getHostPort(String domain) {
        try {
            String[] s = domain.split(":");
            return (null == s || s.length <= 1) ? -1 : Integer.parseInt(s[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private static String getHost(String domain) {
        try {
            String[] s = domain.split(":");
            return (null == s || s.length <= 0) ? "" : s[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
