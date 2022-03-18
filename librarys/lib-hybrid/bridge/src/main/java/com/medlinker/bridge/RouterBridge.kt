package com.medlinker.bridge

import android.content.Context


/**
 * @author hmy
 * @time 4/14/21 17:05
 */
interface RouterBridge {

    /**
     * 路由
     * @param type 'native' | 'h5' | 'rn'
     * @return 是否拦截
     */
    fun route(context: Context?, url: String?, type: String?): Boolean
}