package net.medlinker.base.mvvm

import net.medlinker.libhttp.BaseEntity
import net.medlinker.libhttp.NetCallback
import retrofit2.Call

open abstract class VMNetCallback<T>(
        private var mViewModel: BaseViewModel?): NetCallback<T>() {

    private lateinit var mRequest: Call<BaseEntity<T>>

    fun setRequest(request: Call<BaseEntity<T>>) {
        mRequest = request
        mViewModel?.attach(mRequest)
    }

    override fun onComplete() {
        super.onComplete()
        mViewModel?.detach(mRequest)
    }

    override fun onError(throwable: Throwable?) {
        throwable?.let {
            mViewModel?.mShowToast?.postValue(it.message)
        }
    }

}