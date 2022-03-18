package com.medlinker.lib.log

import com.qpdstudio.logger.Logger
import com.qpdstudio.logger.Settings

class LogUtil {

    companion object {
        private var mSetting = Setting()
        private var mInited = false

        private fun init(): Boolean {
            if (!mInited) {
                Logger.init(Settings().setShowMethodLink(false).setShowThreadInfo(false).setShowLog(mSetting.isDebug()))
            }

            mInited = true
            return mInited
        }

        private fun getTag(tag: String?): String {
            if (null == tag) {
                return mSetting.getTag()
            }

            return tag
        }

        @JvmStatic
        fun setSetting(cfg: Setting) {
            mSetting = cfg
        }


        @JvmStatic
        fun v(tag: String?, msg: String?) {
            if (mSetting.isDebug() && init()) {
                Logger.tag(getTag(tag)).v(msg)
            }
        }

        @JvmStatic
        fun v(tag: String?, msg: String?, vararg args: Any?) {
            if (mSetting.isDebug() && init()) {
                Logger.tag(getTag(tag)).v(msg, *args)
            }
        }

        @JvmStatic
        fun v( msg: String?) {
            v(null, msg)
        }

        @JvmStatic
        fun d(tag: String? = null, msg: String?) {
            if (mSetting.isDebug() && init()) {
                Logger.tag(getTag(tag)).d(msg)
            }
        }

        @JvmStatic
        fun d(tag: String?, msg: String?, vararg args: Any?) {
            if (mSetting.isDebug() && init()) {
                Logger.tag(getTag(tag)).d(msg, *args)
            }
        }

        @JvmStatic
        fun d( msg: String?) {
            d(null, msg)
        }

        @JvmStatic
        fun i(tag: String? = null, msg: String?) {
            if (mSetting.isDebug() && init()) {
                Logger.tag(getTag(tag)).i(msg)
            }
        }

        @JvmStatic
        fun i(tag: String?, msg: String?, vararg args: Any?) {
            if (mSetting.isDebug() && init()) {
                Logger.tag(getTag(tag)).i(msg, *args)
            }
        }

        @JvmStatic
        fun i( msg: String?) {
            i(null, msg)
        }

        @JvmStatic
        fun e(tag: String?, msg: String?) {
            init()
            Logger.tag(getTag(tag)).e(msg)
        }

        @JvmStatic
        fun e(tag: String?, msg: String?, vararg args: Any?) {
            init()
            Logger.tag(getTag(tag)).e(msg, *args)
        }

        @JvmStatic
        fun e(msg: String?) {
            e(null, msg)
        }


        @JvmStatic
        fun w(tag: String? = null, msg: String?) {
            if (mSetting.isDebug() && init()) {
                Logger.tag(getTag(tag)).w(msg)
            }
        }

        @JvmStatic
        fun w(tag: String?, msg: String?, vararg args: Any?) {
            if (mSetting.isDebug() && init()) {
                Logger.tag(getTag(tag)).w(msg, *args)
            }
        }

        @JvmStatic
        fun w(msg: String?) {
            w(null, msg)
        }
    }

    class Setting {
        private var mIsDebug = false
        private var mDefaultTag = ""

        fun setDebugMode(debug: Boolean): Setting {
            mIsDebug = debug
            return this
        }

        fun setDefaultTag(tag: String): Setting {
            mDefaultTag = tag
            return this
        }

        fun isDebug() = mIsDebug

        fun getTag() = mDefaultTag
    }
}