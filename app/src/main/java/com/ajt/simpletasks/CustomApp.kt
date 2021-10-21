package com.ajt.simpletasks

import android.app.Application

//Used to extend the base context and run code when the application (not activities, but the process itself) starts
class CustomApp : Application() {

    override fun onCreate() {
        super.onCreate()
        TaskRepository.init(TaskDatabase.get(this))
    }
}