package com.ajt.simpletasks

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

@Database(entities = [Task::class, Sheet::class], version = 2)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun getTaskDao(): TaskDao

    abstract fun getSheetDao(): SheetDao

    companion object {
        private const val databaseName = "Tasks.db"
        private var instance: TaskDatabase? = null

        fun get(context: Context): TaskDatabase {
            if (instance == null) synchronized(TaskDatabase::class) {
                //Get's the Android/data/com.abcd.efgh folder path
                val appFolderPath = context.getExternalFilesDirs(null)!![0]
                val databasePath = File(appFolderPath, databaseName)
                //
                instance = Room.databaseBuilder(context, TaskDatabase::class.java, databasePath.absolutePath)
                    .setJournalMode(JournalMode.TRUNCATE)
                    .build()
            }
            return instance!!
        }

        fun evict() {
            instance = null
        }
    }

}