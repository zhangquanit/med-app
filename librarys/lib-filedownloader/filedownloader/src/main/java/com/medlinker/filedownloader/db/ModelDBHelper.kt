package com.medlinker.filedownloader.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.annotation.Keep

/**
 * 文件下载任务数据库
 * @author zhangquan
 */
@Keep
class ModelDBHelper(ctx: Context?) : SQLiteOpenHelper(ctx, DB, null, VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val sql = StringBuffer()
        sql.append("CREATE TABLE IF NOT EXISTS [$TABLE] ( \n")
        sql.append(ModelColumn.url + " VARCHAR,\n")
        sql.append(ModelColumn.parentFile + " VARCHAR,\n")
        sql.append(ModelColumn.fileName + " VARCHAR,\n")
        sql.append(ModelColumn.total + " VARCHAR")
        sql.append(") \n")
        db.execSQL(sql.toString())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    companion object {
        const val DB = "med_filedownloader.db" //数据库名
        const val TABLE = "download" //表名
        private const val VERSION = 1 //数据库版本号
    }
}