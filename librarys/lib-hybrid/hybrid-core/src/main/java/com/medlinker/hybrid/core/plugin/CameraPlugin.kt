package com.medlinker.hybrid.core.plugin

import android.app.Activity
import android.content.Intent
import com.medlinker.bridge.BridgeErrorCode
import com.medlinker.bridge.BridgeManager
import com.medlinker.bridge.BridgeRequestCode
import com.medlinker.hybrid.core.jsbridge.WVApiPlugin
import com.medlinker.hybrid.core.jsbridge.WVCallbackContext
import com.medlinker.hybrid.core.jsbridge.entity.WVHybridCallbackEntity
import com.medlinker.hybrid.core.webview.IHybrid
import org.json.JSONArray


/**
 * @author hmy
 * @time 4/21/21 10:13
 */
class CameraPlugin : WVApiPlugin() {

    private var mCameraClipCallbackId = ""
    private var mCameraThumbSize = 100

    private var mAlbumClipCallbackId = ""
    private var mAlbumThumbSize = 100

    override fun supportMethodNames(): Array<String> {
        return arrayOf("callCamera", "callAlbum", "saveToAlbum", "showGallery")
    }

    override fun execute(hybrid: IHybrid?, methodName: String, params: String, callbackContext: WVCallbackContext): Boolean {
        try {
            when (methodName) {
                "callCamera" -> {
                    mCameraClipCallbackId = callbackContext.callbackId
                    BridgeManager.getCameraBridge()?.let {
                        mCameraThumbSize = it.callCamera(hybrid?.getCurContext(), params, BridgeRequestCode.REQUEST_CODE_CAMERA) { permissionAccept, throwable ->
                            if (throwable != null) {
                                sendHybridCallback(mCameraClipCallbackId, WVHybridCallbackEntity(BridgeErrorCode.PERMISSION_REFUSE,
                                        throwable.message.toString()))
                            } else if (!permissionAccept) {
                                sendHybridCallback(mCameraClipCallbackId, WVHybridCallbackEntity(BridgeErrorCode.PERMISSION_REFUSE,
                                        BridgeErrorCode.PERMISSION_REFUSE_CALL_CAMERA))
                            }
                        }
                    }
                    return true
                }
                "callAlbum" -> {
                    mAlbumClipCallbackId = callbackContext.callbackId
                    BridgeManager.getCameraBridge()?.let {
                        mAlbumThumbSize = it.callAlbum(hybrid?.getCurContext(), params, BridgeRequestCode.REQUEST_CODE_ALBUM) { permissionAccept, throwable ->
                            if (throwable != null) {
                                sendHybridCallback(mCameraClipCallbackId, WVHybridCallbackEntity(BridgeErrorCode.PERMISSION_REFUSE,
                                        throwable.message.toString()))
                            } else if (!permissionAccept) {
                                sendHybridCallback(mCameraClipCallbackId, WVHybridCallbackEntity(BridgeErrorCode.PERMISSION_REFUSE,
                                        BridgeErrorCode.PERMISSION_REFUSE_EXTERNAL_STORAGE))
                            }
                        }
                    }
                    return true
                }
                "saveToAlbum" -> {
                    hybrid?.getCurContext()?.let {
                        if (it is Activity) {
                            BridgeManager.getCameraBridge()?.saveToAlbum(it, params) { saveSuccess, permissionAccept, throwable ->
                                val callbackId = callbackContext.callbackId
                                when {
                                    throwable != null -> {
                                        sendHybridCallback(callbackId, WVHybridCallbackEntity(0, throwable.message.toString(), false))
                                    }
                                    permissionAccept -> {
                                        sendHybridCallback(callbackId, WVHybridCallbackEntity(0, throwable?.message.toString(), saveSuccess))
                                    }
                                    else -> {
                                        sendHybridCallback(callbackId, WVHybridCallbackEntity(BridgeErrorCode.PERMISSION_REFUSE,
                                                BridgeErrorCode.PERMISSION_REFUSE_EXTERNAL_STORAGE))
                                    }
                                }
                            }
                        }
                    }
                    return true
                }
                "showGallery" -> {
                    hybrid?.getCurContext()?.let {
                        BridgeManager.getCameraBridge()?.showGallery(it, params)
                    }
                    sendHybridCallbackVoid(callbackContext)
                    return true
                }
                else -> {
                    sendHybridMethodNotFoundCallback(callbackContext.callbackId)
                    return true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == BridgeRequestCode.REQUEST_CODE_CAMERA) {
            BridgeManager.getCameraBridge()?.let { bridge ->
                bridge.onActivityResultCamera(intent) { singlePath ->
                    val list = ArrayList<String?>(1)
                    list.add(singlePath)
                    bridge.compressionImage(mHybrid?.getCurContext(), list, mCameraThumbSize) {
                        if (it.isEmpty()) {
                            return@compressionImage
                        }
                        sendHybridCallback(mCameraClipCallbackId, WVHybridCallbackEntity().setData(it[0].toJSONObject()))
                    }
                }
            }
        } else if (requestCode == BridgeRequestCode.REQUEST_CODE_ALBUM) {
            BridgeManager.getCameraBridge()?.let { bridge ->
                bridge.onActivityResultAlbum(intent) { pathList ->
                    bridge.compressionImage(mHybrid?.getCurContext(), pathList, mAlbumThumbSize) {
                        if (it.isEmpty()) {
                            return@compressionImage
                        }
                        val jsonArray = JSONArray()
                        for (imagePathEntity in it) {
                            jsonArray.put(imagePathEntity.toJSONObject())
                        }
                        sendHybridCallback(mAlbumClipCallbackId, WVHybridCallbackEntity().setData(jsonArray))
                    }
                }
            }
        }
    }
}