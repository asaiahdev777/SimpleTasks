package com.ajt.simpletasks

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


//LayoutInflater used to create Views from XML files
class TaskAdapter(private val layoutInflater: LayoutInflater, private val taskViewModel: TaskViewModel) : RecyclerView.Adapter<TaskRowViewHolder>() {

    private val tasks get() = taskViewModel.tasksLiveData.value ?: mutableListOf()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val callback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.END) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

            override fun isLongPressDragEnabled() = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (direction == ItemTouchHelper.END) showDeleteDialog(tasks[viewHolder.absoluteAdapterPosition], viewHolder as TaskRowViewHolder)
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
    }

    //Called when a row is being created
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TaskRowViewHolder(layoutInflater.inflate(R.layout.task_item_row_layout, parent, false))

    //Called when a row is shown for a first time
    override fun onBindViewHolder(holder: TaskRowViewHolder, position: Int) {
        val task = tasks[position]
        with(holder) {
            itemView.apply {
                background = ColorDrawable(0)
                setOnLongClickListener {
                    background = ColorDrawable(Color.RED)
                    showDeleteDialog(task, holder)
                    false
                }
            }
            with(task) {
                entryTextView.loadText(entry)
                categoryTextView.loadText(category)
                entryTextView.addBatchEdit { entry = it; taskViewModel.updateTask(this) }
                categoryTextView.addBatchEdit { category = it; taskViewModel.updateTask(this) }
                if (inFocus) entryTextView.forceShowKB()
                inFocus = false
            }

            //movementMethod = null

        }
    }

    //Returns total number of rows
    override fun getItemCount() = tasks.size

    private fun EditText.addBatchEdit(onChange: (String) -> Unit) {

        fun removeTextWatcher() = if (tag is TextWatcher) removeTextChangedListener(tag as TextWatcher) else Unit

        removeTextWatcher()

        inputType = EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE
        setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                removeTextWatcher()
                taskViewModel.getTasks()
            }
            else tag = doAfterTextChanged { onChange(it?.toString() ?: "") }
        }
    }


    /*

    private fun EditText.addBatchEdit(onChange: (String) -> Unit) {

        fun removeEdit() {
            //inputType = EditorInfo.TYPE_NULL
        }

        fun removeTextWatcher() = if (tag is TextWatcher) {
            removeTextChangedListener(tag as TextWatcher)
            removeEdit()
        } else Unit

        removeTextWatcher()

        val focusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                removeTextWatcher()
                onFocusChangeListener = null
                taskViewModel.getTasks()
            } else tag = doAfterTextChanged { onChange(it?.toString() ?: "") }
        }
        onFocusChangeListener = focusChangeListener

        inputType = EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE

        setOnClickListener {
            showKB()
            doOnDetach {
                removeEdit()
                onFocusChangeListener = null
            }
        }
        removeEdit()
    }
     */

    private fun showDeleteDialog(task: Task, holder: TaskRowViewHolder) {
        AlertDialog.Builder(holder.itemView.context).apply {
            setCancelable(false)
            setTitle(R.string.deleteTaskPrompt)
            setPositiveButton(R.string.delete) { _, _ -> taskViewModel.deleteTask(task) }
            setNegativeButton(android.R.string.cancel) { _, _ -> notifyItemChanged(holder.absoluteAdapterPosition) }
        }.show()
    }

    fun update(oldTasks: MutableList<Task>, newTasks: MutableList<Task>) {
        taskViewModel.calculateDiff(this, oldTasks, newTasks)
    }

}