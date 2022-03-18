package com.medlinker.lib.utils

import android.app.Application

class MedAppInfo {
    companion object {
        @JvmStatic
        lateinit var appContext: Application

        //编译模式是debug还是release
        @JvmStatic
        var isDebug: Boolean = false

        //打包环境dev，qa， online
        @JvmStatic
        var envType: Int = MedApiConstants.API_TYPE_DEV

        //应用包名
        @JvmStatic
        lateinit var applicationId: String

        //versioncode
        @JvmStatic
        var versionCode: Int = 0

        //应用包名
        @JvmStatic
        lateinit var versionName: String

        //构建时间
        @JvmStatic
        lateinit var buildTime: String

        @JvmStatic
        fun logDebugable(): Boolean = envType != MedApiConstants.API_TYPE_ONLINE
    }
}