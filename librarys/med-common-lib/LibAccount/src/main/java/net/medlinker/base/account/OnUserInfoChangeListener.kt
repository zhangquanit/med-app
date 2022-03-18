package net.medlinker.base.account


/**
 * @author hmy
 * @time 12/30/21 16:50
 */
interface OnUserInfoChangeListener {
    fun onLoginInfoChanged(info: LoginInfo)
    fun onUserInfoChanged(info: UserInfo)
}