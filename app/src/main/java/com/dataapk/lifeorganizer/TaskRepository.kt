package com.dataapk.lifeorganizer

import android.content.Context

class TaskRepository(private val context: Context) {
    fun createTask(task: Task) {
        TaskManager.addTask(task)
    }

    fun updateTask(updatedTask: Task) {
        TaskManager.updateTask(updatedTask.id, updatedTask)
    }

    fun getTaskById(taskId: String): Task? {
        return TaskManager.getAllTasks().find { it.id == taskId }
    }
}