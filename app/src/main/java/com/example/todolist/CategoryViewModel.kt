package com.example.todolist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CategoryViewModel: ViewModel()  {


    private val _category= MutableLiveData("All")
    val category: LiveData<String> = _category

    fun updateCategory(newCategory: String) {
        _category.value = newCategory
    }

}