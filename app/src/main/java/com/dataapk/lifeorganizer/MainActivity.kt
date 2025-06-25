package com.dataapk.lifeorganizer

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.dataapk.lifeorganizer.data.model.Task
import com.dataapk.lifeorganizer.data.model.Priority
import com.dataapk.lifeorganizer.data.model.Category
import com.dataapk.lifeorganizer.ui.adapter.TaskAdapter
import com.dataapk.lifeorganizer.ui.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val taskViewModel: TaskViewModel by viewModels()
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var recyclerView: RecyclerView

    // Views for filtering
    private lateinit var spinnerPriority: Spinner
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnShowAll: MaterialButton
    private lateinit var btnShowPending: MaterialButton
    private lateinit var btnShowCompleted: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViews()
        setupRecyclerView()
        setupObservers()
        setupFilterSpinners()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.recyclerViewTasks)
        spinnerPriority = findViewById(R.id.spinnerPriority)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnShowAll = findViewById(R.id.btnShowAll)
        btnShowPending = findViewById(R.id.btnShowPending)
        btnShowCompleted = findViewById(R.id.btnShowCompleted)

        // Add task button
        val fabAddTask = findViewById<View>(R.id.fabAddTask)
        fabAddTask.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }

        // Filter buttons
        btnShowAll.setOnClickListener { showAllTasks() }
        btnShowPending.setOnClickListener { showPendingTasks() }
        btnShowCompleted.setOnClickListener { showCompletedTasks() }
    }

    private fun setupRecyclerView() {
        taskAdapter = TaskAdapter(
            onTaskClick = { task -> editTask(task) },
            onTaskStatusChanged = { task, isCompleted ->
                updateTaskStatus(task, isCompleted)
            },
            onTaskDelete = { task -> deleteTask(task) }
        )

        recyclerView.apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupObservers() {
        taskViewModel.allTasks.observe(this, Observer { tasks ->
            tasks?.let { taskAdapter.submitList(it) }
        })

        taskViewModel.taskCount.observe(this, Observer { count ->
            // Update task count in UI if needed
        })
    }

    private fun setupFilterSpinners() {
        // Priority filter
        val priorityValues = Priority.values().map { it.name }
        val priorityAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            priorityValues
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerPriority.adapter = priorityAdapter
        spinnerPriority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedPriority = Priority.values()[position]
                filterByPriority(selectedPriority)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Category filter
        val categoryValues = Category.values().map { it.name }
        val categoryAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categoryValues
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerCategory.adapter = categoryAdapter
        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCategory = Category.values()[position]
                filterByCategory(selectedCategory)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun filterByPriority(priority: Priority) {
        taskViewModel.getTasksByPriority(priority).observe(this, Observer { tasks ->
            tasks?.let { taskAdapter.submitList(it) }
        })
    }

    private fun filterByCategory(category: Category) {
        taskViewModel.getTasksByCategory(category).observe(this, Observer { tasks ->
            tasks?.let { taskAdapter.submitList(it) }
        })
    }

    private fun showAllTasks() {
        taskViewModel.allTasks.observe(this, Observer { tasks ->
            tasks?.let { taskAdapter.submitList(it) }
        })
    }

    private fun showPendingTasks() {
        taskViewModel.getTasksByStatus(false).observe(this, Observer { tasks ->
            tasks?.let { taskAdapter.submitList(it) }
        })
    }

    private fun showCompletedTasks() {
        taskViewModel.getTasksByStatus(true).observe(this, Observer { tasks ->
            tasks?.let { taskAdapter.submitList(it) }
        })
    }

    private fun editTask(task: Task) {
        val intent = Intent(this, AddTaskActivity::class.java)
        intent.putExtra("TASK_ID", task.id)
        startActivity(intent)
    }

    private fun updateTaskStatus(task: Task, isCompleted: Boolean) {
        taskViewModel.updateTaskStatus(task.id, isCompleted)
    }

    private fun deleteTask(task: Task) {
        taskViewModel.deleteTask(task)
        Toast.makeText(this, "Task deleted: ${task.title}", Toast.LENGTH_SHORT).show()
    }
}