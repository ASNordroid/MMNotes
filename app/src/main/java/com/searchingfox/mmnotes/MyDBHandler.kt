package com.searchingfox.mmnotes

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import android.content.ContentValues
//import java.nio.file.Files.exists
//import android.os.Environment.getExternalStorageDirectory
//import java.io.File
//import java.io.FileWriter


class MyDBHandler(context: Context, factory: SQLiteDatabase.CursorFactory?):
//, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) :
        SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_PRODUCTS_TABLE = "CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, " +
                "$COLUMN_NAME TEXT, $COLUMN_CONTENT INTEGER, $COLUMN_LASTCHANGED INTEGER)"
        db.execSQL(CREATE_PRODUCTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "notes.db"
        const val TABLE_NAME = "notes"

        const val COLUMN_ID = "_id"
        const val COLUMN_NAME = "name"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_LASTCHANGED = "lastchanged"
    }

    fun getAllNotes(): ArrayList<Note> {
        val notes: ArrayList<Note> = ArrayList()
        val query = "SELECT * FROM $TABLE_NAME"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        if (cursor!!.moveToFirst()) {
            do {
                val id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ID)))
                val name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                val content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT))
                val lastchanged = cursor.getString(cursor.getColumnIndex(COLUMN_LASTCHANGED)).toLong()
                notes.add(Note(id, name, content, lastchanged))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return notes
    }

    fun addNote(note: Note) {
        val values = ContentValues()
        val db = this.writableDatabase

        values.put(COLUMN_NAME, note.name)
        values.put(COLUMN_CONTENT, note.content)
        values.put(COLUMN_LASTCHANGED, note.lastchanged)

        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun updateNote(note: Note): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COLUMN_NAME, note.name)
        values.put(COLUMN_CONTENT, note.content)
        values.put(COLUMN_LASTCHANGED, note.lastchanged)
        val success = db.update(TABLE_NAME, values, "$COLUMN_ID=?", arrayOf(note.id.toString())).toLong()

        db.close()
        return Integer.parseInt("$success") != -1
    }

    fun getNote(noteId: Int): Note? {
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = ($noteId)"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        var note: Note? = null
        if (cursor.moveToFirst()) {
            cursor.moveToFirst()

            val id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ID)))
            val name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
            val content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT))
            val lastchanged = cursor.getString(cursor.getColumnIndex(COLUMN_LASTCHANGED)).toLong()
            note = Note(id, name, content, lastchanged)
            cursor.close()
        }

        db.close()
        return note
    }

    fun findNotes(str: String): ArrayList<Note>? {
        val notes: ArrayList<Note> = ArrayList()
        val query = "SELECT * FROM $TABLE_NAME WHERE instr(\"name\", \"$str\") OR instr(\"content\", \"$str\")"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        if (cursor!!.moveToFirst()) {
            do {
                val id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ID)))
                val name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                val content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT))
                val lastchanged = cursor.getString(cursor.getColumnIndex(COLUMN_LASTCHANGED)).toLong()
                notes.add(Note(id, name, content, lastchanged))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()

        return notes
    }

    fun deleteNote(noteId: Int): Boolean {
        var result = false
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = ($noteId)"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            //val id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COLUMN_ID)))
            db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(noteId.toString()))
            cursor.close()
            result = true
        }

        db.close()
        return result
    }

//    private fun exportDB() {
//        val dbhelper = MyDBHandler(getApplicationContext())
//        val exportDir = File(Environment.getExternalStorageDirectory(), "")
//        if (!exportDir.exists()) {
//            exportDir.mkdirs()
//        }
//
//        val file = File(exportDir, "csvname.csv")
//        try {
//            file.createNewFile()
//            val csvWrite = CSVWriter(FileWriter(file))
//            val db = dbhelper.getReadableDatabase()
//            val curCSV = db.rawQuery("SELECT * FROM notes", null)
//            csvWrite.writeNext(curCSV.getColumnNames())
//            while (curCSV.moveToNext()) {
//                //Which column you want to exprort
//                val arrStr = arrayOf<String>(curCSV.getString(0), curCSV.getString(1), curCSV.getString(2))
//                csvWrite.writeNext(arrStr)
//            }
//            csvWrite.close()
//            curCSV.close()
//        } catch (sqlEx: Exception) {
//            //Log.e("MainActivity", sqlEx.message, sqlEx)
//        }
//
//    }
}