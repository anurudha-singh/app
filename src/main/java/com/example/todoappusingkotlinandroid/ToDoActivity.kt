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
import com.example.todoappusingkotlinandroid.data.TodoManager

class ToDoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityToDoBinding
    private lateinit var adapter: TodoAdapter
    // REMOVED: private val todos = mutableListOf<Todo>()
    // NOW USING: TodoManager.todos for shared state management

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

        // FIXED: Load todos from TodoManager and submit to adapter
        loadTodosFromManager()

        // Navigation button to go back to MainActivity
        binding.button3.setOnClickListener {
            startActivity(
                Intent(this, MainActivity::class.java)
            )
        }
        // FIXED: Add error handling for navigation to GeneralScreen
        binding.button4.setOnClickListener {
            try {
                val intent = Intent(this, GeneralScreen::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                // Handle the error gracefully
                Toast.makeText(this, "Error opening General Screen: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
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
                        
                        // FIXED: Add to TodoManager instead of local list
                        TodoManager.addTodo(newTodo)
                        
                        // IMPORTANT: Update the adapter with todos from manager
                        // This ensures state is maintained across activities
                        adapter.submitList(TodoManager.todos)
                        
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

    // FIXED: Use TodoManager for delete operation
    private fun deleteTodo(todo: Todo) {
        // Remove from TodoManager (maintains state across activities)
        val deleted = TodoManager.deleteTodo(todo)
        
        if (deleted) {
            // Update the adapter with the current todos from manager
            adapter.submitList(TodoManager.todos)
            
            // Show confirmation to user
            Toast.makeText(this, "Todo '${todo.title}' deleted", Toast.LENGTH_SHORT).show()
        } else {
            // Handle error case
            Toast.makeText(this, "Error deleting todo", Toast.LENGTH_SHORT).show()
        }
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
                    // FIXED: Use TodoManager to update the todo
                    val updatedTodo = Todo(
                        id = todo.id,  // IMPORTANT: Keep the same ID!
                        title = title,
                        description = desc
                    )
                    
                    // Update through TodoManager (maintains state across activities)
                    val updated = TodoManager.updateTodo(updatedTodo)
                    
                    if (updated) {
                        // Update the adapter with the modified list from manager
                        adapter.submitList(TodoManager.todos)
                        
                        Toast.makeText(this, "Todo '${title}' updated successfully!", Toast.LENGTH_SHORT).show()
                        d.dismiss()
                    } else {
                        // Handle error case
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

    // FIXED: Helper method to load todos from TodoManager
    private fun loadTodosFromManager() {
        // Get todos from the shared TodoManager and submit to adapter
        adapter.submitList(TodoManager.todos)
        
        // Optional: Add change listener to automatically update when todos change
        // This ensures UI stays in sync if todos are modified from other activities
        TodoManager.addChangeListener {
            runOnUiThread {
                adapter.submitList(TodoManager.todos)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // IMPORTANT: Refresh todos when returning to this activity
        // This ensures we see any changes made in other activities
        loadTodosFromManager()
    }
}



