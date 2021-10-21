package com.ajt.simpletasks

import androidx.recyclerview.widget.DiffUtil

class TaskDiffUtilCallback(private val oldTasks : MutableList<Task>, private val newTasks : MutableList<Task>) : DiffUtil.Callback() {

    override fun getOldListSize() = oldTasks.size

    override fun getNewListSize() = newTasks.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldTasks[oldItemPosition] == newTasks[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = areItemsTheSame(oldItemPosition, newItemPosition)

}