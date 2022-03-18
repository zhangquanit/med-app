package net.medlinker.libhttp.host

import com.medlinker.lib.utils.MedAppInfo

const val TYPE_ONLINE = 3
const val TYPE_QA = 4
const val TYPE_PRE = 5

class Host( var mOnLine: String,
 var mQa: String,
 var mPre: String) {

    fun getHost(): String {
        return when (MedAppInfo.envType){
            TYPE_ONLINE -> mOnLine
            TYPE_PRE -> mPre
            else -> mQa
        }
    }
}