package com.medlinker.protocol

import androidx.annotation.Keep


/**
 * @author hmy
 * @time 2/15/22 10:22
 */
@Keep
data class PrivacyEntity(
    val version: String = "",
    val changeLog: String?,
    val url: String?
)