package com.medlinker.bridge.entity

import org.json.JSONObject


/**
 * @author hmy
 * @time 4/29/21 12:26
 */
data class ImagePathEntity(
        /**
         * 原始图片 id
         */
        val imageId: String,
        /**
         * 压缩后的图片 base64 字符串
         */
        val imageBase64: String) {

    fun toJSONObject(): JSONObject {
        val jsonObject = JSONObject();
        jsonObject.put("imageId", imageId)
        jsonObject.put("thumb", imageBase64)
        return jsonObject
    }
}