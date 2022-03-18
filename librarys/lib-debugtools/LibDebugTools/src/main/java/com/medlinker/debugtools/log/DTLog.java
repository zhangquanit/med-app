package com.medlinker.debugtools.log;

import android.util.Log;

public class DTLog {
    private static boolean mIsLog = true;

    public static void setIsLog(boolean isLog){
        mIsLog = isLog;
    }

    public static void d(String TAG,String msg){
        if (mIsLog){
            Log.d(TAG,msg);
        }
    }

    public static void e(String TAG,String msg){
        if (mIsLog){
            Log.e(TAG,msg);
        }
    }

    public static void i(String TAG,String msg){
        if (mIsLog){
            Log.i(TAG,msg);
        }
    }

    public static void v(String TAG,String msg){
        if (mIsLog){
            Log.v(TAG,msg);
        }
    }

    public static void w(String TAG,String msg){
        if (mIsLog){
            Log.w(TAG,msg);
        }
    }
}
