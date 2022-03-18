package net.medlinker.libhttp


abstract class NetCallback<T> {
    open fun onError(throwable: Throwable?){
    }

    open fun onComplete() {}

    //请求成功
    abstract fun onSuccess(data: T)
}