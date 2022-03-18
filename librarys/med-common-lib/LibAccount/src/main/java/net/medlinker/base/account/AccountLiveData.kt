package net.medlinker.base.account

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class AccountLiveData: MutableLiveData<UserInfo>() {
    override fun observe(owner: LifecycleOwner, observer: Observer<in UserInfo>) {
        super.observe(owner, observer)
        observer.onChanged(value)
    }

    override fun observeForever(observer: Observer<in UserInfo>) {
        super.observeForever(observer)
        observer.onChanged(value)
    }
}