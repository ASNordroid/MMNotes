package com.searchingfox.mmnotes

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import android.content.ContentValues
import android.os.Environment
import android.util.Log
import java.io.File

class MyDBHandler(context: Context, factory: SQLiteDatabase.CursorFactory?):
        SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {
    private val TAG = MyDBHandler::class.java.name

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $TABLE_NAME ($COLUMN_ID INTEGER PRIMARY KEY, " +
                "$COLUMN_NAME TEXT, $COLUMN_CONTENT INTEGER, $COLUMN_LASTCHANGED INTEGER)")
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
        val db = this.writableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        val notes: ArrayList<Note> = ArrayList()

        if (cursor!!.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                val content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT))
                val lastChanged = cursor.getLong(cursor.getColumnIndex(COLUMN_LASTCHANGED))
                notes.add(Note(id, name, content, lastChanged))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return notes
    }

    fun addNote(note: Note): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COLUMN_NAME, note.name)
        values.put(COLUMN_CONTENT, note.content)
        values.put(COLUMN_LASTCHANGED, note.lastChanged)
        val success = db.insert(TABLE_NAME, null, values)

        db.close()
        return success != -1L
    }

    fun updateNote(note: Note): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()

        values.put(COLUMN_NAME, note.name)
        values.put(COLUMN_CONTENT, note.content)
        values.put(COLUMN_LASTCHANGED, note.lastChanged)
        val success = db.update(TABLE_NAME, values, "$COLUMN_ID=?", arrayOf(note.id.toString()))

        db.close()
        return success != -1
    }

    fun getNote(noteId: Int): Note? {
        val db = this.writableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = ($noteId)", null)

        var note: Note? = null
        if (cursor!!.moveToFirst()) {
            //cursor.moveToFirst()
            val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
            val content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT))
            val lastChanged = cursor.getLong(cursor.getColumnIndex(COLUMN_LASTCHANGED))
            note = Note(id, name, content, lastChanged)
        }

        cursor.close()
        db.close()
        return note
    }

    fun findNotes(str: String): ArrayList<Note> {
        val notes: ArrayList<Note> = ArrayList()
        val db = this.writableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE " +
                "INSTR(LOWER(\"name\"), LOWER(\"$str\")) OR INSTR(LOWER(\"content\"), LOWER(\"$str\"))", null)

        if (cursor!!.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                val content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT))
                val lastChanged = cursor.getLong(cursor.getColumnIndex(COLUMN_LASTCHANGED))
                notes.add(Note(id, name, content, lastChanged))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return notes
    }

    fun deleteNote(noteId: Int): Boolean {
        val db = this.writableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = ($noteId)", null)
        var result = false

        if (cursor.moveToFirst()) {
            db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(noteId.toString()))
            cursor.close()
            result = true
        }

        db.close()
        return result
    }

    fun exportAllNotes() {
        val exportDir = File(Environment.getExternalStorageDirectory(), "MMNotes")
        if (!exportDir.exists()) {
            exportDir.mkdirs()
        }

        try {
            val file = File(exportDir, "export.txt") // TODO: add date
            val notesText = getAllNotes()
                    .joinToString { it.name + "\n" + it.content + "\n----------------------------\n" }
            file.writeText(notesText)
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
        }
    }
}