package net.medlinker.base.mvvm.extend

import net.medlinker.base.mvvm.VMNetCallback
import net.medlinker.libhttp.BaseEntity
import net.medlinker.libhttp.extend.exeRequest
import retrofit2.Call

fun <T> Call<BaseEntity<T>>.startRequest(callback: VMNetCallback<T>): Call<BaseEntity<T>> {
    callback.setRequest(this)
    exeRequest(callback)
    return this
}