package com.medlinker.filedownloader.db

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.annotation.Keep
import com.blankj.utilcode.util.Utils
import com.medlinker.filedownloader.db.DBUtil.closeDB
import com.medlinker.filedownloader.db.DBUtil.getLong
import com.medlinker.filedownloader.db.DBUtil.getString
import com.medlinker.filedownloader.entity.MedFileEntity
import java.util.*

/**
 *
 * @author zhangquan
 */
@Keep
object ModelDao {
    private const val whereClause =
        ModelColumn.url + "=? and " + ModelColumn.parentFile + "=? and " + ModelColumn.fileName + "=?"

    @Synchronized
    private fun getDB(): SQLiteDatabase {
        return ModelDBHelper(Utils.getApp()).writableDatabase
    }

    fun findAll(): List<MedFileEntity> {
        var db: SQLiteDatabase? = null
        var c: Cursor? = null
        val fileEntitys = ArrayList<MedFileEntity>()
        try {
            db = getDB();
            c = db.query(
                ModelDBHelper.TABLE,
                null,
                null,
                null, null, null, null
            )
            while (c.moveToNext()) {
                val entity = MedFileEntity()
                entity.url = getString(c, ModelColumn.url)
                entity.parentFile = getString(c, ModelColumn.parentFile)
                entity.fileName = getString(c, ModelColumn.fileName)
                entity.total = getLong(c, ModelColumn.total)
                fileEntitys.add(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            closeDB(db, c)
        }
        return fileEntitys
    }

    fun find(url: String?, parentFile: String?, fileName: String?): List<MedFileEntity> {
        var db: SQLiteDatabase? = null
        var c: Cursor? = null
        val fileEntitys = ArrayList<MedFileEntity>()
        try {
            db = getDB()
            c = db.query(
                ModelDBHelper.TABLE,
                null,
                whereClause, arrayOf(url, parentFile, fileName), null, null, null
            )
            while (c.moveToNext()) {
                val entity = MedFileEntity()
                entity.url = getString(c, ModelColumn.url)
                entity.parentFile = getString(c, ModelColumn.parentFile)
                entity.fileName = getString(c, ModelColumn.fileName)
                entity.total = getLong(c, ModelColumn.total)
                fileEntitys.add(entity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            closeDB(db, c)
        }
        return fileEntitys
    }

    @Synchronized
    fun save(entity: MedFileEntity?): Boolean {
        if (null == entity) {
            return false
        }
        var result = true
        var db: SQLiteDatabase? = null
        try {
            db = getDB()
            db.beginTransaction()
            db.delete(
                ModelDBHelper.TABLE,
                whereClause,
                arrayOf(entity.url, entity.parentFile, entity.fileName)
            )
            db.insert(ModelDBHelper.TABLE, null, getValue(entity))
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            e.printStackTrace()
            result = false
        } finally {
            db?.endTransaction()
            closeDB(db, null)
        }
        return result
    }

    @Synchronized
    fun delete(entity: MedFileEntity?): Boolean {
        if (null == entity) {
            return false
        }
        val dataList = ArrayList<MedFileEntity>()
        dataList.add(entity)
        return delete(dataList)
    }

    @Synchronized
    fun delete(entityList: List<MedFileEntity>?): Boolean {
        if (null == entityList || entityList.isEmpty()) {
            return false
        }
        var result = true
        var db: SQLiteDatabase? = null
        try {
            db = getDB()
            val size = entityList.size
            db.beginTransaction()
            for (i in 0 until size) {
                val entity = entityList[i]
                db.delete(
                    ModelDBHelper.TABLE,
                    whereClause,
                    arrayOf(entity.url, entity.parentFile, entity.fileName)
                )
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            e.printStackTrace()
            result = false
        } finally {
            db?.endTransaction()
            closeDB(db, null)
        }
        return result
    }

    @Synchronized
    private fun getValue(model: MedFileEntity): ContentValues {
        val values = ContentValues()
        values.put(ModelColumn.url, model.url)
        values.put(ModelColumn.parentFile, model.parentFile)
        values.put(ModelColumn.fileName, model.fileName)
        values.put(ModelColumn.total, model.total)
        return values
    }
}