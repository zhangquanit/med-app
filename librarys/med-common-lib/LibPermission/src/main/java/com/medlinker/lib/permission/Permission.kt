package com.medlinker.lib.permission

class Permission(@JvmField val mPermission: String, //权限名称
                 @JvmField val mIsGrand: Boolean,   //是否获取到权限
                 @JvmField val mShouldShowRational: Boolean //用户第一次拒绝权限并且之后也没有勾选不再显示权限对话框，该值为true，其他时候都为false
                 ) {
    override fun toString(): String {
        return "$mPermission mIsGrand:$mIsGrand mShouldShowRational:$mShouldShowRational"
    }
}