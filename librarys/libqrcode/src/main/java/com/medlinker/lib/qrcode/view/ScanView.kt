package com.medlinker.lib.qrcode.view

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.medlinker.lib.qrcode.OnDecodeListener
import com.medlinker.lib.qrcode.camera.CameraManager
import com.medlinker.lib.qrcode.decode.DecodeThread
import com.medlinker.lib.qrcode.decode.InnerOnDecodeListener
import com.medlinker.lib.qrcode.utils.BeepManager
import com.medlinker.lib.qrcode.utils.ResultHandler
import java.io.IOException

class ScanView: SurfaceView, SurfaceHolder.Callback{
    private val TAG = "ScanView"

    private lateinit var mCameraManager: CameraManager
    private var mResultHandler: ResultHandler? = null
    private var mIsHasSurface = false
    private var mPlayBeep = true
    private lateinit var mDecodeListener: OnDecodeListener
    private val mBeepManager: BeepManager by lazy {
        BeepManager(context as Activity)
    }

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    /**
     * 扫描成功是否自动播放声音， 默认播放声音
     *
     * @param isPlay, true播放，false不播放
     */
    public fun playBeep(isPlay: Boolean) {
        mPlayBeep = isPlay
    }

    /**
     * 开始扫描，并通过回调返回扫描结果
     */

    public fun startScan(decodeListener: OnDecodeListener) {
        mDecodeListener = decodeListener
        mCameraManager = CameraManager(context.applicationContext)
        mResultHandler = null
        if (mIsHasSurface) {
            // The activity was paused but not stopped, so the surface still
            // exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(getHolder())
        } else {
            // Install the callback and wait for surfaceCreated() to init the
            // camera.
            getHolder().addCallback(this)
        }
    }

    /**
     * 停止扫描
     */
    public fun stopScan() {
        if (mResultHandler != null) {
            mResultHandler!!.quitSynchronously()
            mResultHandler = null
        }

        mBeepManager.close()

        mCameraManager.closeDriver()
        if (!mIsHasSurface) {
            getHolder().removeCallback(this)
        }
    }

    private fun initCamera(surfaceHolder: SurfaceHolder) {
        if (mCameraManager.isOpen()) {
            Log.w(
                TAG,
                "initCamera() while already open -- late SurfaceView callback?"
            )
            return
        }
        try {
            mCameraManager.openDriver(surfaceHolder)
            // Creating the handler starts the preview, which can also throw a
            // RuntimeException.
            if (null == mResultHandler) {
                mResultHandler = ResultHandler(
                    object: InnerOnDecodeListener {
                        override fun onDecodeResult(result: String?, qrBmp: ByteArray?) {
                            if (mPlayBeep) {
                                mBeepManager.playBeepSoundAndVibrate()
                            }
                            mDecodeListener.onDecodeResult(result, qrBmp)
                        }

                        override fun getCropRect(): Rect {

                            return mDecodeListener.cropRect
                        }

                        override fun getSurfaceSize(): Point {
                            return Point(width, height)
                        }
                    },
                    mCameraManager,
                    DecodeThread.ALL_MODE
                )
            }
        } catch (ioe: IOException) {
            Log.w(TAG, ioe)
        } catch (e: RuntimeException) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(
                TAG,
                "Unexpected error initializing camera",
                e
            )
        }
    }

    public fun getCameraResolution(): Point {
        if (::mCameraManager.isInitialized) {
            return mCameraManager.cameraResolution
        }

        return Point(0, 0)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        if (!mIsHasSurface) {
            mIsHasSurface = true
            initCamera(holder)
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        mIsHasSurface = false
    }
}