package com.ajt.simpletasks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    val forceShowNewSheetLiveData = MutableLiveData<Unit>()
    val availableSheets by lazy {
        MutableLiveData<MutableList<Sheet>>().also {
            viewModelScope.launch(Dispatchers.IO) {
                val sheets = TaskRepository.getSheets()
                sheets.forEach { it.totalTasks = TaskRepository.getTotalTasksInSheet(it) }
                sortSheets(sheets)
                if (sheets.isNotEmpty()) {
                    currentSheetLiveData.postValue(sheets[0])
                    it.postValue(sheets)
                } else forceShowNewSheetLiveData.postValue(Unit)
            }
        }
    }
    val currentSheetLiveData = MutableLiveData<Sheet>()
    val tasksLiveData by lazy { MutableLiveData<MutableList<Task>>().also { getTasks() } }

    val currentSheetName get() = currentSheetLiveData.value?.name ?: getApplication<CustomApp>().getString(R.string.defaultString)

    fun addSheet(name: String) {
        if (availableSheets.value?.any { it.name == name } != true)
            viewModelScope.launch(Dispatchers.IO) {
                val sheet = Sheet(id = Date().time, name = name).apply { totalTasks = 0 }
                TaskRepository.addSheet(sheet)

                val sheets = availableSheets.value ?: mutableListOf()
                sheets.add(sheet)
                sortSheets(sheets)
                availableSheets.postValue(sheets)
                currentSheetLiveData.postValue(sheet)
            }
    }

    fun deleteSheet(sheet: Sheet) {
        viewModelScope.launch(Dispatchers.IO) {
            TaskRepository.deleteSheet(sheet)
            val sheets = availableSheets.value ?: mutableListOf()
            sheets.removeAll { it.name == sheet.name }
            sortSheets(sheets)

            if (sheets.isEmpty()) {
                tasksLiveData.postValue(mutableListOf())
                forceShowNewSheetLiveData.postValue(Unit)
            } else currentSheetLiveData.postValue(sheets.last())
            availableSheets.postValue(sheets)
        }
    }

    fun addTask() {
        viewModelScope.launch(Dispatchers.IO) {
            val task = Task(id = Date().time, entry = "", sheet = currentSheetLiveData.value?.name ?: getApplication<CustomApp>().getString(R.string.defaultString), category = "")
            task.inFocus = true
            TaskRepository.addTask(task)

            val sheet = availableSheets.value?.find { it.name == task.sheet }
            if (sheet != null) sheet.totalTasks++
            availableSheets.postValue(availableSheets.value)

            tasksLiveData.value?.let { tasks ->
                tasks.add(task)
                sortTasks(tasks)
                tasksLiveData.postValue(tasks)
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            TaskRepository.updateTask(task)
            tasksLiveData.value?.let { tasks ->
                tasks.find { it.id == task.id }?.copyAttributes(task)/*
                sortTasks(tasks)
                tasksLiveData.postValue(tasks)*/
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            TaskRepository.deleteTask(task)

            val sheet = availableSheets.value?.find { it.name == task.sheet }
            if (sheet != null) sheet.totalTasks--
            availableSheets.postValue(availableSheets.value)

            tasksLiveData.value?.let { tasks ->
                tasks.removeAll { it.id == task.id }
                sortTasks(tasks)
                tasksLiveData.postValue(tasks)
            }
        }
    }

    fun getTasks() {
        val currentSheet = currentSheetLiveData.value
        if (currentSheet != null)
            viewModelScope.launch(Dispatchers.IO) {
                tasksLiveData.postValue(sortTasks(TaskRepository.getTasks(currentSheet)))
            }
    }

    private fun sortSheets(sheets: MutableList<Sheet>) {
        sheets.sortBy { it.name.toLowerCase() }
    }

    fun calculateDiff(taskAdapter: TaskAdapter, oldTasks: MutableList<Task>, newTasks: MutableList<Task>) {
        viewModelScope.launch(Dispatchers.IO) {
            val diffResult = DiffUtil.calculateDiff(TaskDiffUtilCallback(oldTasks, newTasks))
            launch(Dispatchers.Main) { diffResult.dispatchUpdatesTo(taskAdapter) }
        }
    }

    private fun sortTasks(tasks: MutableList<Task>, sort: Boolean? = true): MutableList<Task> {
        with(tasks) {
            when (sort) {
                null -> sortBy { it.id }
                true -> sortBy { it.category.toLowerCase(Locale.ROOT) }
                false -> sortByDescending { it.category.toLowerCase(Locale.ROOT) }
            }
        }
        return tasks
    }

    override fun onCleared() {
        super.onCleared()
        TaskRepository.closeDown()
    }
}