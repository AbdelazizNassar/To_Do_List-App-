package com.example.todolist

import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.format.DateFormat
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.todolist.database.Task
import com.example.todolist.database.TaskViewModel
import com.example.todolist.databinding.FragmentTaskBinding
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class TaskFragment : Fragment() {

    private lateinit var binding: FragmentTaskBinding
    private lateinit var taskViewModel: TaskViewModel
    private lateinit var categoryViewModel: CategoryViewModel
    private var selectedTimeInMillis: Long? = null
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskBinding.inflate(inflater, container, false)

        categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]
        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]
        createNotificationChannel()
        permissionLauncher = handlePermissionResponse()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAutoCompleteContent()
        handleSwitchCheckedChangeListener()
        handleAddTaskBtnClickListener()
        binding.timePickerButton.setOnClickListener {
            showDateTimePickerDialog()
        }
    }
    private fun handleSwitchCheckedChangeListener() {
        binding.switchId.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // for users who use Android 13 or higher
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
                else{
                    selectedTimeInMillis = null
                    binding.timePickerButton.isEnabled = true
                }
            }//for users who use Android 12 or lower
            else{
                binding.timePickerButton.isEnabled = false
                binding.timeDisplay.text = "No time set"

            }
        }
    }
    private fun handleAddTaskBtnClickListener(){
        binding.addTaskBtn.setOnClickListener {
            if (binding.autoCompleteTextView.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please select a category.", Toast.LENGTH_SHORT).show()
            } else if (binding.switchId.isChecked && selectedTimeInMillis == null) {
                Toast.makeText(requireContext(), "Please select a time.", Toast.LENGTH_SHORT).show()
            } else  if (binding.taskNameEt.text.toString().isEmpty()){
                Toast.makeText(requireContext(), "Please enter the task name.", Toast.LENGTH_SHORT).show()
            }
            else {
                addTask(categoryViewModel.category.value.toString())
            }
        }
    }

    private fun showDateTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val initialHour = calendar.get(Calendar.HOUR_OF_DAY)
        val initialMinute = calendar.get(Calendar.MINUTE)

        val is24HourFormat = DateFormat.is24HourFormat(requireContext())

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                // Handle date selection
                val selectedCalendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }

                val timePickerDialog = TimePickerDialog(
                    requireContext(),
                    { _, selectedHour, selectedMinute ->
                        // Handle time selection
                        selectedCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                        selectedCalendar.set(Calendar.MINUTE, selectedMinute)

                        val currentTime = Calendar.getInstance()

                        // Check if the selected time is in the past
                        if (selectedCalendar.timeInMillis < currentTime.timeInMillis) {
                            Toast.makeText(requireContext(), "Selected time is in the past!", Toast.LENGTH_LONG).show()
                            // Optionally, you can reopen the dialog or force the user to select a new time
                            showDateTimePickerDialog()
                            return@TimePickerDialog
                        }

                        // Set up the date format
                        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        val formattedDate = dateFormat.format(selectedCalendar.time)

                        // Set up the time format to include AM/PM
                        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                        val formattedTime = timeFormat.format(selectedCalendar.time)

                        // Combine date and time
                        val dateTimeString = "$formattedDate, $formattedTime"

                        binding.timeDisplay.text = dateTimeString
                        selectedTimeInMillis = selectedCalendar.timeInMillis
                    },
                    initialHour,
                    initialMinute,
                    is24HourFormat
                )

                // Show the time picker after selecting the date
                timePickerDialog.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Show the date picker
        datePickerDialog.show()
    }

    private fun addTask(categoryName: String) {
        val taskAlarm = TaskAlarm()
        taskViewModel.viewModelScope.launch {
            val taskName = binding.taskNameEt.text.toString()
            val timeInMillis = selectedTimeInMillis ?: 0
            val task = Task(name = taskName, category = categoryName, timeInMillis = timeInMillis, isReminded = binding.switchId.isChecked)
            taskViewModel.insert(task)
            Log.d("TAG", "addTask: $task")
            resetViewsContent()
            if(timeInMillis > 0){
                taskAlarm.requestExactAlarmPermission(requireContext(),timeInMillis,taskName){
                    binding.switchId.isChecked
                }
            }
            Toast.makeText(requireContext(), "Added Successfully!", Toast.LENGTH_SHORT).show()
        }

    }

    private fun handlePermissionResponse(): ActivityResultLauncher<String> {
        return registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d("TaskFragment", "Permission granted, showing time picker")
                binding.timePickerButton.isEnabled = true
            } else {
                // Show a dialog explaining why the permission is needed
                RequestAlertDialog.showAlertDialog(
                    requireContext(),
                    getString(R.string.notification),
                    Settings.ACTION_APP_NOTIFICATION_SETTINGS,
                ){
                    binding.switchId.isChecked = false
                }
                Toast.makeText(requireContext(), "Permission denied. Notifications won't be sent.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createNotificationChannel() {
        val name = "Notification Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("1", name, importance).apply {
            description = "Display notification channel for the app"
        }
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    private fun setAutoCompleteContent(){
        val categories = resources.getStringArray(R.array.categories).toList()
        // i used sublist to remove the first item from the array which is All
        val adapter = ArrayAdapter(requireContext(), R.layout.drow_down_item, categories.subList(1, categories.size))
        binding.autoCompleteTextView.setAdapter(adapter)
        binding.autoCompleteTextView.setOnItemClickListener { parent, _, position, _ ->
            categoryViewModel.updateCategory(parent.getItemAtPosition(position).toString())
        }
    }
    private fun resetViewsContent(){
        binding.taskNameEt.text?.clear()
        binding.autoCompleteTextView.text?.clear()
        binding.switchId.isChecked = false
    }

}
