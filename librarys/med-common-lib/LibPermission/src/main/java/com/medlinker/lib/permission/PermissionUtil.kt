package com.medlinker.lib.permission

import android.content.pm.PackageManager
import androidx.fragment.app.FragmentActivity

class PermissionUtil(activity: FragmentActivity) {
    private lateinit var mPermissionFragment: PermissionFragment
    private var mListener: ((granted: List<Permission>, rejected: List<Permission>) -> Unit)? = null
    private var mListenerEach: ((permission: Permission) -> Unit)? = null
    private val mActivity: FragmentActivity = activity

    private val mGrantedPermissions = arrayListOf<Permission>()
    private val mRejectPermissions = arrayListOf<Permission>()

    private val mFragmentTag = "med#!permission"

    private var mRequestPermissions: Array<out String> = arrayOf()

    init {
        initPermissionsFragment()
    }

    private fun initPermissionsFragment() {
        var permissionsFragment: PermissionFragment? =
            mActivity.supportFragmentManager.findFragmentByTag(mFragmentTag) as? PermissionFragment
        if (null == permissionsFragment) {
            permissionsFragment = PermissionFragment()
            mActivity.supportFragmentManager
                .beginTransaction()
                .add(permissionsFragment, mFragmentTag)
                .commitAllowingStateLoss()
            mActivity.supportFragmentManager.executePendingTransactions()
        }

        mPermissionFragment = permissionsFragment
    }

    /**
     * 请求权限
     *
     * @param permissions 请求的权限数组
     */
    fun requestPermissions(vararg permissions: String): PermissionUtil {
        mRequestPermissions = permissions

        return this
    }

    internal fun requestPermissiones(permissions: Array<out String>): PermissionUtil {
        mRequestPermissions = permissions

        return this
    }

    /**
     * 请求权限结果。返回已经授予的权限列表和被拒绝的权限列表
     */
    fun onResult(listener: (granted: List<Permission>, rejected: List<Permission>) -> Unit): PermissionUtil {
        mListener = listener
        mListenerEach = null
        if (mRequestPermissions.isNotEmpty()) {
            requestReal()
        }

        return this
    }

    /**
     * 获取请求权限结果， 每次返回一个权限的请求结果
     */
    fun onResultForEach(listener: (permission: Permission) -> Unit): PermissionUtil {
        mListenerEach = listener
        mListener = null
        if (mRequestPermissions.isNotEmpty()) {
            requestReal()
        }

        return this
    }

    private fun requestReal() {
        mGrantedPermissions.clear()
        mRejectPermissions.clear()
        val needRequestPermission = arrayListOf<String>()
        mRequestPermissions.forEach { permission ->
            if (mPermissionFragment.isGranted(permission)) {
                mGrantedPermissions.add(
                    Permission(
                        permission,
                        mIsGrand = true,
                        mShouldShowRational = false
                    )
                )
            } else if (mPermissionFragment.isRevoked(permission)) {
                mRejectPermissions.add(
                    Permission(
                        permission,
                        mIsGrand = false,
                        mShouldShowRational = false
                    )
                )
            } else {
                needRequestPermission.add(permission)
            }
        }

        if (needRequestPermission.isNotEmpty()) {
            mPermissionFragment.requestPermissions(needRequestPermission.toTypedArray(), this)
        } else {
            sendResult()
        }
    }

    internal fun onPermissionResult(
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        for (i in permissions.indices) {
            val permission = Permission(
                permissions[i], mIsGrand = grantResults[i] == PackageManager.PERMISSION_GRANTED,
                mShouldShowRational = mPermissionFragment.shouldShowRequestPermissionRationale(
                    permissions[i]
                )
            )
            if (permission.mIsGrand) {
                mGrantedPermissions.add(permission)
            } else {
                mRejectPermissions.add(permission)
            }
        }

        sendResult()
    }

    private fun sendResult() {
        mListener?.let {
            it.invoke(mGrantedPermissions, mRejectPermissions)
        }


        mListenerEach?.let {
            val allResult = arrayListOf<Permission>()
            allResult.addAll(mGrantedPermissions)
            allResult.addAll(mRejectPermissions)
            allResult.forEach { permission ->
                it.invoke(permission)
            }
        }

        mListener = null
        mListenerEach = null
        mRequestPermissions = arrayOf()
    }

}