package com.searchingfox.mmnotes

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
//import android.util.Log
import android.widget.EditText

//import java.io.File
//import android.os.Environment.getExternalStorageDirectory
//import android.text.Editable
//import android.text.TextWatcher
import java.util.Date
import java.util.UUID

// TODO: save periodically: after every letter? every n minutes? n seconds? look at OmniNotes

class NoteEditor : AppCompatActivity() {
    private var editTitle: EditText? = null
    private var editBody: EditText? = null
    private var sentNoteId:Int? = null

    val TAG = "NoteEditor_log"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.note_editor)
        editBody = findViewById(R.id.editNoteBody)
        editTitle = findViewById(R.id.editNoteHeader)

        val toolbar = findViewById<Toolbar>(R.id.note_toolbar)
        setSupportActionBar(toolbar)

        val intent = intent
        if (Intent.ACTION_SEND == intent.action && "text/plain" == intent.type) {
            val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (sharedText != null) { // Do I need this check?
                editBody?.append(sharedText)
            }
        } else if (intent.getBooleanExtra("EDITNOTE", false)) {
            sentNoteId = intent.getIntExtra("NOTEID", 0)
            val note = MyDBHandler(this, null).getNote(sentNoteId!!)
            editTitle?.append(note?.name)
            editBody?.append(note?.content)
        }

//        editBody?.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
//
//            }
//
//            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//
//                if (your_string.equals(s.toString())) {
//                    //do something
//                } else {
//                    //do something
//                }
//            }
//
//            override fun afterTextChanged(s: Editable) {
//
//            }
//        })
    }

    override fun onBackPressed() {
        val noteContent = editBody?.text.toString()
        var noteTitle = editTitle?.text.toString()
        if (noteContent != "") { // TODO: Should I compare strings or something else
            val dbHandler = MyDBHandler(this, null)
            if (sentNoteId != null) {
                val changedNote = dbHandler.getNote(sentNoteId!!)
                changedNote?.name = noteTitle
                changedNote?.content = noteContent
                changedNote?.lastchanged = Date().time
                dbHandler.updateNote(changedNote!!) // TODO: this returns boolean
            } else {
                if (noteTitle == "") {
                    noteTitle = noteContent.take(28) // TODO: manage same names
                }
                dbHandler.addNote(Note(noteTitle, noteContent))
            }
        }

        finish()
    }
}
