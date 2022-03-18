package com.medlinker.lib.utils

import android.content.Context

class MedSpHelper private constructor() {

    companion object{
        private val mSp by lazy {
            MedAppInfo.appContext.getSharedPreferences("med_common_data_store.db", Context.MODE_PRIVATE)
        }

        @JvmStatic
        fun putString(key: String, value:String?) {
            mSp.edit().putString(key, value)
                    .apply()
        }

        @JvmStatic
        fun getString(key: String, default: String?): String? {
            return mSp.getString(key, default)
        }

        @JvmStatic
        fun putInt(key: String, value:Int) {
            mSp.edit().putInt(key, value)
                    .apply()
        }

        @JvmStatic
        fun getInt(key: String, default: Int): Int {
            return mSp.getInt(key, default)
        }

        @JvmStatic
        fun putLong(key: String, value:Long) {
            mSp.edit().putLong(key, value)
                    .apply()
        }

        @JvmStatic
        fun getLong(key: String, default: Long): Long {
            return mSp.getLong(key, default)
        }

        @JvmStatic
        fun putFloat(key: String, value:Float) {
            mSp.edit().putFloat(key, value)
                    .apply()
        }

        @JvmStatic
        fun getFloat(key: String, default: Float): Float {
            return mSp.getFloat(key, default)
        }

        @JvmStatic
        fun putBoolean(key: String, value:Boolean) {
            mSp.edit().putBoolean(key, value)
                    .apply()
        }

        @JvmStatic
        fun getBoolean(key: String, default: Boolean): Boolean {
            return mSp.getBoolean(key, default)
        }

        @JvmStatic
        fun contains(key: String?): Boolean {
            return mSp.contains(key)
        }

        @JvmStatic
        fun remove(key: String?) {
            mSp.edit().remove(key).apply()
        }

        @JvmStatic
        fun removeSync(key: String?) {
            mSp.edit().remove(key).commit()
        }

        @JvmStatic
        fun clear() {
            mSp.edit().clear().apply()
        }
    }

}