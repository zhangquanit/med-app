package com.medlinker.hybrid.core.plugin.ui.entity

import android.graphics.Color
import android.text.TextUtils
import com.medlinker.hybrid.core.utils.WVUtils
import org.json.JSONObject


/**
 * @author hmy
 * @time 3/31/21 14:23
 */
class MenuEntity {

    /**
     * dropdown | button
     */
    var type: String = ""
    var icon: String = ""
    var title: String = ""
    var titleSize: Float = 14f
    var titleColor: Int? = Color.parseColor("#4A4A4A")
    var onClick: String = ""
    lateinit var dropdownItems: ArrayList<DropdownItemEntity>

    fun toObject(jsonObject: JSONObject): MenuEntity {
        type = jsonObject.optString("type", "button")
        icon = jsonObject.optString("icon")
        title = jsonObject.optString("title")
        jsonObject.optString("titleSize").let {
            if (!TextUtils.isEmpty(it)) {
                titleSize = it.toFloat()
            }
        }
        jsonObject.optString("titleColor").let {
            if (!TextUtils.isEmpty(it)) {
                titleColor = WVUtils.parseColor(it)
            }
        }
        onClick = jsonObject.optString("onClick")
        val dropdownItemJsonArray = jsonObject.optJSONArray("dropdownItems")
        dropdownItems = ArrayList()
        dropdownItemJsonArray?.let {
            for (i in 0 until dropdownItemJsonArray.length()) {
                val obj: JSONObject = dropdownItemJsonArray.get(i) as JSONObject
                val title = obj.optString("title")
                val icon = obj.optString("icon")
                val onClick = obj.optString("onClick")
                dropdownItems.add(DropdownItemEntity(title, icon, onClick))
            }
        }
        return this
    }
}