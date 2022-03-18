package com.medlinker.login.sy;


import com.blankj.utilcode.util.NetworkUtils;

/**
 * @author: pengdaosong CreateTime:  2019-08-20 09:53 Email：pengdaosong@medlinker.com Description:
 */
public class SYCodeUtils {


    public static String code2Str(int code) {
        return code2Str(code, "不能使用一键登录功能!");
    }


    public static String code2Str(int code, String defaultStr) {

        switch (code) {
            case 1003:{
                if (!NetworkUtils.getWifiEnabled() && !NetworkUtils.getMobileDataEnabled()){
                    return "请检查你的网络!";
                }else{
                    return "一键登录失败!";
                }
            }
            case 1007:
                return "网络请求失败!";
            case 1006:
                return "未开启移动数据网络";
            case 1008:
                return "未开启网络";
            case 1009:
            case 1023:
                return "未检测到SIM卡";
            case 1031:
                return "请求过于频繁";
            case 1032:
                return "用户禁用";
            default:
                return defaultStr;
        }
    }
}
