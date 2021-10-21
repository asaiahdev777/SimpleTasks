package com.ajt.simpletasks

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "Sheets")
data class Sheet(@PrimaryKey val id: Long, val name: String) {
    @Ignore var totalTasks = 0
}