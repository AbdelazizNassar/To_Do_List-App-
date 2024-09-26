package com.example.todolist.database

import androidx.lifecycle.LiveData

class TaskRepository(private val dao : TaskDao) {

    // LiveData of all tasks from the database
    suspend fun getAllTasks(): List<Task>{
        return dao.getTasks()
    }

    // LiveData of tasks filtered by category from the database
    suspend  fun getTasksByCategory(category: String): List<Task>{
        return dao.getTasksByCategory(category)
    }

    // Insert a task into the database
    suspend fun insertTask(task: Task) {
        dao.insertTask(task)
    }

    // Update a task in the database
     fun updateTask(task: Task) {
        dao.updateTask(task)
    }
    // Delete a task from the database
     fun deleteTask(task: Task) {
        // This function is not suspend since it’s a database operation that can be run synchronously.
        dao.deleteTask(task)
    }
}