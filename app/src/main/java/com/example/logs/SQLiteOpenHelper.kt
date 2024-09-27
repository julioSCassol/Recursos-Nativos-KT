package com.example.logs

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val DATABASE_NAME = "formdata.db"
private const val DATABASE_VERSION = 1

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "form_data.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "FormData"
        private const val COLUMN_NAME = "Nome"
        private const val COLUMN_EMAIL = "Email"
        private const val COLUMN_COMENTARIO = "Comentario"
        private const val COLUMN_IMAGEM_URI = "ImagemURI"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableStatement = ("CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_NAME TEXT, " +
                "$COLUMN_EMAIL TEXT, " +
                "$COLUMN_COMENTARIO TEXT, " +
                "$COLUMN_IMAGEM_URI TEXT)")
        db.execSQL(createTableStatement)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertData(nome: String, email: String, comentario: String, imageUri: String): Boolean {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_NAME, nome)
            put(COLUMN_EMAIL, email)
            put(COLUMN_COMENTARIO, comentario)
            put(COLUMN_IMAGEM_URI, imageUri)
        }

        val result = db.insert(TABLE_NAME, null, contentValues)
        db.close()
        return result != -1L
    }
}