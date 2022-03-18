package com.medlinker.lib.permission.ext

/**
 *
 * @author zhangquan
 */
object MedPermissionConfig {
    private val mExplainMap = HashMap<String, String>()

    @JvmStatic
    fun addExplain(permission: String, explain: String) {
        mExplainMap[permission] = explain
    }

    @JvmStatic
    fun removeExplain(permission: String) {
        mExplainMap.remove(permission)
    }

    @JvmStatic
    fun addExplain(explans: Map<String, String>) {
        mExplainMap.putAll(explans)
    }

    fun getExplanInfos(): HashMap<String, String> {
        return mExplainMap
    }
}