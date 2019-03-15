package com.searchingfox.mmnotes

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.support.v7.view.ActionMode
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), MainInterface {
    var actionMode: ActionMode? = null
    var myAdapter: MyAdapter? = null
    companion object {
        var isMultiSelectOn = false
        val TAG = "MainActivityLog"
    }

    override fun mainInterface(size: Int) {
        if (actionMode == null) actionMode = startSupportActionMode(ActionModeCallback())
        if (size > 0) actionMode?.title = "$size"
        else actionMode?.finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        isMultiSelectOn = false
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            startActivity(Intent(this@MainActivity,
                    NoteEditor::class.java))
        }
    }

    override fun onResume() {
        rv_notes_list.layoutManager = LinearLayoutManager(this)
        myAdapter = MyAdapter(updateData(), this, this)
        rv_notes_list.adapter = myAdapter
        myAdapter?.notifyDataSetChanged()
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                val db = MyDBHandler(this@MainActivity, null)
                rv_notes_list.layoutManager = LinearLayoutManager(this@MainActivity)
                rv_notes_list.adapter = MyAdapter(db.findNotes(query)!!, this@MainActivity, this@MainActivity)
                return false
            }
        })
        menu.findItem(R.id.action_search).setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                val db = MyDBHandler(this@MainActivity, null)
                rv_notes_list.layoutManager = LinearLayoutManager(this@MainActivity)
                rv_notes_list.adapter = MyAdapter(db.getAllNotes(),this@MainActivity, this@MainActivity)
                return true
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        return when (id) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    inner class ActionModeCallback : ActionMode.Callback {
        private var shouldResetRecyclerView = true

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when (item?.itemId) {
                R.id.action_delete -> {
                    shouldResetRecyclerView = false
                    myAdapter?.deleteSelectedIds()
                    actionMode?.title = "" //remove item count from action mode.
                    actionMode?.finish()
                    return true
                }
                R.id.action_concat -> {
                    shouldResetRecyclerView = true
                    myAdapter?.concatSelectedIds()
                    myAdapter?.deleteSelectedIds()
                    actionMode?.title = "" //remove item count from action mode.
                    actionMode?.finish()
                    return true
                }
            }
            return false
        }

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            val inflater = mode?.menuInflater
            inflater?.inflate(R.menu.action_mode_menu, menu)
            val fab = findViewById<FloatingActionButton>(R.id.fab)
            fab.hide()
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            if (shouldResetRecyclerView) {
                myAdapter?.selectedIds?.clear()
                myAdapter?.notifyDataSetChanged()
            }
            val fab = findViewById<FloatingActionButton>(R.id.fab)
            fab.show()
            isMultiSelectOn = false
            actionMode = null
            shouldResetRecyclerView = true
        }
    }

    private fun updateData(): ArrayList<Note> {
        val dbHandler = MyDBHandler(this, null)
        return dbHandler.getAllNotes()
    }
}