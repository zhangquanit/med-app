package net.medlinker.libhttp.extend

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.medlinker.network.retrofit.RetrofitProvider
import com.medlinker.network.retrofit.error.ApiException
import net.medlinker.libhttp.BaseEntity
import net.medlinker.libhttp.NetCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.ParameterizedType

fun <T> Call<BaseEntity<T>>.exeRequest(callback: NetCallback<T>): Call<BaseEntity<T>> {
    enqueue(object : Callback<BaseEntity<T>> {
        override fun onResponse(
            call: Call<BaseEntity<T>>?,
            response: Response<BaseEntity<T>>
        ) {
            callback.onComplete()
            if (response.code() != 200) {
                callback.onError(ApiException(response.code(), response.message()))
                return
            }
            val responseData = response.body()
            try {
                RetrofitProvider.INSTANCE.checkResponse(
                    responseData!!.errcode,
                    responseData.errmsg,
                    responseData.data
                )
            } catch (e: Exception) {
                e.printStackTrace()
                callback.onError(ApiException(responseData!!.errcode, responseData.errmsg))
                return
            }

            dealNull(responseData, callback)

            callback.onSuccess(responseData.data)
        }

        override fun onFailure(call: Call<BaseEntity<T>>?, t: Throwable?) {
            callback.onComplete()
            callback.onError(t)
        }
    })
    return this
}

private fun <T> dealNull(response: BaseEntity<T>, callback: NetCallback<T>) {
    if (response.data == null) {
        val clazz =
            (callback.javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<T>
        val constructor = clazz.getDeclaredConstructor()
        constructor.isAccessible = true
        response.data = constructor.newInstance()
    }
}

fun <T> Call<BaseEntity<T>>.bindStopLifeCycle(activity: FragmentActivity): Call<BaseEntity<T>> {
    activity.lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun stopHttp() {
            cancel()
        }
    })
    return this
}

fun <T> Call<BaseEntity<T>>.bindDestroyLifeCycle(activity: FragmentActivity): Call<BaseEntity<T>> {
    activity.lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun stopHttp() {
            cancel()
        }
    })
    return this
}
