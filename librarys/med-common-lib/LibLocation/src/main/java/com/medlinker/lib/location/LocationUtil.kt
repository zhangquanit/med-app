package com.medlinker.lib.location

import android.Manifest
import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.baidu.location.*
import com.medlinker.lib.permission.ext.MedPermissionUtil

class LocationUtil {
    /**
     * 定位回调接口
     */
    interface LocateListener {
        /**
         * 定位成功
         */
        fun onLocateSuccess(location: BDLocation)

        /**
         * 定位失败
         */
        fun onLocateFault()

        /**
         * 无权限
         */
        fun noPermissions()
    }

    companion object {
        private var mLocateClient: LocationClient? = null
        private var mOption: LocationClientOption? = null
        private var mLocationListener: BDAbstractLocationListener? = null

        private fun init(context: Context, timeStep: Int) {
            if (null != mLocateClient) {
                if (mLocateClient!!.isStarted) {
                    mLocateClient!!.stop()
                }
            } else {
                mLocateClient = LocationClient(context)
                mOption = LocationClientOption().apply {
                    //可选，设置定位模式，默认高精度
                    //可选，设置定位模式，默认高精度
                    setLocationMode(com.baidu.location.LocationClientOption.LocationMode.Hight_Accuracy)
                    //使用高精度和仅用设备两种定位模式的，参数必须设置为true
                    //使用高精度和仅用设备两种定位模式的，参数必须设置为true
                    setCoorType("bd09ll")
                    setOpenGps(true)
                    setScanSpan(if(timeStep < 1000) 0 else timeStep)
                    setLocationNotify(true)
                    setIsNeedAddress(true)
                }
            }
        }

        /**
         * 定位
         * @param activity 用于请求权限，和初始化定位
         * @param listener 定位回调
         * @param timeStep 定位时间间隔，单位毫秒，小于1000时只定位一次， 大于1000，定位多次并且需要手动调用destroy()方法
         * @param listener 定位回调方法
         */
        @JvmStatic
        fun location(activity: FragmentActivity, timeStep: Int, listener: LocateListener?) {
            activity.runOnUiThread {
                MedPermissionUtil(activity)
                        .requestPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        .onResult { granted ->
                            try {
                                if (granted) {
                                    startLocation(activity.application, listener, timeStep)
                                } else {
                                    listener?.noPermissions()
                                }
                            } catch (throwable: Throwable) {
                                throwable.printStackTrace()
                                listener?.onLocateFault()
                            }
                        }
            }
        }


        private fun startLocation(context: Context, listener: LocateListener?, timeStep: Int) {
            init(context, timeStep)
            mLocationListener = MyLocationListener(listener, timeStep)
            mLocateClient?.registerLocationListener(mLocationListener)
            mLocateClient?.locOption = mOption
            mLocateClient?.start()
        }

        /**
         * 如果不是只定位一次。需要手动调用destory()方法释放资源
         */
        @JvmStatic
        fun destroy() {
            mLocateClient?.let {
                it.unRegisterLocationListener(mLocationListener)
                if (it.isStarted) {
                    it.stop()
                }
            }

            mLocationListener = null
            mOption = null
            mLocateClient = null
        }

    }

    class MyLocationListener(private val mListener: LocateListener?, private val mTimeStep: Int):
        BDAbstractLocationListener() {
        override fun onReceiveLocation(loc: BDLocation?) {
            if (loc == null || loc.getProvince() == null || loc.getCity() == null){
                mListener?.onLocateFault()

            } else {
                mListener?.onLocateSuccess(loc)
            }

            if (mTimeStep < 1000) {
                destroy()
            }
        }

    }
}