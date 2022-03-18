package com.medlinker.bridge

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.medlinker.bridge.entity.ShareEntity
import com.medlinker.bridge.utils.ContactsUtil
import com.medlinker.lib.permission.ext.MedPermissionUtil
import org.json.JSONArray
import org.json.JSONObject


/**
 * @author hmy
 * @time 4/15/21 16:47
 */
abstract class SocialBridge {

    abstract fun onShare(context: Context, shareEntity: ShareEntity)

    fun share(context: Context, params: String) {
        val jsonObject = JSONObject(params)
        val jsonArray = jsonObject.optJSONArray("platforms")
        var platforms = ArrayList<String>(0)
        jsonArray?.let {
            platforms = ArrayList(jsonArray.length())
            for (i in 0 until jsonArray.length()) {
                val platform = jsonArray.get(i) as String
                platforms.add(platform)
            }
        }
        val title = jsonObject.optString("title")
        val desc = jsonObject.optString("desc")
        val image = jsonObject.optString("image")
        val link = jsonObject.optString("link")
        val innerLink = jsonObject.optString("innerLink")
        val showCopy = jsonObject.optBoolean("showCopy")
        val shareEntity = ShareEntity(platforms, title, desc, image, link, innerLink, showCopy)

        BridgeManager.getSocialBridge()?.onShare(context, shareEntity)
    }

    /**
     * 获取联系人
     */
    @SuppressLint("CheckResult")
    fun getContact(context: FragmentActivity, callBack: (contacts: JSONArray?, permissionAccept: Boolean, throwable: Throwable?) -> Unit) {
        MedPermissionUtil(context)
                .requestPermissions(Manifest.permission.READ_CONTACTS)
                .onResult {
                    try {
                        if (it) {
                            try {
                                val contactsList: List<ContactsUtil.ContactsEntity> = ContactsUtil.getInstance().readContactsList(context)
                                val jsonArray = JSONArray()
                                for (entity in contactsList) {
                                    val jsonObject = JSONObject()
                                    jsonObject.put("name", entity.name)
                                    jsonObject.put("cellphone", entity.phoneNumber)
                                    jsonArray.put(jsonObject)
                                }
                                callBack.invoke(jsonArray, true, null)
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                                callBack.invoke(null, true, e)
                            }
                        } else {
                            callBack.invoke(null, false, null)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        callBack.invoke(null, true, e)
                    }
                }
    }

    @SuppressLint("CheckResult")
    fun callPhone(context: FragmentActivity, params: String, callBack: (permissionAccept: Boolean, throwable: Throwable?) -> Unit) {
        val jsonObject = JSONObject(params)
        val cellphone = jsonObject.optString("cellphone")

        val intent = Intent(Intent.ACTION_DIAL)
        val data = Uri.parse("tel:$cellphone")
        intent.data = data
        context.startActivity(intent)
    }

    @SuppressLint("CheckResult")
    fun sendSms(context: FragmentActivity, params: String, callBack: (permissionAccept: Boolean, throwable: Throwable?) -> Unit) {
        val jsonObject = JSONObject(params)
        val cellphone = jsonObject.optString("cellphone")
        val content = jsonObject.optString("content")

        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("smsto:$cellphone")
        intent.putExtra("sms_body", content)
        context.startActivity(intent)

//        PermissionUtil(context)
//                .requestPermissions(Manifest.permission.SEND_SMS)
//                .onResult { _, rejected ->
//                    try {
//                        if (rejected.isEmpty()) {
//                            val smstoUri = Uri.parse("smsto:")
//                            val intent = Intent(Intent.ACTION_SENDTO, smstoUri)
//                            intent.putExtra(Telephony.Sms.ADDRESS, cellphone)
//                            intent.putExtra("sms_body", content)
//                            context.startActivity(intent)
//                        } else {
//                            callBack.invoke(false, null)
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                        callBack.invoke(false, e)
//                    }
//                }
    }
}