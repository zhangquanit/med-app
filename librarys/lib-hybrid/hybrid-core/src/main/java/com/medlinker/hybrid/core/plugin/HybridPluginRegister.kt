package com.medlinker.hybrid.core.plugin

import com.medlinker.hybrid.core.jsbridge.WVApiPlugin
import com.medlinker.hybrid.core.jsbridge.WVPluginManager
import com.medlinker.hybrid.core.plugin.ui.UIPlugin


/**
 * @author hmy
 * @time 3/10/21 11:04
 */
class HybridPluginRegister {

    companion object {

        fun setup() {
            registerPlugin("ui", UIPlugin::class.java)
            registerPlugin("router", RouterPlugin::class.java)
            registerPlugin("social", SocialPlugin::class.java)
            registerPlugin("auth", AuthPlugin::class.java)
            registerPlugin("device", DevicePlugin::class.java)
            registerPlugin("camera", CameraPlugin::class.java)
            registerPlugin("core", CorePlugin::class.java)
            registerPlugin("updater", UpdaterPlugin::class.java)
            registerPlugin("linking", LinkingPlugin::class.java)
        }

        fun registerPlugin(pluginName: String, clazz: Class<out WVApiPlugin>) {
            WVPluginManager.registerPlugin(pluginName, clazz)
        }

        fun unregisterPlugin(pluginName: String) {
            WVPluginManager.unregisterPlugin(pluginName)
        }

    }
}