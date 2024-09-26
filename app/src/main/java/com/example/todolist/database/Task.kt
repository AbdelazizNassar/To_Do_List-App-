package com.example.todolist.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(
    @PrimaryKey(true)
    @ColumnInfo("_id")
    val id : Int = 0,
    var isCompleted : Boolean = false,
    val name : String,
    val category : String,
    val timeInMillis : Long = 0,
    val isReminded : Boolean,
)
