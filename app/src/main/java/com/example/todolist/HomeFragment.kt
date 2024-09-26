package com.example.todolist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todolist.database.TaskViewModel
import com.example.todolist.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var categoryViewModel: CategoryViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showCategories()
        observeCategoryChanges()
    }

    private fun observeCategoryChanges() {
        categoryViewModel.category.observe(viewLifecycleOwner) { category ->
            Log.d("category", "Category changed: $category")
          showTasks(category)
        }
    }

    private fun showCategories() {
        val categories =resources.getStringArray(R.array.categories)
        val adapter = CategoryAdapter(this, categories.toList())
        binding.categoryRv.adapter = adapter
    }

    private fun showTasks(category: String) {
        taskViewModel.viewModelScope.launch {
                if (category == "All") {
                    if(taskViewModel.getAllTasks().isEmpty()){
                        binding.backgroundIV.setImageResource(R.drawable.to_do_list)
                        binding.backgroundTV.text = "No Tasks Added Yet"
                        val adapter = TaskAdapter(this@HomeFragment, taskViewModel.getAllTasks())
                        binding.homeRv.adapter = adapter
                    }
                    else{
                        binding.backgroundIV.setImageResource(0)
                        binding.backgroundTV.text = ""
                        val adapter = TaskAdapter(this@HomeFragment, taskViewModel.getAllTasks())
                        binding.homeRv.adapter = adapter
                    }
                } else {
                    val tasks = taskViewModel.getTasksByCategory(category)
                    if(tasks.isEmpty()){
                        binding.backgroundIV.setImageResource(R.drawable.to_do_list)
                        binding.backgroundTV.text = "No Tasks Added Yet"
                        val adapter = TaskAdapter(this@HomeFragment, tasks)
                        Log.d("TAg", "showTasks: $tasks")
                        binding.homeRv.adapter = adapter
                    }else{
                        binding.backgroundIV.setImageResource(0)
                        binding.backgroundTV.text = ""
                        val adapter = TaskAdapter(this@HomeFragment, tasks)
                        Log.d("TAg", "showTasks: $tasks")
                        binding.homeRv.adapter = adapter
                    }

                }
        }
    }

}
