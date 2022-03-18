package com.medlinker.hybridapp.entity

import org.json.JSONObject


/**
 * @author hmy
 * @time 4/20/21 12:33
 */
data class UserInfoEntity(val userName: String) {

    fun toJsonObject(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("userName", userName)
        return jsonObject
    }
}