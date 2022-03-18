package net.medlinker.base.account

import androidx.annotation.Keep

@Keep
data class LoginInfo(
        var userId: Long = 0,
        var sessionId: String? = ""
)
