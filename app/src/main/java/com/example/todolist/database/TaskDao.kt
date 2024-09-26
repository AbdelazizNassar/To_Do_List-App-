package com.example.todolist.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)
    @Update
     fun updateTask(task: Task)
    @Delete
     fun deleteTask(task: Task)



    // if u used live date then don't use suspend function
    @Query("SELECT * FROM task")
    suspend fun getTasks(): List<Task>

    // if u used live date then don't use suspend function
    @Query("SELECT * FROM task WHERE category = :category")
     suspend fun getTasksByCategory(category: String): List<Task>



}