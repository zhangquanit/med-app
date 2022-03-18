package com.doctorwork.android.logreport;

import android.os.Environment;

import java.io.File;

/**
 * @author : HuBoChao
 * @date : 2020/12/3
 * @desc :
 */
public class Constant {
    public static boolean isGranted;
    public static final String localDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
            File.separator + "LogReport";
    public static final String uuidFile = "medLinkerUUID.png";
    public static final String ABOUT_LOG_REPORT_SP = "about_log_report_sp";
    public static final String SP_UUID = "sp_uuid";
    public static final String SP_PHONE = "sp_phone";
    public static final String BASE_URL_PROD = "https://api.doctorwork.com/web-monitor/";
    public static final String BASE_URL_DEV = "https://api-dev.doctorwork.com/web-monitor/";
    public static final String LOG_REPORT = "api/v1/native/report";
}
