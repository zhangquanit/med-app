package com.medlinker.protocol

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import net.medlinker.base.router.RouterUtil
import net.medlinker.base.storage.KVUtil
import net.medlinker.libhttp.NetCallback
import net.medlinker.libhttp.extend.bindDestroyLifeCycle
import net.medlinker.libhttp.extend.exeRequest
import net.medlinker.libhttp.host.HostManager


/**
 * @author hmy
 * @time 2/15/22 10:24
 */
enum class PrivacyManager {
    INSTANCE;

    private var mAgreementPlatform = ""
    private var mClientName = ""
    private var mDefaultPrivacyUrl = ""
    private var mDefaultUserAgreementUrl = ""
    private var mMainColor = Color.BLUE
    private var mTopIconRes = 0
    private var mMainContentOfCollectedInformation = ""
    private var mNewPrivacyEntity: PrivacyEntity? = null

    companion object {
        const val KEY_PRIVACY = "privacy"
    }

    fun init(
        agreementPlatform: String,
        clientName: String,
        defaultPrivacyUrl: String,
        defaultUserAgreementUrl: String,
        mainColor: Int,
        topIconRes: Int,
        mainContentOfCollectedInformation: String
    ) {
        mAgreementPlatform = agreementPlatform
        mClientName = clientName
        mDefaultPrivacyUrl = defaultPrivacyUrl
        mDefaultUserAgreementUrl = defaultUserAgreementUrl
        mMainColor = mainColor
        mTopIconRes = topIconRes
        mMainContentOfCollectedInformation = mainContentOfCollectedInformation
    }

    private fun request(activity: FragmentActivity, callBack: (PrivacyEntity) -> Unit) {
        val param = PrivacyParam(mAgreementPlatform)
        HostManager.getApi(PrivacyApi::class.java)
                .agreement(param)
                .bindDestroyLifeCycle(activity)
                .exeRequest(object : NetCallback<PrivacyEntity>() {
                    override fun onSuccess(data: PrivacyEntity) {
                        callBack.invoke(data)
                    }

                })
    }

    fun checkFirstLoadApp(activity: FragmentActivity) {
        val jsonStr = KVUtil.getString(KEY_PRIVACY)
        if (!TextUtils.isEmpty(jsonStr)) {
            return
        }

        request(activity) {
            cachePrivacyData(it)
        }
    }

    fun checkUpdate(activity: FragmentActivity, liveData: MutableLiveData<PrivacyEntity>?) {
        request(activity) {
            mNewPrivacyEntity = it
            liveData?.value = it
        }
    }

    fun checkShowPrivacyUpdateDialog(activity: FragmentActivity) {
        getNewPrivacyData()?.let {
            val cachePrivacyEntity = getCachePrivacyData()
            if (cachePrivacyEntity.version < it.version) {
                val dialogFragment = PrivacyUpdateDialogFragment()
                dialogFragment.setCanceledOnTouchOutside(false)
                dialogFragment.isCancelable = false
                dialogFragment.show(activity.supportFragmentManager, "PrivacyUpdateProtocol")
            }
        }
    }

    fun cachePrivacyData(data: PrivacyEntity?) {
        data?.let {
            val str = Gson().toJson(data).toString()
            KVUtil.set(KEY_PRIVACY, str)
        }
    }

    fun getNewPrivacyData(): PrivacyEntity? {
        return mNewPrivacyEntity
    }

    fun getCachePrivacyData(): PrivacyEntity {
        val jsonStr = KVUtil.getString(KEY_PRIVACY)
        if (TextUtils.isEmpty(jsonStr)) {
            return PrivacyEntity("0", "", mDefaultPrivacyUrl)
        }
        return Gson().fromJson(jsonStr, PrivacyEntity::class.java)
    }

    fun getPrivacyUrl(): String? {
        val jsonStr = KVUtil.getString(KEY_PRIVACY)
        if (TextUtils.isEmpty(jsonStr)) {
            return mDefaultPrivacyUrl
        }
        val data = Gson().fromJson(jsonStr, PrivacyEntity::class.java)
        return data.url
    }

    fun getUserAgreementUrl(): String {
        return mDefaultUserAgreementUrl
    }

    fun getMainColor(): Int {
        return mMainColor
    }

    fun getTopIconRes(): Int {
        return mTopIconRes
    }

    fun getClientName(): String {
        return mClientName
    }

    fun getMainContentOfCollectedInformation(): String {
        return mMainContentOfCollectedInformation
    }

    fun startPrivacyActivityByNewUrl(context: Context) {
        val intent = Intent(context, PrivacyWebActivity::class.java)
        val url = if (mNewPrivacyEntity == null) getPrivacyUrl() else mNewPrivacyEntity!!.url
        intent.putExtra(RouterUtil.DATA_KEY, url)
        context.startActivity(intent)
    }

    fun startPrivacyActivity(context: Context) {
        val intent = Intent(context, PrivacyWebActivity::class.java)
        intent.putExtra(RouterUtil.DATA_KEY, getPrivacyUrl())
        context.startActivity(intent)
    }

    fun startUserAgreementActivity(context: Context) {
        val intent = Intent(context, PrivacyWebActivity::class.java)
        intent.putExtra(RouterUtil.DATA_KEY, mDefaultUserAgreementUrl)
        context.startActivity(intent)
    }
}