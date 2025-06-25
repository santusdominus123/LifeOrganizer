package com.dataapk.lifeorganizer.data.repository

import androidx.lifecycle.LiveData
import com.dataapk.lifeorganizer.data.dao.TaskDao
import com.dataapk.lifeorganizer.data.model.Task
import com.dataapk.lifeorganizer.data.model.Priority
import com.dataapk.lifeorganizer.data.model.Category

class TaskRepository(private val taskDao: TaskDao) {

    fun getAllTasks(): LiveData<List<Task>> = taskDao.getAllTasks()

    suspend fun getAllTasksList(): List<Task> = taskDao.getAllTasksList()

    suspend fun getTaskById(id: Long): Task? = taskDao.getTaskById(id)

    fun getTasksByPriority(priority: Priority): LiveData<List<Task>> =
        taskDao.getTasksByPriority(priority)

    fun getTasksByCategory(category: Category): LiveData<List<Task>> =
        taskDao.getTasksByCategory(category)

    fun getTasksByStatus(isCompleted: Boolean): LiveData<List<Task>> =
        taskDao.getTasksByStatus(isCompleted)

    suspend fun getPendingTasks(): List<Task> = taskDao.getPendingTasks()

    suspend fun getCompletedTasks(): List<Task> = taskDao.getCompletedTasks()

    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun deleteTaskById(id: Long) = taskDao.deleteTaskById(id)

    suspend fun updateTaskStatus(id: Long, isCompleted: Boolean) =
        taskDao.updateTaskStatus(id, isCompleted)

    fun getTaskCount(): LiveData<Int> = taskDao.getTaskCount()

    fun getPendingTaskCount(): LiveData<Int> = taskDao.getPendingTaskCount()

    fun getCompletedTaskCount(): LiveData<Int> = taskDao.getCompletedTaskCount()

    // Synchronous versions for statistics
    suspend fun getTaskCountSync(): Int = taskDao.getTaskCountSync()

    suspend fun getCompletedTaskCountSync(): Int = taskDao.getCompletedTaskCountSync()

    suspend fun getPendingTaskCountSync(): Int = taskDao.getPendingTaskCountSync()

    suspend fun getSuccessRate(): Int {
        val totalTasks = getTaskCountSync()
        if (totalTasks == 0) return 0
        val completedTasks = getCompletedTaskCountSync()
        return (completedTasks * 100) / totalTasks
    }
}