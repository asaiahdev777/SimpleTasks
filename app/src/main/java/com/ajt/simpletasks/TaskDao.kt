package com.ajt.simpletasks

import androidx.room.*

//A DAO is a database access object.
//Used to perform SQL queries on a database
@Dao
interface TaskDao {

    @Query("SELECT * FROM Tasks WHERE Tasks.sheet = :sheet")
    fun getTasks(sheet: String): MutableList<Task>

    @Insert(entity = Task::class, onConflict = OnConflictStrategy.REPLACE)
    fun addTask(task: Task)

    @Update(entity = Task::class, onConflict = OnConflictStrategy.REPLACE)
    fun updateTask(task: Task)

    @Delete(entity = Task::class)
    fun deleteTask(task: Task)

    @Query("DELETE FROM Tasks WHERE Tasks.sheet = :sheet")
    fun deleteSheet(sheet: String)
}