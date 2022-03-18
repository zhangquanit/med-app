package com.medlinker.bridge

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.text.TextUtils
import org.json.JSONObject


/**
 * @author hmy
 * @time 4/21/21 17:09
 */
class DeviceBridge {

    fun getClipboard(context: Context?, callBack: (content: JSONObject?) -> Unit) {
        Handler().post {
            val text = paste(context)
            val jsonObject = JSONObject()
            jsonObject.put("content", text)
            callBack.invoke(jsonObject)
        }
    }

    fun setClipboard(context: Context?, params: String?) {
        context?.let {
            val jsonObject = JSONObject(params)
            val content = jsonObject.optString("content")

            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.setPrimaryClip(ClipData.newPlainText(content, content))
        }
    }

    /**
     * 获取剪切板内容
     * @return
     */
    private fun paste(context: Context?): String? {
        context?.let {
            val manager: ClipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            if (manager.hasPrimaryClip() && manager.primaryClip?.itemCount!! > 0) {
                val addedText: CharSequence = manager.primaryClip!!.getItemAt(0).text
                val addedTextString = addedText.toString()
                if (!TextUtils.isEmpty(addedTextString)) {
                    return addedTextString
                }
            }
        }
        return ""
    }
}