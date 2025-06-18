package com.dataapk.lifeorganizer

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Spinner
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import android.widget.ProgressBar // Tambahkan ini juga jika belum ada
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var etTaskTitle: TextInputEditText
    private lateinit var etTaskDescription: TextInputEditText
    private lateinit var etDeadline: TextInputEditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnPriorityHigh: MaterialButton
    private lateinit var btnPriorityMedium: MaterialButton
    private lateinit var btnPriorityLow: MaterialButton
    private lateinit var btnCancel: MaterialButton
    private lateinit var btnSaveTask: MaterialButton
    private lateinit var progressBarSaving: ProgressBar

    private var selectedPriority = Task.Priority.MEDIUM
    private var selectedCategory = Task.Category.WORK
    private var editingTask: Task? = null
    private lateinit var taskRepository: TaskRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        taskRepository = TaskRepository(this)
        initializeViews()
        setupCategorySpinner()
        setupClickListeners()
        setupPriorityButtons()
        checkForEditTask()
    }

    private fun initializeViews() {
        etTaskTitle = findViewById(R.id.etTaskTitle)
        etTaskDescription = findViewById(R.id.etTaskDescription)
        etDeadline = findViewById(R.id.etDeadline)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        btnPriorityHigh = findViewById(R.id.btnPriorityHigh)
        btnPriorityMedium = findViewById(R.id.btnPriorityMedium)
        btnPriorityLow = findViewById(R.id.btnPriorityLow)
        btnCancel = findViewById(R.id.btnCancel)
        btnSaveTask = findViewById(R.id.btnSaveTask)
        progressBarSaving = findViewById(R.id.progressBarSaving)
    }

    private fun checkForEditTask() {
        val taskId = intent.getStringExtra("task_id")
        if (taskId != null) {
            editingTask = taskRepository.getTaskById(taskId)
            editingTask?.let { task ->
                etTaskTitle.setText(task.title)
                etTaskDescription.setText(task.description)
                etDeadline.setText(task.deadline)
                selectedPriority = task.priority
                selectedCategory = task.category

                // Update UI based on the task being edited
                when (selectedPriority) {
                    Task.Priority.HIGH -> setPrioritySelection(btnPriorityHigh)
                    Task.Priority.MEDIUM -> setPrioritySelection(btnPriorityMedium)
                    Task.Priority.LOW -> setPrioritySelection(btnPriorityLow)
                }

                spinnerCategory.setSelection(task.category.ordinal)
                btnSaveTask.text = "UPDATE TASK"
            }
        }
    }

    private fun setupCategorySpinner() {
        val categoryAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            Task.Category.values().map { it.name }
        )
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter
        spinnerCategory.setSelection(selectedCategory.ordinal)

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory = Task.Category.values()[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupPriorityButtons() {
        // Set default selection
        setPrioritySelection(btnPriorityMedium)

        btnPriorityHigh.setOnClickListener {
            selectedPriority = Task.Priority.HIGH
            setPrioritySelection(btnPriorityHigh)
        }

        btnPriorityMedium.setOnClickListener {
            selectedPriority = Task.Priority.MEDIUM
            setPrioritySelection(btnPriorityMedium)
        }

        btnPriorityLow.setOnClickListener {
            selectedPriority = Task.Priority.LOW
            setPrioritySelection(btnPriorityLow)
        }
    }

    private fun setPrioritySelection(selectedButton: MaterialButton) {
        // Reset all buttons
        btnPriorityHigh.apply {
            strokeWidth = 1
            backgroundTintList = ContextCompat.getColorStateList(context, android.R.color.transparent)
        }
        btnPriorityMedium.apply {
            strokeWidth = 1
            backgroundTintList = ContextCompat.getColorStateList(context, android.R.color.transparent)
        }
        btnPriorityLow.apply {
            strokeWidth = 1
            backgroundTintList = ContextCompat.getColorStateList(context, android.R.color.transparent)
        }

        // Set selected button
        selectedButton.apply {
            strokeWidth = 3
            backgroundTintList = ContextCompat.getColorStateList(context, R.color.primary_100)
        }
    }

    private fun setupClickListeners() {
        btnCancel.setOnClickListener {
            finish()
        }

        btnSaveTask.setOnClickListener {
            saveTask()
        }

        etDeadline.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, day ->
                val formattedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", month + 1, day, year)
                etDeadline.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun saveTask() {
        val title = etTaskTitle.text.toString().trim()
        val description = etTaskDescription.text.toString().trim()
        val deadline = etDeadline.text.toString().trim()

        if (title.isEmpty()) {
            etTaskTitle.error = "Please enter task title"
            etTaskTitle.requestFocus()
            return
        }

        if (deadline.isEmpty()) {
            etDeadline.error = "Please select deadline"
            etDeadline.requestFocus()
            return
        }

        progressBarSaving.visibility = View.VISIBLE
        btnSaveTask.isEnabled = false
        btnCancel.isEnabled = false

        if (editingTask != null) {
            // Update existing task
            val updatedTask = editingTask!!.copy(
                title = title,
                description = description,
                priority = selectedPriority,
                category = selectedCategory,
                deadline = deadline
            )
            taskRepository.updateTask(updatedTask)
            Toast.makeText(this, "Task updated successfully!", Toast.LENGTH_SHORT).show()
        } else {
            // Create new task
            val newTask = Task(
                title = title,
                description = description,
                priority = selectedPriority,
                category = selectedCategory,
                deadline = deadline
            )
            taskRepository.createTask(newTask)
            Toast.makeText(this, "Task created successfully!", Toast.LENGTH_SHORT).show()
        }

        setResult(RESULT_OK)
        finish()
    }
}