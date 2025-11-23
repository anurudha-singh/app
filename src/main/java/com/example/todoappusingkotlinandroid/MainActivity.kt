package com.example.todoappusingkotlinandroid

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.todoappusingkotlinandroid.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var counter =0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.button.setOnClickListener {

            binding.editTextText.text=counter.toString()
            counter++
            // Handle button tap here
//            Toast.makeText(this, "Button clicked!", Toast.LENGTH_SHORT).show()
        }

        binding.button2.setOnClickListener {
            val intent = Intent(this, ToDoActivity::class.java)
            startActivity(intent)

        }
    }
    //TODO: Implement functions to add, update, delete, and display tasks
    //TODO: Implement counter function to update the text count display
    // TODO: set up RecyclerView & FAB listeners



}
