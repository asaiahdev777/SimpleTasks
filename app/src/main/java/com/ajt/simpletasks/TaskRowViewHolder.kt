package com.ajt.simpletasks

import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView

//This class is a ViewHolder. Holds views for use in RecyclerView.Adapter
class TaskRowViewHolder(rowLayout : View) : RecyclerView.ViewHolder(rowLayout) {
    val entryTextView : EditText = itemView.findViewById(R.id.entry)
    val categoryTextView : EditText = itemView.findViewById(R.id.category)
}