package com.dataapk.lifeorganizer

import android.content.Context
import androidx.lifecycle.LiveData
import com.dataapk.lifeorganizer.data.database.TaskDatabase
import com.dataapk.lifeorganizer.data.repository.TaskRepository
import com.dataapk.lifeorganizer.data.model.Task
import com.dataapk.lifeorganizer.data.model.Priority
import com.dataapk.lifeorganizer.data.model.Category
import kotlinx.coroutines.*

object TaskManager {
    private var repository: TaskRepository? = null
    private var taskChangeListener: ((List<Task>) -> Unit)? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun initialize(context: Context) {
        val database = TaskDatabase.getDatabase(context)
        repository = TaskRepository(database.taskDao())
    }

    fun setTaskChangeListener(listener: ((List<Task>) -> Unit)?) {
        taskChangeListener = listener
    }

    private fun notifyTaskChange() {
        scope.launch {
            val tasks = repository?.getAllTasksList() ?: emptyList()
            withContext(Dispatchers.Main) {
                taskChangeListener?.invoke(tasks)
            }
        }
    }

    fun getAllTasksFlow(): LiveData<List<Task>>? = repository?.getAllTasks()

    fun addTask(task: Task) {
        scope.launch {
            repository?.insertTask(task)
            notifyTaskChange()
        }
    }

    fun updateTask(updatedTask: Task) {
        scope.launch {
            repository?.updateTask(updatedTask)
            notifyTaskChange()
        }
    }

    fun deleteTask(task: Task) {
        scope.launch {
            repository?.deleteTask(task)
            notifyTaskChange()
        }
    }

    fun deleteTaskById(id: Long) {
        scope.launch {
            repository?.deleteTaskById(id)
            notifyTaskChange()
        }
    }

    fun toggleTaskCompletion(id: Long, isCompleted: Boolean) {
        scope.launch {
            repository?.updateTaskStatus(id, isCompleted)
            notifyTaskChange()
        }
    }

    // Synchronous methods for backward compatibility (use with caution)
    suspend fun getAllTasks(): List<Task> = repository?.getAllTasksList() ?: emptyList()
    suspend fun getCompletedTasks(): List<Task> = repository?.getCompletedTasks() ?: emptyList()
    suspend fun getPendingTasks(): List<Task> = repository?.getPendingTasks() ?: emptyList()
    suspend fun getTasksByCategory(category: Category): List<Task> =
        withContext(Dispatchers.IO) {
            // Since Room doesn't provide suspend version for filtered queries by default,
            // we'll get all tasks and filter them
            getAllTasks().filter { it.category == category }
        }
    suspend fun getTasksByPriority(priority: Priority): List<Task> =
        withContext(Dispatchers.IO) {
            getAllTasks().filter { it.priority == priority }
        }

    suspend fun getCompletedTaskCount(): Int = repository?.getCompletedTaskCountSync() ?: 0
    suspend fun getTaskCount(): Int = repository?.getTaskCountSync() ?: 0
    suspend fun getPendingTaskCount(): Int = repository?.getPendingTaskCountSync() ?: 0
    suspend fun getSuccessRate(): Int = repository?.getSuccessRate() ?: 0

    // LiveData methods for UI observation
    fun getTasksByPriorityLiveData(priority: Priority): LiveData<List<Task>>? =
        repository?.getTasksByPriority(priority)

    fun getTasksByStatusLiveData(isCompleted: Boolean): LiveData<List<Task>>? =
        repository?.getTasksByStatus(isCompleted)

    fun getTasksByCategoryLiveData(category: Category): LiveData<List<Task>>? =
        repository?.getTasksByCategory(category)

    fun cleanup() {
        scope.cancel()
    }
}