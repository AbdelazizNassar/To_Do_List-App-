package com.example.todolist.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository


    init {
        val taskDao = RoomDbHelper.getInstance(application).dao
        repository = TaskRepository(taskDao)
    }

    suspend fun getAllTasks(): List<Task>{
        return repository.getAllTasks()
    }

    suspend fun getTasksByCategory(category: String): List<Task>{
        return repository.getTasksByCategory(category)
    }
    // Use viewModelScope for coroutines to launch background tasks
    fun insert(task: Task) = viewModelScope.launch {
        repository.insertTask(task)
    }

      fun update(task: Task) {
          repository.updateTask(task)
      }


     fun delete(task: Task) {
        repository.deleteTask(task)
    }

}