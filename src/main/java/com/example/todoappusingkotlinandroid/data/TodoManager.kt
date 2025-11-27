package com.example.todoappusingkotlinandroid.data

import com.example.todoappusingkotlinandroid.data.Todo

/**
 * TodoManager - Singleton class for managing todos across the entire app
 * This ensures todos are maintained when navigating between activities
 * without using a local database.
 * 
 * KEY BENEFITS:
 * - Data persists across activity navigation
 * - Single source of truth for todos
 * - Easy to use from any activity
 * - Memory efficient (single instance)
 */
object TodoManager {
    
    // Private mutable list - only this class can modify it directly
    private val _todos = mutableListOf<Todo>()
    
    // Public read-only access to todos
    val todos: List<Todo> get() = _todos.toList()
    
    // Listeners for todo changes (for updating UI automatically)
    private val listeners = mutableListOf<() -> Unit>()
    
    /**
     * Add a new todo to the list
     * @param todo The todo item to add
     */
    fun addTodo(todo: Todo) {
        _todos.add(todo)
        notifyListeners()
    }
    
    /**
     * Update an existing todo
     * @param updatedTodo The updated todo item
     * @return true if update successful, false if todo not found
     */
    fun updateTodo(updatedTodo: Todo): Boolean {
        val index = _todos.indexOfFirst { it.id == updatedTodo.id }
        return if (index != -1) {
            _todos[index] = updatedTodo
            notifyListeners()
            true
        } else {
            false
        }
    }
    
    /**
     * Delete a todo from the list
     * @param todo The todo item to delete
     * @return true if deletion successful, false if todo not found
     */
    fun deleteTodo(todo: Todo): Boolean {
        val removed = _todos.remove(todo)
        if (removed) {
            notifyListeners()
        }
        return removed
    }
    
    /**
     * Delete a todo by ID
     * @param todoId The ID of the todo to delete
     * @return true if deletion successful, false if todo not found
     */
    fun deleteTodoById(todoId: Long): Boolean {
        val todo = _todos.find { it.id == todoId }
        return if (todo != null) {
            deleteTodo(todo)
        } else {
            false
        }
    }
    
    /**
     * Get a todo by ID
     * @param todoId The ID of the todo to find
     * @return The todo if found, null otherwise
     */
    fun getTodoById(todoId: Long): Todo? {
        return _todos.find { it.id == todoId }
    }
    
    /**
     * Clear all todos
     */
    fun clearAllTodos() {
        _todos.clear()
        notifyListeners()
    }
    
    /**
     * Get the count of todos
     * @return Number of todos
     */
    fun getTodoCount(): Int = _todos.size
    
    /**
     * Check if todos list is empty
     * @return true if no todos, false otherwise
     */
    fun isEmpty(): Boolean = _todos.isEmpty()
    
    /**
     * Add a listener that gets called when todos change
     * Useful for automatically updating UI when data changes
     * @param listener Function to call when todos change
     */
    fun addChangeListener(listener: () -> Unit) {
        listeners.add(listener)
    }
    
    /**
     * Remove a change listener
     * @param listener Function to remove from listeners
     */
    fun removeChangeListener(listener: () -> Unit) {
        listeners.remove(listener)
    }
    
    /**
     * Notify all listeners that todos have changed
     * This triggers UI updates in activities that are listening
     */
    private fun notifyListeners() {
        listeners.forEach { it.invoke() }
    }
    
    /**
     * For debugging - print all todos to console
     */
    fun printAllTodos() {
        println("=== TodoManager Debug ===")
        println("Total todos: ${_todos.size}")
        _todos.forEachIndexed { index, todo ->
            println("$index: ${todo.title} - ${todo.description}")
        }
        println("========================")
    }
}
