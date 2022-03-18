package net.medlinker.base.mvvm

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call

/**
 * @author: pengdaosong CreateTime:  2020-09-17 13:37 Email：pengdaosong@medlinker.com Description:
 * 注意：移除前LifecycleObserver请确认子类是否有使用，否则导致业务异常
 */
open class BaseViewModel : ViewModel(), LifecycleObserver {
    val mShowLoading = MutableLiveData<Boolean>()
    val mShowToast = MutableLiveData<String>()
    private val mRequests = ArrayList<Call<*>>()

    fun attach(request: Call<*>) {
        mRequests.add(request)
    }

    fun detach(request: Call<*>) {
        mRequests.remove(request)
    }

    override fun onCleared() {
        mRequests.forEach {
            it.cancel()
        }
        super.onCleared()
    }

    fun load() {}
    fun showLoading() {
        mShowLoading.postValue(true)
    }

    fun hiddenLoading() {
        mShowLoading.postValue(false)
    }

    fun showToast(message: String) {
        mShowToast.postValue(message)
    }
}