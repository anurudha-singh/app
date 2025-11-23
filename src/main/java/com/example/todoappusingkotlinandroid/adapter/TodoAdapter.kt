package com.example.todoappusingkotlinandroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todoappusingkotlinandroid.R
import com.example.todoappusingkotlinandroid.data.Todo

class TodoAdapter(
    // Function called when user clicks on a todo item
    private val onItemClick: (Todo) -> Unit = {},
    // Function called when user clicks the delete button
    private val onDeleteClick: (Todo) -> Unit = {},
    // Function called when user clicks the edit button
    private val onEditClick: (Todo) -> Unit = {}
) : ListAdapter<Todo, TodoAdapter.TodoViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<Todo>() {
            override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean =
                oldItem == newItem
        }
    }

    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Find and store references to views from item_todo.xml
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        // NEW: Find the delete button from the layout
        private val deleteButton: android.widget.Button = itemView.findViewById(R.id.deleteButton)
        private val editButton: android.widget.Button = itemView.findViewById(R.id.editButton)

        fun bind(todo: Todo) {
            // Bind the todo data to the TextViews for display
            tvTitle.text = todo.title
            tvDescription.text = todo.description

            // Set click listener for the entire item (for viewing/editing)
            itemView.setOnClickListener { onItemClick(todo) }
            
            // FIXED: Set click listener for the delete button
            // This is the correct place to handle delete button clicks
            deleteButton.setOnClickListener { 
                // Call the delete function passed from the activity
                onDeleteClick(todo) 
            }
            editButton.setOnClickListener {
                onEditClick(todo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        // FIXED: Using the correct layout file for RecyclerView items
        // item_todo.xml contains TextViews for displaying data (not EditTexts for input)
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(v)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
