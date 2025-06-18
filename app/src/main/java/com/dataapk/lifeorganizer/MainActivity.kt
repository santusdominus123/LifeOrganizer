package com.dataapk.lifeorganizer

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var tasksAdapter: TasksAdapter
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var fabAddTask: FloatingActionButton
    private lateinit var emptyStateView: View

    private val addTaskLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let { data ->
                try {
                    val title = data.getStringExtra("new_task_title") ?: return@let
                    val description = data.getStringExtra("new_task_description") ?: ""
                    val priorityName = data.getStringExtra("new_task_priority") ?: "MEDIUM"
                    val categoryName = data.getStringExtra("new_task_category") ?: "WORK"
                    val deadline = data.getStringExtra("new_task_deadline") ?: ""

                    val priority = Task.Priority.valueOf(priorityName)
                    val category = Task.Category.valueOf(categoryName)

                    val newTask = Task(
                        title = title,
                        description = description,
                        priority = priority,
                        category = category,
                        deadline = deadline
                    )

                    TaskManager.addTask(newTask)
                    Toast.makeText(this, "Task added successfully! 🚀", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing new task", e)
                    Toast.makeText(this, "Error adding task", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            Log.d(TAG, "Starting MainActivity onCreate")
            setContentView(R.layout.activity_main)
            Log.d(TAG, "Layout set successfully")

            // Initialize Firebase listener
            FirebaseManager.listenForTaskChanges { tasks ->
                runOnUiThread {
                    TaskManager.updateTasksFromFirebase(tasks)
                    updateTasksList()
                    updateDashboardStats()
                }
            }

            initializeViews()
            setupRecyclerView()
            setupFab()

        } catch (e: Exception) {
            Log.e(TAG, "Critical error in onCreate", e)
            e.printStackTrace()
            showFallbackLayout()
        }
    }

    private fun showFallbackLayout() {
        try {
            setContentView(android.R.layout.simple_list_item_1)
            val textView = findViewById<TextView>(android.R.id.text1)
            textView?.text = "App is loading... Please restart if this persists."
        } catch (e: Exception) {
            Log.e(TAG, "Even fallback layout failed", e)
        }
    }

    private fun setupRecyclerView() {
        try {
            tasksRecyclerView = findViewById(R.id.recyclerViewTasks)
            emptyStateView = findViewById(R.id.emptyStateView)

            tasksAdapter = TasksAdapter(
                tasks = TaskManager.getAllTasks().toMutableList(),
                onTaskToggle = { task ->
                    TaskManager.toggleTaskCompletion(task.id)
                    updateDashboardStats()
                },
                onTaskEdit = { task ->
                    showEditTaskDialog(task)
                },
                onTaskDelete = { task ->
                    showDeleteConfirmDialog(task)
                }
            )

            tasksRecyclerView.apply {
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = tasksAdapter
                addItemDecoration(DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL))
            }

            updateTasksList()

        } catch (e: Exception) {
            Log.e(TAG, "Error setting up RecyclerView", e)
        }
    }

    private fun setupFab() {
        try {
            fabAddTask = findViewById(R.id.fabAddTask)
            fabAddTask.setOnClickListener {
                val intent = Intent(this, AddTaskActivity::class.java)
                addTaskLauncher.launch(intent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up FAB", e)
        }
    }

    private fun updateTasksList() {
        try {
            val tasks = TaskManager.getAllTasks()
            tasksAdapter.updateTasks(tasks)

            if (tasks.isEmpty()) {
                tasksRecyclerView.visibility = View.GONE
                emptyStateView.visibility = View.VISIBLE
            } else {
                tasksRecyclerView.visibility = View.VISIBLE
                emptyStateView.visibility = View.GONE
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating tasks list", e)
        }
    }

    private fun showEditTaskDialog(task: Task) {
        try {
            val dialogView = layoutInflater.inflate(R.layout.activity_add_task, null)


        } catch (e: Exception) {
            Log.e(TAG, "Error showing edit dialog", e)
            Toast.makeText(this, "Error opening edit dialog", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updatePriorityView(priority: Task.Priority, textView: TextView, indicator: View) {
        textView.text = priority.name
        val color = when(priority) {
            Task.Priority.HIGH -> Color.RED
            Task.Priority.MEDIUM -> Color.YELLOW
            Task.Priority.LOW -> Color.GREEN
        }
        indicator.setBackgroundColor(color)
    }

    private fun updateCategoryView(category: Task.Category, textView: TextView, iconView: ImageView) {
        textView.text = category.name

        val iconRes = when(category) {
            Task.Category.WORK -> android.R.drawable.ic_menu_agenda
            Task.Category.STUDY -> android.R.drawable.ic_menu_edit
            Task.Category.PERSONAL -> android.R.drawable.ic_menu_my_calendar
            Task.Category.HEALTH -> android.R.drawable.ic_menu_compass
        }
        iconView.setImageResource(iconRes)
    }

    private fun showDatePickerDialog(etDeadline: EditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                val formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", month + 1, day, year)
                etDeadline.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun setupSpinners(prioritySpinner: Spinner, categorySpinner: Spinner, task: Task) {
        val priorityAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            Task.Priority.values().map { it.name }
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            prioritySpinner.adapter = this
            prioritySpinner.setSelection(task.priority.ordinal)
        }

        val categoryAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            Task.Category.values().map { it.name }
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categorySpinner.adapter = this
            categorySpinner.setSelection(task.category.ordinal)
        }
    }

    private fun updateTask(
        originalTask: Task,
        etTitle: EditText,
        etDescription: EditText,
        etDeadline: EditText,
        spinnerPriority: Spinner,
        spinnerCategory: Spinner
    ) {
        try {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val deadline = etDeadline.text.toString().trim()

            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter task title", Toast.LENGTH_SHORT).show()
                return
            }

            val selectedPriority = Task.Priority.values()[spinnerPriority.selectedItemPosition]
            val selectedCategory = Task.Category.values()[spinnerCategory.selectedItemPosition]

            val updatedTask = originalTask.copy(
                title = title,
                description = description,
                priority = selectedPriority,
                category = selectedCategory,
                deadline = deadline
            )

            TaskManager.updateTask(originalTask.id, updatedTask)
            Toast.makeText(this, "Task updated successfully! ✅", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Log.e(TAG, "Error updating task", e)
            Toast.makeText(this, "Error updating task", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteConfirmDialog(task: Task) {
        AlertDialog.Builder(this)
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete '${task.title}'?")
            .setPositiveButton("Delete") { _, _ ->
                TaskManager.deleteTask(task.id)
                Toast.makeText(this, "Task deleted successfully! 🗑️", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun initializeViews() {
        try {
            initializeTabLayout()
            updateDashboardStats()
            Log.d(TAG, "All views initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing views", e)
            e.printStackTrace()
        }
    }

    private fun updateDashboardStats() {
        try {
            val completedTasks = TaskManager.getCompletedTaskCount()
            val totalTasks = TaskManager.getTaskCount()
            val pendingTasks = TaskManager.getPendingTaskCount()
            val successRate = TaskManager.getSuccessRate()

            findViewById<TextView>(R.id.tvTaskCount)?.text = "$completedTasks/$totalTasks"
            findViewById<TextView>(R.id.tvCompletedCount)?.text = completedTasks.toString()
            findViewById<TextView>(R.id.tvPendingCount)?.text = pendingTasks.toString()
            findViewById<TextView>(R.id.tvSuccessRate)?.text = "$successRate%"

            val progressBar = findViewById<ProgressBar>(R.id.progressBarTask)
            if (progressBar != null && totalTasks > 0) {
                progressBar.progress = successRate
            }

            Log.d(TAG, "Dashboard stats updated: $completedTasks/$totalTasks")

        } catch (e: Exception) {
            Log.e(TAG, "Error updating dashboard stats", e)
        }
    }

    private fun initializeTabLayout() {
        try {
            val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
            tabLayout?.let {
                it.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        when (tab?.position) {
                            0 -> showDashboardView()
                            1 -> showTasksView()
                            2 -> showFinanceView()
                        }
                    }
                    override fun onTabUnselected(tab: TabLayout.Tab?) {}
                    override fun onTabReselected(tab: TabLayout.Tab?) {}
                })

                // Select first tab by default
                it.getTabAt(0)?.select()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing TabLayout", e)
        }
    }

    private fun showDashboardView() {
        findViewById<View>(R.id.dashboardContent)?.visibility = View.VISIBLE
        findViewById<View>(R.id.taskContentContainer)?.visibility = View.GONE
        findViewById<View>(R.id.financeContent)?.visibility = View.GONE
        fabAddTask.visibility = View.GONE
    }

    private fun showTasksView() {
        findViewById<View>(R.id.dashboardContent)?.visibility = View.GONE
        findViewById<View>(R.id.taskContentContainer)?.visibility = View.VISIBLE
        findViewById<View>(R.id.financeContent)?.visibility = View.GONE
        fabAddTask.visibility = View.VISIBLE
        updateTasksList()
    }

    private fun showFinanceView() {
        findViewById<View>(R.id.dashboardContent)?.visibility = View.GONE
        findViewById<View>(R.id.taskContentContainer)?.visibility = View.GONE
        findViewById<View>(R.id.financeContent)?.visibility = View.VISIBLE
        fabAddTask.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        updateDashboardStats()
        updateTasksList()
    }

    override fun onDestroy() {
        super.onDestroy()
        TaskManager.setTaskChangeListener(null)
    }
}