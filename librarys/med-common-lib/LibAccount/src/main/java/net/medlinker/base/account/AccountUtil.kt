package net.medlinker.base.account

import com.google.gson.Gson
import com.medlinker.lib.utils.MedKVUtil

object AccountUtil {
    private const val KEY_LOGIN_INFO = "key_login_info"
    private const val KEY_USER_INFO = "key_user_info"

    private var mLoginInfo: LoginInfo? = null
    private var mUserInfo: AccountLiveData = AccountLiveData()
    private var mListeners: ArrayList<OnUserInfoChangeListener>? = null

    fun clear() {
        MedKVUtil.removeKey(KEY_USER_INFO)
        MedKVUtil.removeKey(KEY_LOGIN_INFO)
        mLoginInfo = null;
        mUserInfo.postValue(null)
    }

    fun saveLoginInfo(info: LoginInfo) {
        mLoginInfo = info
        MedKVUtil.set(KEY_LOGIN_INFO, Gson().toJson(info))
        mListeners?.let {
            for (listener in it) {
                listener.onLoginInfoChanged(info)
            }
        }
    }

    fun getLoginInfo(): LoginInfo? {
        if (null == mLoginInfo) {
            try {
                mLoginInfo =
                    Gson().fromJson(MedKVUtil.getString(KEY_LOGIN_INFO), LoginInfo::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return mLoginInfo
    }

    fun saveUserInfo(info: UserInfo) {
        mUserInfo.postValue(info)
        MedKVUtil.set(KEY_USER_INFO, Gson().toJson(info))
        mListeners?.let {
            for (listener in it) {
                listener.onUserInfoChanged(info)
            }
        }
    }

    fun getUserInfo(): UserInfo? {
        var info = mUserInfo.value
        if (null == info) {
            try {
                info = Gson().fromJson(MedKVUtil.getString(KEY_USER_INFO), UserInfo::class.java)
                mUserInfo.postValue(info)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return info
    }

    fun observer() = mUserInfo

    fun addUserInfoChangeListener(listener: OnUserInfoChangeListener?) {
        if (mListeners == null) {
            mListeners = ArrayList()
        }
        listener?.let { mListeners?.add(it) }
    }

    fun removeUserInfoChangeListener(listener: OnUserInfoChangeListener?) {
        mListeners?.let {
            if (it.contains(listener)) {
                it.remove(listener)
            }
        }
    }

}