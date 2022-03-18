package net.medlinker.base.account

import androidx.annotation.Keep

@Keep
data class UserInfo(
    var avatar: String? = "",
    var cellphone: String? = "",
    var id: Long? = 0,
    /**
     * 性别：1男  2女
     */
    var sex: Int? = 0,
    /**
     * 昵称
     */
    var username: String? = "",
    /**
     * 姓名
     */
    var name: String? = "",
    /**
     * 出生日期  yyyyMMdd
     */
    var birthDate: Int?=0
)