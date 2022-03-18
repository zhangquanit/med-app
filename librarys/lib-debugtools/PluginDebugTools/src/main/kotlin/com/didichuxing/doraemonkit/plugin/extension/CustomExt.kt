package com.didichuxing.doraemonkit.plugin.extension

open class CustomExt {
    var customSwitch : Boolean = false
    var showLaneFun : Boolean = true

    fun customSwitch(customSwitch: Boolean) {
        this.customSwitch = customSwitch
    }

    fun showLaneFun(showLaneFun: Boolean) {
        this.showLaneFun = showLaneFun
    }
}