package com.medlinker.bridge

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.text.TextUtils
import android.util.Base64
import androidx.fragment.app.FragmentActivity
import com.medlinker.bridge.entity.ImageEntity
import com.medlinker.bridge.entity.ImagePathEntity
import com.medlinker.bridge.entity.ImagePreviewEntity
import com.medlinker.bridge.utils.SaveNetPhotoUtils
import com.medlinker.lib.permission.ext.MedPermissionUtil
import org.json.JSONArray
import org.json.JSONObject
import top.zibin.luban.Luban
import java.io.File
import java.io.FileInputStream


/**
 * @author hmy
 * @time 4/21/21 15:09
 */
abstract class CameraBridge {

    @SuppressLint("CheckResult")
    fun callCamera(context: Context?, params: String, requestCode: Int, callBack: (permissionAccept: Boolean, throwable: Throwable?) -> Unit): Int {
        val jsonObject = JSONObject(params)
        val lens = jsonObject.optString("lens", "main") //'main'|'front'	相机镜头，如果手机没有指定的镜头，则调用可用镜头
        val clip = jsonObject.optString("clip") //裁剪图片的高宽比，空字符串不裁剪，4:3 表示长宽比为 4 比 3
        val thumbSize = jsonObject.optInt("thumbSize", 100) //缩略图大小，单位为 KB

        val isFrontCamera = "front" == lens
        var width = 0
        var height = 0
        if (!TextUtils.isEmpty(clip) && clip.contains(":")) {
            val clips = clip.split(":")
            width = if (TextUtils.isEmpty(clips[0])) 1 else clips[0].toInt()
            height = if (TextUtils.isEmpty(clips[1])) 1 else clips[1].toInt()
        }

        if (context is FragmentActivity) {
            MedPermissionUtil(context).requestPermissions(Manifest.permission.CAMERA)
                    .onResult {
                        try {
                            if (it) {
                                onCallCamera(context, isFrontCamera, width, height, requestCode)
                            } else {
                                callBack.invoke(false, null)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            callBack.invoke(false, e)
                        }
                    }
        }
        return thumbSize
    }

    @SuppressLint("CheckResult")
    fun callAlbum(context: Context?, params: String, requestCode: Int, callBack: (permissionAccept: Boolean, throwable: Throwable?) -> Unit): Int {
        val jsonObject = JSONObject(params)
        val max = jsonObject.optInt("max", 1) //最大图片数量
        val clip = jsonObject.optString("clip") //仅当一张图片时有效，裁剪图片的高宽比，空字符串不裁剪，4:3 表示长宽比为 4 比 3
        val thumbSize = jsonObject.optInt("thumbSize", 100) //缩略图大小，单位为 KB

        var width = 0
        var height = 0
        if (!TextUtils.isEmpty(clip) && clip.contains(":")) {
            val clips = clip.split(":")
            width = if (TextUtils.isEmpty(clips[0])) 1 else clips[0].toInt()
            height = if (TextUtils.isEmpty(clips[1])) 1 else clips[1].toInt()
        }

        if (context is FragmentActivity) {
            MedPermissionUtil(context)
                    .requestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .onResult {
                        try {
                            if (it) {
                                onCallAlbum(context, max, width, height, requestCode)
                            } else {
                                callBack.invoke(false, null)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            callBack.invoke(false, e)
                        }
                    }
        }
        return thumbSize
    }

    @SuppressLint("CheckResult")
    fun saveToAlbum(context: Activity, params: String, callBack: (saveSuccess: Boolean, permissionAccept: Boolean, throwable: Throwable?) -> Unit) {
        if (context is FragmentActivity) {
            MedPermissionUtil(context)
                    .requestPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .onResult {
                        try {
                            if (it) {
                                val jsonObject = JSONObject(params)
                                val imageUrl = jsonObject.optString("imageUrl")
                                SaveNetPhotoUtils.savePhoto(context, imageUrl, object : BridgeCallback<Exception> {
                                    override fun onCallback(data: Exception?) {
                                        if (data == null) {
                                            callBack.invoke(true, true, null)
                                        } else {
                                            callBack.invoke(false, true, data)
                                        }
                                    }
                                })
                            } else {
                                callBack.invoke(false, false, null)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            callBack.invoke(false, false, e)
                        }
                    }
        }
    }

    fun showGallery(context: Context, params: String) {
        val jsonObject = JSONObject(params)
        val index = jsonObject.optInt("index", 0)
        val imagesJsonArray: JSONArray? = jsonObject.optJSONArray("images")
        if (imagesJsonArray == null || imagesJsonArray.length() == 0) {
            return
        }

        val imageEntityList = ArrayList<ImageEntity>()
        for (i in 0 until imagesJsonArray.length()) {
            val obj: JSONObject = imagesJsonArray.get(i) as JSONObject
            val url = obj.optString("url")
            val title = obj.optString("title")
            val description = obj.optString("description")

            val imageEntity = ImageEntity(url, title, description)
            imageEntityList.add(imageEntity)
        }
        val imagePreviewEntity = ImagePreviewEntity(index, imageEntityList)

        onShowGallery(context, imagePreviewEntity)
    }

    /**
     * @param isFrontCamera 是否是前置摄像头
     * @param cropWidth 裁剪宽度（比例值）
     * @param cropHeight 裁剪高度（比例值）
     */
    abstract fun onCallCamera(context: Context?, isFrontCamera: Boolean = false, cropWidth: Int, cropHeight: Int, requestCode: Int)

    /**
     * @param maxImgCount 最大图片数量
     * @param cropWidth 裁剪宽度（比例值）
     * @param cropHeight 裁剪高度（比例值）
     */
    abstract fun onCallAlbum(context: Context?, maxImgCount: Int = 1, cropWidth: Int, cropHeight: Int, requestCode: Int)

    abstract fun onShowGallery(context: Context?, imagePreviewEntity: ImagePreviewEntity)

    abstract fun onActivityResultCamera(intent: Intent?, callback: (imagePath: String?) -> Unit)

    abstract fun onActivityResultAlbum(intent: Intent?, callback: (imagePaths: ArrayList<String?>) -> Unit)

    /**
     * 压缩图片，并将图片转成为base64
     */
    @SuppressLint("StaticFieldLeak")
    fun compressionImage(context: Context?, images: ArrayList<String?>, thumbSize: Int, callback: (imagePaths: List<ImagePathEntity>) -> Unit) {
        val task: AsyncTask<ArrayList<String?>, Void, List<ImagePathEntity>> = object : AsyncTask<ArrayList<String?>, Void, List<ImagePathEntity>>() {
            override fun doInBackground(vararg params: ArrayList<String?>): List<ImagePathEntity> {
                val list = ArrayList<ImagePathEntity>()
                var inputStream: FileInputStream? = null
                try {
                    for (path in images) {
                        val files = Luban.with(context)
                                .ignoreBy(thumbSize)
                                .load(path)
                                .setTargetDir(context?.cacheDir?.absolutePath)
                                .filter {
                                    return@filter !TextUtils.isEmpty(it)
                                }.get()

                        val updateFile = files[0]
                        if (null == updateFile || !updateFile.exists()) {
                            continue
                        }

                        inputStream = FileInputStream(updateFile)
                        val buffer = ByteArray(updateFile.length().toInt())
                        inputStream.read(buffer)
                        inputStream.close()
                        val base64Str = Base64.encodeToString(buffer, Base64.NO_WRAP)
                        list.add(ImagePathEntity("", "data:image/jpg;base64,$base64Str"))
                    }
                } catch (e: Exception) {

                } finally {
                    inputStream?.close()
                }
                return list
            }

            override fun onPostExecute(result: List<ImagePathEntity>) {
                callback.invoke(result)
            }
        }
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, images)
    }

    fun deleteSingleFile(file: File) {
        if (file.exists() && file.isFile) {
            file.delete()
        }
    }
}