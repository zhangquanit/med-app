package net.medlinker.main.hybrid.plugin

import com.medlinker.hybrid.core.jsbridge.WVApiPlugin
import com.medlinker.hybrid.core.jsbridge.WVPluginManager

/**
 * @description
 * @author guojianming
 * @date 2021-11-29
 */
class HybridPluginRegister {
    companion object {
        fun setup() {
            registerPlugin("payment", PayPlugin::class.java)
        }

        fun registerPlugin(pluginName: String, clazz: Class<out WVApiPlugin>) {
            WVPluginManager.registerPlugin(pluginName, clazz)
        }

        fun unregisterPlugin(pluginName: String) {
            WVPluginManager.unregisterPlugin(pluginName)
        }
    }


}