package com.medlinker.bridge.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import com.blankj.utilcode.util.Utils
import com.medlinker.bridge.BridgeCallback
import com.medlinker.bridge.BridgeManager
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL

/**
 * @author hmy
 * @time 4/28/21 16:17
 */
object SaveNetPhotoUtils {
    private var context: Context? = null
    private var photoUrl: String? = null
    private var bitmap: Bitmap? = null
    private var exception: Exception? = null

    private var photoName: String? = null
    private var bridgeCallback: BridgeCallback<Exception>? = null

    /**
     * 保存图片，无须自定义名字
     *
     * @param context
     * @param photoUrl
     */
    fun savePhoto(context: Context?, photoUrl: String?, bridgeCallback: BridgeCallback<Exception>?) {
        this.context = context
        this.photoUrl = photoUrl
        this.bridgeCallback = bridgeCallback
        Thread(saveFileRunnable).start()
    }

    /**
     * 定义图片名字保存到相册
     *
     * @param context
     * @param photoUrl
     * @param photoName 图片名字，定义格式 name.jpg/name.png/...
     */
    fun savePhoto(context: Context?, photoUrl: String?, photoName: String?, bridgeCallback: BridgeCallback<Exception>?) {
        this.context = context
        this.photoUrl = photoUrl
        this.photoName = photoName
        this.bridgeCallback = bridgeCallback
        Thread(saveFileRunnable2).start()
    }

    private val saveFileRunnable = Runnable {
        try {
            if (!TextUtils.isEmpty(photoUrl)) {
                val url = URL(photoUrl)
                val inputStream = url.openStream()
                bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
            }
            saveFile(bitmap)
            exception = null
        } catch (e: Exception) {
            exception = e
            e.printStackTrace()
        }
        messageHandler.sendMessage(messageHandler.obtainMessage())
    }

    private val saveFileRunnable2 = Runnable {
        try {
            if (!TextUtils.isEmpty(photoUrl)) {
                val url = URL(photoUrl)
                val inputStream = url.openStream()
                bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
            }
            saveFile(bitmap, photoName)
            exception = null
        } catch (e: Exception) {
            exception = e
            e.printStackTrace()
        }
        messageHandler.sendMessage(messageHandler.obtainMessage())
    }

    /**
     * 保存成功和失败通知
     */
    @SuppressLint("HandlerLeak")
    private val messageHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            bridgeCallback?.onCallback(exception)
        }
    }

    /**
     * 保存图片
     *
     * @param bm
     * @throws IOException
     */
    @Throws(IOException::class)
    fun saveFile(bm: Bitmap?) {
        val path =Utils.getApp().cacheDir.path + BridgeManager.getFileDir() + "/"
        val dirFile = File(path)
        if (!dirFile.exists()) {
            dirFile.mkdir()
        }

        //图片命名
        val fileName = System.currentTimeMillis().toString() + ".jpg"
        val myCaptureFile = File(path + fileName)
        val bos = BufferedOutputStream(FileOutputStream(myCaptureFile))
        bm!!.compress(Bitmap.CompressFormat.JPEG, 80, bos)
        bos.flush()
        bos.close()

        //广播通知相册有图片更新
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val uri = Uri.fromFile(myCaptureFile)
        intent.data = uri
        context!!.sendBroadcast(intent)
    }

    /**
     * 保存图片
     *
     * @param bm
     * @param photoName 图片命名
     * @throws IOException
     */
    @Throws(IOException::class)
    fun saveFile(bm: Bitmap?, photoName: String?) {
        val path = Utils.getApp().cacheDir.path + BridgeManager.getFileDir() + "/"
        val dirFile = File(path)
        if (!dirFile.exists()) {
            dirFile.mkdir()
        }

        //图片命名后保存到相册
        val myCaptureFile = File(path + photoName)
        val bos = BufferedOutputStream(FileOutputStream(myCaptureFile))
        bm!!.compress(Bitmap.CompressFormat.JPEG, 80, bos)
        bos.flush()
        bos.close()

        //广播通知相册有图片更新
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val uri = Uri.fromFile(myCaptureFile)
        intent.data = uri
        context!!.sendBroadcast(intent)
    }

}