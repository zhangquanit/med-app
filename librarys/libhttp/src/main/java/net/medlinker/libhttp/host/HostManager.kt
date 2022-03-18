package net.medlinker.libhttp.host

import com.medlinker.network.retrofit.RetrofitProvider
import retrofit2.Retrofit
import java.util.concurrent.ConcurrentHashMap


object HostManager {
    private val mHostMap = ConcurrentHashMap<String, Host>()
    private val mRetrofitMap = ConcurrentHashMap<String, Retrofit>()
    private val mApiInterface = ConcurrentHashMap<Class<*>, Any>()

    /**
     * 添加一个host
     */
    fun addHost(hostName: String, host: Host): HostManager {
        mHostMap[hostName] = host
        return this
    }

    /**
     * 添加一组host
     */
    fun addHosts(hosts: Map<String, Host>): HostManager {
        mHostMap.putAll(hosts)
        return this
    }

    /**
     * 获取对应环境的host
     */
    fun getHost(hostName: String): String {
        return mHostMap[hostName]!!.getHost()
    }

    /**
     * 获取对应接口retrofit的网络访问对象
     */
    fun <T> getApi(clazz: Class<T>): T {
        var interfaceObj: Any? = mApiInterface[clazz]
        if (null != interfaceObj) {
            return interfaceObj as T
        }

        val hostName = clazz.getAnnotation(HostName::class.java)!!.value
        var retrofit: Retrofit? = mRetrofitMap[hostName]
        if (null == retrofit) {
            retrofit = RetrofitProvider.INSTANCE.buildRetrofit(getHost(hostName))
            mRetrofitMap[hostName] = retrofit
        }

        interfaceObj = mRetrofitMap[hostName]!!.create(clazz)
        mApiInterface[clazz] = interfaceObj!!

        return interfaceObj
    }

    fun getHosts(): Map<String, Host> {
        return mHostMap
    }

    fun clear() {
        mRetrofitMap.clear()
        mApiInterface.clear()
    }
}