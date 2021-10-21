package com.ajt.simpletasks

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SheetDao {

    @Query("SELECT * FROM Sheets")
    fun getSheets() : MutableList<Sheet>

    @Query("SELECT COUNT() FROM Tasks WHERE Tasks.sheet = :sheet")
    fun getTotalTasksInSheet(sheet: String) : Int

    @Insert(entity = Sheet::class, onConflict = OnConflictStrategy.IGNORE)
    fun addSheet(sheet: Sheet)

    @Query("DELETE FROM Sheets WHERE Sheets.name = :sheet")
    fun deleteSheet(sheet: String)
}