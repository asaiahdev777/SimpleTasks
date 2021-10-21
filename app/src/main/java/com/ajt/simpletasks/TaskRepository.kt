package com.ajt.simpletasks

object TaskRepository {

    private lateinit var taskDatabase: TaskDatabase

    fun init(Database: TaskDatabase) {
        taskDatabase = Database
    }

    fun getSheets() = taskDatabase.getSheetDao().getSheets()

    fun getTotalTasksInSheet(sheet: Sheet) = taskDatabase.getSheetDao().getTotalTasksInSheet(sheet.name)

    fun addSheet(sheet: Sheet) = taskDatabase.getSheetDao().addSheet(sheet)

    fun deleteSheet(sheet: Sheet) {
        taskDatabase.getSheetDao().deleteSheet(sheet.name)
        taskDatabase.getTaskDao().deleteSheet(sheet.name)
    }

    fun getTasks(sheet: Sheet) = taskDatabase.getTaskDao().getTasks(sheet.name)

    fun addTask(task: Task) = taskDatabase.getTaskDao().addTask(task)

    fun updateTask(task: Task) = taskDatabase.getTaskDao().updateTask(task)

    fun deleteTask(task: Task) = taskDatabase.getTaskDao().deleteTask(task)

    fun closeDown() {
        taskDatabase.close()
        TaskDatabase.evict()
    }
}