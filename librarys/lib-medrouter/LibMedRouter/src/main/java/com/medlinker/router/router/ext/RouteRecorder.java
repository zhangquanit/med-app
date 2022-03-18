package com.medlinker.router.router.ext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangquan
 */
public class RouteRecorder {
    private static Map<Class, String> routeMap = new HashMap<>();
    private static Map<String, String> rnMap = new HashMap<>();
    private static boolean isDebug;

    public static void setDebug(boolean debug) {
        isDebug = debug;
    }

    public static boolean isDebug() {
        return isDebug;
    }

    public static void add(String sourceUrl, Class cls) {
        if (isDebug) routeMap.put(cls, sourceUrl);
    }

    public static Map<Class, String> getClassMap() {
        return routeMap;
    }

    public static void clearClassMap() {
        routeMap.clear();
    }

    public static void addRNRouter(String reactRouter, String routePath) {
        if (isDebug) rnMap.put(reactRouter, routePath);
    }

    public static Map<String, String> getRnMap() {
        return rnMap;
    }

    public static void clearRnMap() {
        rnMap.clear();
    }

    public static void clearAll() {
        clearClassMap();
        clearRnMap();
    }
}
