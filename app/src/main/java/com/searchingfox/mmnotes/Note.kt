package com.searchingfox.mmnotes

import java.util.Date

class Note {
    var id: Int = 0 // ID - String, Int, UUID ???
    var name: String? = null
    var content: String? = null
    var lastChanged: Long? = null // maybe int
    private var isSelected: Boolean? = false

    //TODO: var lastsaved: Long? = null
    //val path: File? = null

    constructor(id:Int, name: String, content: String, lastChanged: Long) {
        this.id = id
        this.name = name
        this.content = content
        this.lastChanged = lastChanged
    }

    constructor(name: String, content: String) {
        this.name = name
        this.content = content
        this.lastChanged = Date().time
    }

    fun setSelected(selected: Boolean) {
        isSelected = selected
    }


    fun isSelected(): Boolean {
        return isSelected!!
    }
}