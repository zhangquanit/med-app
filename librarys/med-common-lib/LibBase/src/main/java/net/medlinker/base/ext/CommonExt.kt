package net.medlinker.base.ext

import android.widget.Toast
import com.blankj.utilcode.util.Utils
import com.google.gson.Gson
import com.google.gson.JsonParser
import java.util.*
import kotlin.collections.HashMap


/** toast相关 **/
fun Any.toast(msg: CharSequence?) {
    Toast.makeText(Utils.getApp(), msg, Toast.LENGTH_SHORT).show()
}

fun Any.longToast(msg: CharSequence) {
    Toast.makeText(Utils.getApp(), msg, Toast.LENGTH_LONG).show()
}

/** json相关 **/
fun Any.toJson() = Gson().toJson(this)

inline fun <reified T> Any.toObject() = Gson().fromJson(this.toString(), T::class.java)

fun <T> String.toList(cls: Class<T>): List<T> {

    val list = ArrayList<T>()
    try {
        val gson = Gson()
        val arry = JsonParser().parse(this).asJsonArray
        for (jsonElement in arry) {
            list.add(gson.fromJson(jsonElement, cls))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return list

}

fun Any.toMap(): HashMap<String, Any> {
    var map = HashMap<String, Any>()
    if (this == null) {
        return map
    }
    var clazz = this::class.java;
    var fields = clazz.getDeclaredFields()
    fields.forEach {
        it.isAccessible = true
        if (it.get(this) != null) {
            map.put(it.name, it.get(this))
        }
    }
    return map
}
