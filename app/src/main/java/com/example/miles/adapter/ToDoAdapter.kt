package com.example.miles.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.miles.R
import com.example.miles.model.ToDoItem

class ToDoAdapter(private var todoList: List<ToDoItem>) :
    RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder>() {

    class ToDoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskText: TextView = itemView.findViewById(R.id.taskText)
        val sharedWithText: TextView = itemView.findViewById(R.id.sharedWithText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false)
        return ToDoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        val task = todoList[position]
        holder.taskText.text = task.task
        holder.sharedWithText.text = "Shared with: ${task.sharedWith.joinToString(", ")}"
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    fun updateList(newList: List<ToDoItem>) {
        todoList = newList
        notifyDataSetChanged()
    }
}
