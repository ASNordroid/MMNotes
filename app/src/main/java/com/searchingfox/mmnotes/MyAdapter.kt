package com.searchingfox.mmnotes

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.android.synthetic.main.notes_list_item.view.*

class MyAdapter(items : ArrayList<Note>, private val context: Context,
                private val mainInterface: MainInterface) :
        RecyclerView.Adapter<MyViewHolder>(), ViewHolderClickListener {
    private val TAG = MyAdapter::class.java.name
    private var modelList: MutableList<Note> = items  //ArrayList<Note>()
    val selectedIds: MutableList<Int> = ArrayList()

    override fun onLongTap(index: Int) {
        if (!MainActivity.isMultiSelectOn) {
            MainActivity.isMultiSelectOn = true
        }
        addIDIntoSelectedIds(index)
    }

    override fun onTap(index: Int, view: View?) {
        if (MainActivity.isMultiSelectOn) {
            addIDIntoSelectedIds(index)
        } else {
            //Toast.makeText(context, "Clicked Item @ Position ${index + 1}", Toast.LENGTH_SHORT).show()
            val intent = Intent(view?.context, NoteEditor::class.java)
            intent.putExtra("NOTEID", modelList[index].id)
            intent.putExtra("EDITNOTE", true) // TODO: is this the right way?
            view?.context?.startActivity(intent)
        }
    }

    private fun addIDIntoSelectedIds(index: Int) {
        val id = modelList[index].id
        if (selectedIds.contains(id))
            selectedIds.remove(id)
        else
            selectedIds.add(id)
        Log.d(TAG, id.toString())
        notifyItemChanged(index)
        if (selectedIds.size < 1) MainActivity.isMultiSelectOn = false
        mainInterface.mainInterface(selectedIds.size)
    }

    override fun getItemCount() = modelList.size

    fun deleteSelectedIds() {
        if (selectedIds.size < 1) return
        val selectedIdIteration = selectedIds.listIterator()
        val dbHandler = MyDBHandler(context, null)
        while (selectedIdIteration.hasNext()) {
            val selectedItemID = selectedIdIteration.next()
            var indexOfModelList = 0
            val modelListIteration: MutableListIterator<Note> = modelList.listIterator()
            while (modelListIteration.hasNext()) {
                val model = modelListIteration.next()
                if (selectedItemID == model.id) {
                    dbHandler.deleteNote(model.id)
                    modelListIteration.remove()
                    selectedIdIteration.remove()
                    notifyItemRemoved(indexOfModelList)
                }
                indexOfModelList++
            }
            MainActivity.isMultiSelectOn = false
        }
    }

    fun concatSelectedIds() {
        if (selectedIds.size < 2) return

        val selectedIdIteration = selectedIds.listIterator()
        val dbHandler = MyDBHandler(context, null)
        var newName = ""
        // TODO: is this efficient?? change selectedIds to store indexes in RecycleView??
        for (i in modelList) {
            if (i.id == selectedIds[0]) {
                newName = i.name!!
            }
        }
        val newContent = ArrayList<String>()
        while (selectedIdIteration.hasNext()) {
            val selectedItemID = selectedIdIteration.next()
            var indexOfModelList = 0
            val modelListIteration: MutableListIterator<Note> = modelList.listIterator()
            while (modelListIteration.hasNext()) {
                val model = modelListIteration.next()
                if (selectedItemID == model.id) {
                    newContent.add(model.content!!)
                }
                indexOfModelList++
            }
        }
        dbHandler.addNote(Note(newName, newContent.joinToString("\n\n----------\n\n")))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).
                inflate(R.layout.notes_list_item, parent, false)
        return MyViewHolder(itemView, this)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: MyViewHolder, index: Int) {
        holder.tvNotesTitle.text = modelList[index].name
        val sdf = SimpleDateFormat("dd.MM.yy hh:mm")
        holder.tvNotesDate.text = sdf.format(Date(modelList[index].lastChanged!!))
        val id = modelList[index].id

        if (selectedIds.contains(id)) {
            // if item is selected set foreground color of FrameLayout
            holder.frameLayout.foreground = ColorDrawable(ContextCompat.getColor(context, R.color.colorControlActivated))
        } else {
            // else remove selected item color
            holder.frameLayout.foreground = ColorDrawable(ContextCompat.getColor(context, android.R.color.transparent))
        }
    }
}

class MyViewHolder(itemView: View, private val r_tap: ViewHolderClickListener) : RecyclerView.ViewHolder(itemView),
        View.OnLongClickListener, View.OnClickListener {
    val tvNotesTitle = itemView.tv_note_title!!
    val tvNotesDate = itemView.tv_note_date!!
    val frameLayout: FrameLayout = itemView.selectableItem

    init {
        frameLayout.setOnClickListener(this)
        frameLayout.setOnLongClickListener(this)
    }

    override fun onClick(v: View?) {
        r_tap.onTap(adapterPosition, v)
    }

    override fun onLongClick(v: View?): Boolean {
        r_tap.onLongTap(adapterPosition)
        return true
    }
}

interface MainInterface {
    fun mainInterface (size : Int)
}

interface ViewHolderClickListener {
    fun onLongTap(index : Int)
    fun onTap(index : Int, view: View?)
}