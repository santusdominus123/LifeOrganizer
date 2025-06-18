package com.dataapk.lifeorganizer

object TaskManager {
    private val tasks = mutableListOf<Task>()
    private var taskChangeListener: ((List<Task>) -> Unit)? = null

    fun setTaskChangeListener(listener: ((List<Task>) -> Unit)?) {
        taskChangeListener = listener
    }

    fun addTask(task: Task) {
        tasks.add(task)
        taskChangeListener?.invoke(tasks)
    }

    fun updateTask(id: String, updatedTask: Task) {
        val index = tasks.indexOfFirst { it.id == id }
        if (index != -1) {
            tasks[index] = updatedTask
            taskChangeListener?.invoke(tasks)
        }
    }

    fun deleteTask(id: String) {
        tasks.removeIf { it.id == id }
        taskChangeListener?.invoke(tasks)
    }

    fun toggleTaskCompletion(id: String) {
        val index = tasks.indexOfFirst { it.id == id }
        if (index != -1) {
            val task = tasks[index]
            tasks[index] = task.copy(isCompleted = !task.isCompleted)
            taskChangeListener?.invoke(tasks)
        }
    }

    fun getAllTasks(): List<Task> = tasks.toList()
    fun getCompletedTasks(): List<Task> = tasks.filter { it.isCompleted }
    fun getPendingTasks(): List<Task> = tasks.filter { !it.isCompleted }
    fun getTasksByCategory(category: Task.Category): List<Task> = tasks.filter { it.category == category }
    fun getTasksByPriority(priority: Task.Priority): List<Task> = tasks.filter { it.priority == priority }

    fun getCompletedTaskCount(): Int = tasks.count { it.isCompleted }
    fun getTaskCount(): Int = tasks.size
    fun getPendingTaskCount(): Int = tasks.size - getCompletedTaskCount()
    fun getSuccessRate(): Int = if (tasks.isEmpty()) 0 else (getCompletedTaskCount() * 100 / tasks.size)

    fun updateTasksFromFirebase(firebaseTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(firebaseTasks)
        taskChangeListener?.invoke(tasks)
    }
}