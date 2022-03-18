package com.medlinker.bridge

import android.content.Context
import java.util.*


/**
 * @author hmy
 * @time 11/10/21 17:45
 */
class LinkingBridge {
    fun checkInstalled(context: Context?, apps: Array<String>): MutableCollection<Boolean>? {
        context?.let {
            val packageManager = context.packageManager
            val info = packageManager.getInstalledPackages(0)
            if (info.isEmpty()) return null

            val map = LinkedHashMap<String, Boolean>()
            val size: Int = apps.size
            for (i in 0 until size) {
                map[apps[i]] = false
            }
            var count = 0
            for (i in info.indices) {
                val packageName = info[i].packageName
                if (map.containsKey(packageName)) {
                    map[packageName] = true
                    count++
                    if (count == map.size) {
                        break
                    }
                }
            }
            return map.values
        }
        return null
    }
}