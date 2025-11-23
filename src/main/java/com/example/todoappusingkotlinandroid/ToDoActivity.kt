package com.example.todoappusingkotlinandroid

// FIXED: Proper import with full package name
import com.example.todoappusingkotlinandroid.adapter.TodoAdapter
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todoappusingkotlinandroid.databinding.ActivityMainBinding
import com.example.todoappusingkotlinandroid.databinding.ActivityToDoBinding
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todoappusingkotlinandroid.data.Todo

class ToDoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityToDoBinding
    private lateinit var adapter: TodoAdapter
    private val todos = mutableListOf<Todo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityToDoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Get reference to RecyclerView from the layout
        val rv = binding.rvTodos

        // FIXED: Initialize adapter with BOTH click handlers
        // onItemClick: when user taps the todo item
        // onDeleteClick: when user taps the delete button
        adapter = TodoAdapter(
            onItemClick = { todo ->
                // Item click: show details or edit functionality can be added here
                Toast.makeText(this, "Clicked: ${todo.title}", Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { todo ->
                // FIXED: Proper delete implementation
                // This will be called when delete button is clicked in the adapter
                deleteTodo(todo)
            },
            onEditClick={
                todo  ->
                editTodo(todo)
            }
        )

        // FIXED: Proper RecyclerView setup
        // Set LinearLayoutManager to display items in a vertical list
        rv.layoutManager = LinearLayoutManager(this)
        // Connect the adapter to the RecyclerView
        rv.adapter = adapter

        // FIXED: Submit initial empty list to adapter (moved after adapter initialization)
        adapter.submitList(todos.toList())

        // Navigation button to go back to MainActivity
        binding.button3.setOnClickListener {
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }


        // Floating Action Button for adding new todos
        binding.floatingActionButton.setOnClickListener {
            // Inflate the dialog layout for adding new todos
            val dialogView = layoutInflater.inflate(R.layout.dialog_add_note, null)
            val titleEt = dialogView.findViewById<EditText>(R.id.etTitle)
            val descEt = dialogView.findViewById<EditText>(R.id.etDescription)

            val dialog = AlertDialog.Builder(this)
                .setTitle("Add Todo")  // Changed from "Add Note" to "Add Todo"
                .setView(dialogView)
                .setPositiveButton("Add") { d, _ ->  // Changed button text to "Add"
                    val title = titleEt.text.toString().trim()
                    val desc = descEt.text.toString().trim()

                    // FIXED: Validate input and add todo to the list
                    if (title.isNotEmpty()) {
                        // Create new Todo with unique ID (using current time as simple ID)
                        val newTodo = Todo(
                            id = System.currentTimeMillis(),
                            title = title,
                            description = desc
                        )
                        
                        // Add to the mutable list
                        todos.add(newTodo)
                        
                        // IMPORTANT: Update the adapter with the new list
                        // This will trigger the RecyclerView to refresh and show the new item
                        adapter.submitList(todos.toList())
                        
                        Toast.makeText(this, "Todo added successfully!", Toast.LENGTH_SHORT).show()
                        d.dismiss()
                    } else {
                        // Show error if title is empty
                        Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel") { d, _ ->
                    d.dismiss()
                }
                .create()

            dialog.show()
        }
    }

    // FIXED: Proper delete function with ListAdapter
    private fun deleteTodo(todo: Todo) {
        // Remove from the mutable list
        todos.remove(todo)
        
        // IMPORTANT: Use submitList() with ListAdapter, not notifyDataSetChanged()
        // Create a new list copy to trigger DiffUtil comparison
        adapter.submitList(todos.toList())
        
        // Show confirmation to user
        Toast.makeText(this, "Todo '${todo.title}' deleted", Toast.LENGTH_SHORT).show()
    }

    // FIXED: Proper edit function that UPDATES existing todo instead of creating new one
    private fun editTodo(todo: Todo) {
        // Inflate the same dialog layout used for adding todos
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_note, null)
        val titleEt = dialogView.findViewById<EditText>(R.id.etTitle)
        val descEt = dialogView.findViewById<EditText>(R.id.etDescription)

        // PRE-POPULATE the EditTexts with existing todo data
        titleEt.setText(todo.title)
        descEt.setText(todo.description)
        
        val dialog = AlertDialog.Builder(this)
            .setTitle("Edit Todo")  // Changed title to reflect editing action
            .setView(dialogView)
            .setPositiveButton("Update") { d, _ ->  // Changed button text to "Update"
                val title = titleEt.text.toString().trim()
                val desc = descEt.text.toString().trim()

                // Validate input before updating
                if (title.isNotEmpty()) {
                    // FIXED: Find the existing todo in the list and update it
                    val todoIndex = todos.indexOfFirst { it.id == todo.id }
                    
                    if (todoIndex != -1) {
                        // Create updated todo with SAME ID but new data
                        val updatedTodo = Todo(
                            id = todo.id,  // IMPORTANT: Keep the same ID!
                            title = title,
                            description = desc
                        )
                        
                        // Replace the old todo with the updated one at the same position
                        todos[todoIndex] = updatedTodo
                        
                        // Update the adapter with the modified list
                        // This will trigger DiffUtil to detect the content change
                        adapter.submitList(todos.toList())
                        
                        Toast.makeText(this, "Todo '${title}' updated successfully!", Toast.LENGTH_SHORT).show()
                        d.dismiss()
                    } else {
                        // This shouldn't happen, but handle the error case
                        Toast.makeText(this, "Error: Todo not found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Show error if title is empty
                    Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { d, _ ->
                d.dismiss()
            }
            .create()

        dialog.show()
    }
    }



