package com.medlinker.filedownloader.db

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.annotation.Keep

/**
 *
 * @author zhangquan
 */
@Keep
object DBUtil {
    @JvmStatic
    @Synchronized
    fun getInt(c: Cursor, column: String?): Int {
        return c.getInt(c.getColumnIndex(column))
    }

    @JvmStatic
    @Synchronized
    fun getLong(c: Cursor, column: String?): Long {
        return c.getLong(c.getColumnIndex(column))
    }

    @JvmStatic
    @Synchronized
    fun getString(c: Cursor, column: String?): String {
        return c.getString(c.getColumnIndex(column))
    }

    /**
     * 关闭数据库
     *
     * @param db
     * @param c
     */
    @JvmStatic
    @Synchronized
    fun closeDB(db: SQLiteDatabase?, c: Cursor?) {
        try {
            db?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            c?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    @Synchronized
    fun closeDB(db: SQLiteDatabase?) {
        try {
            db?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    @JvmStatic
    @Synchronized
    fun closeCursor(c: Cursor?) {
        try {
            c?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}