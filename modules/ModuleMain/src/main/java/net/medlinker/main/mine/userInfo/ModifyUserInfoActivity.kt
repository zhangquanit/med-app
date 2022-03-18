package net.medlinker.main.mine.userInfo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.medlinker.lib.fileupload.RxMedFileUpload
import com.medlinker.lib.fileupload.entity.FileEntity
import com.medlinker.lib.fileupload.entity.UploadConfig
import com.medlinker.lib.imagepicker.ClipCameraActivity
import com.medlinker.lib.imagepicker.ClipPhotoPickerActivity
import com.medlinker.lib.imagepicker.PickerConstants
import com.medlinker.widget.navigation.CommonNavigationBar
import kotlinx.android.synthetic.main.activity_modify_user_info.*
import kotlinx.android.synthetic.main.dialog_image_picker.view.*
import net.medlinker.base.account.AccountUtil
import net.medlinker.base.mvvm.VMActivity
import net.medlinker.main.R
import net.medlinker.main.mine.viewmodel.ModifyInfoVm

/**
 * 编辑资料
 */
class ModifyUserInfoActivity : VMActivity<ModifyInfoVm>() {
    private var mNewHeader: String? = null
    private var mNewName: String? = null

    companion object {
        private const val REQUEST_CAMERA = 100
        private const val REQUEST_ALBUM = 101
        private const val REQUEST_NAME = 102

        fun start(context: Context) {
            context.startActivity(Intent(context, ModifyUserInfoActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modify_user_info)
        addListener()
    }

    override fun initActionBar(navigation: CommonNavigationBar) {
        navigation.showTitle(R.string.edit_info)
    }

    private fun addListener() {
        rl_header.setOnClickListener {
            showPickDialog()
        }

        ll_name.setOnClickListener {
            EditNameActivity.startForResult(this, REQUEST_NAME)
        }

        btn_save_modify.setOnClickListener {
            if (null == mNewHeader && null == mNewName) {
                return@setOnClickListener
            }

            val params = HashMap<String, Any>()
            mNewHeader?.let {
                params["avatar"] = it
            }

            mNewName?.let {
                params["username"] = it
            }

            mViewModel?.modifyUserInfo(params)
        }

        mViewModel?.mModifyResult?.observe(this, Observer {
            if(it){
                AccountUtil.getUserInfo()?.let {
                    if (null != mNewHeader) {
                        it.avatar = mNewHeader
                    }

                    if (null != mNewName) {
                        it.username = mNewName
                    }

                    AccountUtil.saveUserInfo(it)
                    finish()
                }
            }
        })

        AccountUtil.observer().observe(this, Observer { userInfo ->
            userInfo?.let {
                if (!TextUtils.isEmpty(it.avatar)) {
                    Glide.with(this).load(it.avatar).circleCrop().into(iv_header)
                }

                tv_name.text = it.username
            }
        })
    }

    private fun showPickDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_image_picker, null)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        view.tv_cancel.setOnClickListener {
            dialog.dismiss()
        }

        view.tv_camera.setOnClickListener {
            startActivityForResult(Intent(this, ClipCameraActivity::class.java), REQUEST_CAMERA)
            dialog.dismiss()
        }

        view.tv_album.setOnClickListener {
            startActivityForResult(Intent(this, ClipPhotoPickerActivity::class.java), REQUEST_ALBUM)
            dialog.dismiss()
        }

        dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundColor(0x00000000)
        dialog.show()
    }

    @SuppressLint("CheckResult")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || null == data) {
            return
        }

        if (requestCode == REQUEST_ALBUM || requestCode == REQUEST_CAMERA) {
            var avatarFile: String? = data!!.getStringExtra(PickerConstants.BUNDLE_PARAMS)
            if (null == avatarFile) {
                return
            }

            RxMedFileUpload.startUpload(
                UploadConfig()
                    .setUploadBucket("avatar")
                    .setFileEntity(FileEntity(avatarFile.hashCode(), avatarFile))
            )
                .subscribe({ fileEntities ->
                    mNewHeader = fileEntities[0].fileUrl
                    Glide.with(this).load(mNewHeader).circleCrop().into(iv_header)
                }) { throwable ->
                    throwable.printStackTrace()
                }
        } else if (requestCode == REQUEST_NAME) {
            mNewName = data!!.getStringExtra(EditNameActivity.KEY_NAME)
            tv_name.text = mNewName
        }

    }
}