package net.medlinker.imbusiness.entity

import androidx.annotation.Keep


/**
 * @author hmy
 * @time 12/3/21 17:12
 */
@Keep
data class ImButtonEntity(
    val name: String?,
    /**
     * 问诊类型
     */
    val consultType: Int = 0,
    val jumpUrl: String?,
    val icon: String?
)
