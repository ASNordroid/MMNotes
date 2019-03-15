package com.searchingfox.mmnotes

//import java.io.File
import java.util.Date

class Note {
    var id: Int = 0 // ID - String, Int, UUID ???
    var name: String? = null
    var content: String? = null
    var lastchanged: Long? = null // maybe int
    private var isSelected: Boolean? = false
    //val path: File? = null

    constructor(id:Int, name: String, content: String, lastchanged: Long) {
        this.id = id
        this.name = name
        this.content = content
        this.lastchanged = lastchanged
    }

    constructor(name: String, content: String) {
        this.name = name
        this.content = content
        this.lastchanged = Date().time
    }

    fun setSelected(selected: Boolean) {
        isSelected = selected
    }


    fun isSelected(): Boolean {
        return isSelected!!
    }
}