package com.medlinker.debugtools.fun.lane;

import android.content.Context;
import android.content.SharedPreferences;

import com.medlinker.debugtools.DTModule;

/**
 * @author: pengdaosong
 * @CreateTime: 2021/3/4 11:42 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class DTLaneSwitcherUtils {

    /**
     * 保存泳道数据
     */
    public static void setLane(String lane) {
        EnvStore.setLane(lane);
    }

    public static void setLaneName(String name) {
        EnvStore.setLaneName(name);
    }

    public static final class EnvStore {

        private static final String SP_NAME = "env";
        private static final String SP_LANE = "env_lane"; //泳道数据
        private static final String SP_LANE_NAME = "env_lane_name"; //泳道名

        private static void setLane(String lane) {
            putString(SP_LANE, lane);
        }

        private static void setLaneName(String name) {
            putString(SP_LANE_NAME, name);
        }

        public static String getLane() {
            return getString(SP_LANE, null);
        }

        public static String getLaneName(String defaultName) {
            return getString(SP_LANE_NAME, defaultName);
        }

        private static SharedPreferences getSp() {
            return DTModule.app().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        }

        private static void putString(String key, String value) {
            SharedPreferences.Editor editor = getSp().edit();
            editor.putString(key, value);
            editor.commit(); //因为要杀进程 必须同步提交  apply是异步提交，不一定能保存上
        }

        private static String getString(String key, String defValue) {
            return getSp().getString(key, defValue);
        }

        private static void putInt(String key, int value) {
            SharedPreferences.Editor editor = getSp().edit();
            editor.putInt(key, value);
            editor.commit(); //因为要杀进程 必须同步提交  apply是异步提交，不一定能保存上
        }

        private static int getInt(String key, int defValue) {
            return getSp().getInt(key, defValue);
        }

    }
}
