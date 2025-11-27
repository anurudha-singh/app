package com.example.todoappusingkotlinandroid

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todoappusingkotlinandroid.databinding.ActivityGeneralScreenBinding
import com.example.todoappusingkotlinandroid.data.TodoManager
import android.widget.Toast

class GeneralScreen : AppCompatActivity() {
    private lateinit var binding: ActivityGeneralScreenBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // FIXED: Initialize binding before using it
        binding = ActivityGeneralScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)  // Use binding.root instead of R.layout
        
        // FIXED: Add null check and error handling
        try {
            binding.btToNavigateBackToTodoActivity.setOnClickListener {
                startActivity(
                    Intent(this, ToDoActivity::class.java)
                )
            }
            
            // DEMO: Show that TodoManager maintains state across activities
            displayTodoInfo()
            
        } catch (e: Exception) {
            // Log the error or show a toast
            e.printStackTrace()
        }
    }

    // Method to demonstrate that todos are maintained across activities
    private fun displayTodoInfo() {
        val todoCount = TodoManager.getTodoCount()
        val message = if (todoCount > 0) {
            "You have $todoCount todo(s) waiting in the previous screen!"
        } else {
            "No todos yet. Go back and create some!"
        }
        
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        
        // Update the TextView to show todo information
        try {
            binding.textView.text = "General Screen\n\n$message\n\nState is maintained without database!"
        } catch (e: Exception) {
            // Handle if textView binding fails
            e.printStackTrace()
        }
    }
}