package com.medlinker.bridge


/**
 * @author hmy
 * @time 4/16/21 14:54
 */
object BridgeErrorCode {

    const val SUCCESS = 0;

    /**
     * 方法不存在
     */
    const val METHOD_NOT_EXIST = -1

    /**
     * 模块不存在
     */
    const val MODULE_NOT_EXIST = -2

    /**
     *crash异常
     */
    const val EXCEPTION = 10001

    /**
     * 权限被拒绝
     */
    const val PERMISSION_REFUSE = 10000
    const val PERMISSION_REFUSE_CALL_CAMERA = "未获取相机权限"
    const val PERMISSION_REFUSE_CALL_PHONE = "未获取拨号权限"
    const val PERMISSION_REFUSE_SEND_SMS = "未获取短信权限"
    const val PERMISSION_REFUSE_READ_CONTACTS = "未获取联系人权限"
    const val PERMISSION_REFUSE_EXTERNAL_STORAGE = "未获取文件读写权限"
}