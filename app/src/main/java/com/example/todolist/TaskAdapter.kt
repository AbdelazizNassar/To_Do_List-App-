package com.example.todolist

import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.icu.text.SimpleDateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.todolist.database.Task
import com.example.todolist.database.TaskViewModel
import com.example.todolist.databinding.TaskListItemBinding
import java.util.Calendar
import java.util.Locale

class TaskAdapter(private val fragment: Fragment, private val tasks : List<Task>) : Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(val binding: TaskListItemBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = TaskListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TaskViewHolder(binding)
    }

    override fun getItemCount() = tasks.size


    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val taskAlarm = TaskAlarm()
        val viewModel = ViewModelProvider(fragment)[TaskViewModel::class.java]
        val categoryViewModel = ViewModelProvider(fragment)[CategoryViewModel::class.java]
        val task = tasks[position]
        holder.binding.viewId.background = ColorDrawable(fragment.resources.getColor(R.color.blue))
        holder.binding.taskCheckBox.isChecked = task.isCompleted
        holder.binding.taskTv.text = task.name
        // Apply strike-through if the task is completed, remove it if not
        if (task.isCompleted) {
            holder.binding.taskTv.paintFlags =
                holder.binding.taskTv.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.binding.taskTv.paintFlags =
                holder.binding.taskTv.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        if (task.timeInMillis > 0) {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = task.timeInMillis
            }

            // Set up the date format
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(calendar.time)

            // Set up the time format to include AM/PM
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val formattedTime = timeFormat.format(calendar.time)

            // Combine date and time
            val dateTimeString = "$formattedDate, $formattedTime"

            holder.binding.taskTime.text = "$dateTimeString"
        } else {
            holder.binding.taskTime.text = ""
        }
        // Handle the checkbox change event
        holder.binding.taskCheckBox.setOnCheckedChangeListener { _, isChecked ->
            task.isCompleted=isChecked
                viewModel.update(task)
            // this because when user press on the task which in All category the All tasks appear
            if(categoryViewModel.category.value == "All"){
                categoryViewModel.updateCategory("All")
            }else{
                categoryViewModel.updateCategory(task.category)
            }
            // Reapply the strike-through effect based on the new state
            Log.d("checked","$isChecked")
            if (isChecked) {
                holder.binding.taskTv.paintFlags =
                    holder.binding.taskTv.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                Toast.makeText(fragment.requireContext(), "You did it! Keep the momentum!", Toast.LENGTH_SHORT).show()
                // Cancel the alarm for this task
                taskAlarm.cancelAlarm(fragment.requireContext(), task.timeInMillis, task.name)
            } else {
                holder.binding.taskTv.paintFlags =
                    holder.binding.taskTv.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                Toast.makeText(fragment.requireContext(), "Oops! Let's get back to complete this task!", Toast.LENGTH_SHORT).show()
                // Set the alarm for this task
                if(task.timeInMillis > System.currentTimeMillis()){
                    taskAlarm.setAlarm(fragment.requireContext(), task.timeInMillis, task.name)
                }else{
                    taskAlarm.cancelAlarm(fragment.requireContext(), task.timeInMillis, task.name)
                }
            }
        }
        // delete the task from database
        holder.binding.deleteTask.setOnClickListener {
            viewModel.delete(task)
            if(categoryViewModel.category.value == "All"){
                categoryViewModel.updateCategory("All")
            }else{
                categoryViewModel.updateCategory(task.category)
            }
            Toast.makeText(fragment.requireContext(), "Deleted Successfully!", Toast.LENGTH_SHORT).show()
            taskAlarm.cancelAlarm(fragment.requireContext(), task.timeInMillis, task.name)
        }
    }

}