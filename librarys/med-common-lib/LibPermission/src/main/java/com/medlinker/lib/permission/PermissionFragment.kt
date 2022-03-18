package com.medlinker.lib.permission

import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

class PermissionFragment : Fragment() {

    private val PERMISSION_REQUEST_CODE = 111
    private lateinit var mPermissionUtil: PermissionUtil
    private var mRequestPermissions: Array<out String> = arrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val realReq = permissions.toList().equals(mRequestPermissions.toList())
        if (realReq || requestCode == PERMISSION_REQUEST_CODE) {
            if (::mPermissionUtil.isInitialized) {
                mPermissionUtil.onPermissionResult(permissions, grantResults)
            }
        }
    }

    fun requestPermissions(permissions: Array<out String>, permissionUtil: PermissionUtil) {
        mPermissionUtil = permissionUtil
        mRequestPermissions = permissions
        requestPermissions(permissions, PERMISSION_REQUEST_CODE)
    }

    fun isGranted(permission: String): Boolean {
        activity?.let {
            return@isGranted ActivityCompat.checkSelfPermission(
                it,
                permission
            ) == PackageManager.PERMISSION_GRANTED || !isMarshmallow()
        }

        return !isMarshmallow()
    }

    private fun isMarshmallow(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }


    @TargetApi(Build.VERSION_CODES.M)
    fun isRevoked(permission: String): Boolean {
        activity?.let {
            return@isRevoked it.packageManager.isPermissionRevokedByPolicy(
                permission,
                it.packageName
            ) && isMarshmallow()
        }

        return false
    }

}