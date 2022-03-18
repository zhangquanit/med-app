package com.medlinker.hybrid.core.utils

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Build.VERSION
import android.text.TextUtils
import java.util.*


/**
 * @author hmy
 * @time 3/12/21 14:05
 */
class WVUtils {

    companion object {
        fun isConnectedNetwork(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            return activeNetwork?.isConnectedOrConnecting == true
        }

        fun hasKitkat(): Boolean {
            return VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        }

        fun getStatusBarHeight(context: Context): Int {
            var height = -1
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                height = context.resources.getDimensionPixelSize(resourceId)
            }
            return height
        }

        fun dpToPx(context: Context?, dipValue: Float): Int {
            context?.let {
                val scale = it.resources.displayMetrics.density
                return (dipValue * scale + 0.5f).toInt()
            }
            return dipValue.toInt()
        }

        fun pxToDp(context: Context?, pxValue: Float): Int {
            context?.let {
                val scale = it.resources.displayMetrics.density
                return (pxValue / scale + 0.5f).toInt()
            }
            return pxValue.toInt()
        }

        fun parseColor(color: String): Int? {
            val newColor = color.trim()
            if (TextUtils.isEmpty(newColor)) {
                return null
            }
            if (newColor.toLowerCase(Locale.ROOT).contains("rgba")) {
                val c = newColor.substring(newColor.indexOf("(") + 1, newColor.indexOf(")"))
                val rgba = c.split(",")
                return Color.argb(rgba[3].toInt(), rgba[0].toInt(), rgba[1].toInt(), rgba[2].toInt())
            }
            if (newColor.toLowerCase(Locale.ROOT).contains("rgb")) {
                val c = newColor.substring(newColor.indexOf("(") + 1, newColor.indexOf(")"))
                val rgb = c.split(",")
                return Color.rgb(rgb[0].toInt(), rgb[1].toInt(), rgb[2].toInt())
            }
            if ("transparent" == color) {
                return Color.TRANSPARENT
            }
            return Color.parseColor(newColor)
        }
    }
}