package com.searchingfox.mmnotes

import android.os.Bundle
import android.preference.PreferenceActivity
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        export_button.setOnClickListener {
            val dbHandler = MyDBHandler(this, null)
            dbHandler.exportAllNotes()
        }
    }
}
