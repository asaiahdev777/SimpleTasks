package com.ajt.simpletasks

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

//Class used to hold data
@Entity(tableName = "Tasks")
data class Task(
    @PrimaryKey var id: Long = 1,
    var entry: String,
    var sheet: String,
    var category: String
) {

   @Ignore var inFocus = false

    fun copyAttributes(taskToClone: Task) {
        id = taskToClone.id
        entry = taskToClone.entry
        category = taskToClone.category
        sheet = taskToClone.sheet
    }
}