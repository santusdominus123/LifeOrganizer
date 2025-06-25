package com.dataapk.lifeorganizer.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.dataapk.lifeorganizer.data.database.TaskDatabase
import com.dataapk.lifeorganizer.data.repository.TaskRepository
import com.dataapk.lifeorganizer.data.model.Task
import com.dataapk.lifeorganizer.data.model.Priority
import com.dataapk.lifeorganizer.data.model.Category
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository

    // LiveData properties
    val allTasks: LiveData<List<Task>>
    val taskCount: LiveData<Int>
    val pendingTaskCount: LiveData<Int>
    val completedTaskCount: LiveData<Int>

    init {
        val taskDao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        allTasks = repository.getAllTasks()
        taskCount = repository.getTaskCount()
        pendingTaskCount = repository.getPendingTaskCount()
        completedTaskCount = repository.getCompletedTaskCount()
    }

    // Suspend functions for database operations
    suspend fun getTaskById(id: Long): Task? {
        return repository.getTaskById(id)
    }

    fun getTasksByPriority(priority: Priority): LiveData<List<Task>> {
        return repository.getTasksByPriority(priority)
    }

    fun getTasksByCategory(category: Category): LiveData<List<Task>> {
        return repository.getTasksByCategory(category)
    }

    fun getTasksByStatus(isCompleted: Boolean): LiveData<List<Task>> {
        return repository.getTasksByStatus(isCompleted)
    }

    fun insertTask(task: Task) {
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun updateTaskStatus(id: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateTaskStatus(id, isCompleted)
        }
    }
}