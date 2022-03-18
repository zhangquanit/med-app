package com.medlinker.lib.share

enum class ShareType( val type: Int, val des: String) {
    TEXT(0, "文本"),
    IMAGE_LOCAL(1, "本地图片"),
    IMAGE_URL(2, "网络图片"),
    WEB(3, "纯URL"),
    MUSIC(4, "音乐"),
    VIDEO(5, "网络视频"),
    VIDEO_LOCAL(6, "本地视频"),
    TEXT_AND_IMAGE(7, "图片和文本"),
    FILE(8, "文件"),
    EMOJI(9, "emoji"),
    AUTH(10, "授权操作");

    companion object {
        fun getSupportType(platformName: String): List<ShareType> {
            return when (platformName) {
                ShareSdkHelper.PLATFORM_WECHAT -> arrayListOf(TEXT, IMAGE_LOCAL, IMAGE_URL, WEB, MUSIC, VIDEO, EMOJI, AUTH)
                ShareSdkHelper.PLATFORM_WECHAT_CIRCLE -> arrayListOf(TEXT, IMAGE_LOCAL, IMAGE_URL, WEB, MUSIC, VIDEO)
                ShareSdkHelper.PLATFORM_SINA -> arrayListOf(TEXT, TEXT_AND_IMAGE, IMAGE_LOCAL, IMAGE_URL, WEB, AUTH)
                ShareSdkHelper.PLATFORM_QQ -> arrayListOf(IMAGE_LOCAL, IMAGE_URL, WEB, MUSIC, VIDEO, AUTH)
                ShareSdkHelper.PLATFORM_QQ_ZONE -> arrayListOf(TEXT, IMAGE_LOCAL, IMAGE_URL, WEB, MUSIC, VIDEO)
                ShareSdkHelper.PLATFORM_SMS -> arrayListOf(TEXT, TEXT_AND_IMAGE, IMAGE_LOCAL, IMAGE_URL)
                else -> arrayListOf()
            }
        }
    }
}