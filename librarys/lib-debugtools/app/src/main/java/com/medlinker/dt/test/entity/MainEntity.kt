package com.medlinker.dt.test.entity

import android.app.Activity
import com.medlinker.debugtools.base.DTBaseActivity

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-29 12:45
 * Email：pengdaosong@medlinker.com
 * Description:
 */
data class MainEntity(
        var clz: Class<out Activity> = DTBaseActivity::class.java,
        var title: String = "",
        var content: String = "",
        var type : Int = 0
)