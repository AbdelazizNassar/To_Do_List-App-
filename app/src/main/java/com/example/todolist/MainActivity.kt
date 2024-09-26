package com.example.todolist

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.todolist.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.blue)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Setup NavController
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        // Setup ActionBar with NavController
        NavigationUI.setupActionBarWithNavController(this, navController)

        // Set title based on the current fragment
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.taskFragment ->{
                    binding.addBtn.hide()
                    supportActionBar?.title = "AddingTask"
                }
                R.id.homeFragment2 -> {
                    binding.addBtn.show()
                    supportActionBar?.title = "ToDoList"
                }
                // Add more cases for other fragments if needed
                else -> supportActionBar?.title = "ToDoList" // Default title
            }
        }
        binding.addBtn.setOnClickListener {
            navController.navigate(R.id.action_homeFragment2_to_taskFragment)
        }
    }

    // Handle back button in the action bar
    override fun onSupportNavigateUp() = navController.navigateUp()
}
