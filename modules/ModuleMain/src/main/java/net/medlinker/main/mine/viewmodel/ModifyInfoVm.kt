package net.medlinker.main.mine.viewmodel

import androidx.lifecycle.MutableLiveData
import net.medlinker.base.mvvm.BaseViewModel
import net.medlinker.base.mvvm.VMNetCallback
import net.medlinker.base.mvvm.extend.startRequest
import net.medlinker.libhttp.host.HostManager
import com.medlinker.login.net.AccountApi

class ModifyInfoVm: BaseViewModel() {

    val mModifyResult = MutableLiveData<Boolean>()

    fun modifyUserInfo(params:HashMap<String, Any>) {
        HostManager.getApi(AccountApi::class.java).modifyUserInfo(params)
                .startRequest(object : VMNetCallback<Void>(this){
                    override fun onSuccess(data: Void) {
                        mModifyResult.postValue(true)
                    }

                    override fun onError(throwable: Throwable?) {
                        super.onError(throwable)
                        mModifyResult.postValue(false)
                    }
                })
    }
}