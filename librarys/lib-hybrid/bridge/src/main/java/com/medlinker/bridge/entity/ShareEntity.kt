package com.medlinker.bridge.entity


/**
 * @author hmy
 * @time 4/15/21 16:27
 */
data class ShareEntity(
        /**
         * 分享平台:['wechat','timeline','qq','weibo','medlinker']
         */
        val platforms: List<String>?,
        /**
         * 分享标题
         */
        val title: String?,
        /**
         * 分享描述
         */
        val desc: String?,
        /**
         * 分享图标
         */
        val image: String?,
        /**
         * 分享链接
         */
        val link: String?,
        /**
         * 内部分享链接
         */
        val innerLink: String?,
        /**
         * 显示复制按钮
         */
        val showCopy: Boolean = false)