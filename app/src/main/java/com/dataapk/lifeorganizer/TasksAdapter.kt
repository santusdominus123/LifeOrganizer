package com.dataapk.lifeorganizer

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox

class TasksAdapter(
    private var tasks: MutableList<Task>,
    private val onTaskToggle: (Task) -> Unit,
    private val onTaskEdit: (Task) -> Unit,
    private val onTaskDelete: (Task) -> Unit
) : RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkboxTask: MaterialCheckBox = itemView.findViewById(R.id.checkboxTask)
        val tvTaskTitle: TextView = itemView.findViewById(R.id.tvTaskTitle)
        val tvTaskDescription: TextView = itemView.findViewById(R.id.tvTaskDescription)
        val tvTaskDeadline: TextView = itemView.findViewById(R.id.tvTaskDeadline)
        val tvTaskCategory: TextView = itemView.findViewById(R.id.tvTaskCategory)
        val btnEditTask: MaterialButton = itemView.findViewById(R.id.btnEditTask)
        val btnDeleteTask: MaterialButton = itemView.findViewById(R.id.btnDeleteTask)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        val context = holder.itemView.context

        // Bind task data
        holder.tvTaskTitle.text = task.title
        holder.tvTaskDescription.text = if (task.description.isNotEmpty()) {
            task.description
        } else {
            "No description"
        }
        holder.tvTaskDeadline.text = "Due: ${task.deadline}"
        holder.tvTaskCategory.text = task.category.name
        holder.checkboxTask.isChecked = task.isCompleted

        // Set priority indicator color
        val priorityColor = when (task.priority) {
            Task.Priority.HIGH -> Color.RED
            Task.Priority.MEDIUM -> Color.parseColor("#FFA500") // Orange
            Task.Priority.LOW -> Color.GREEN
        }
        
    }

    override fun getItemCount(): Int = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }

    fun addTask(task: Task) {
        tasks.add(0, task) // Add to top
        notifyItemInserted(0)
    }

    fun removeTask(position: Int) {
        if (position in 0 until tasks.size) {
            tasks.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, tasks.size)
        }
    }

    fun updateTask(position: Int, updatedTask: Task) {
        if (position in 0 until tasks.size) {
            tasks[position] = updatedTask
            notifyItemChanged(position)
        }
    }

    fun getTaskAt(position: Int): Task? {
        return if (position in 0 until tasks.size) tasks[position] else null
    }

    // Filter methods
    fun filterByCategory(category: Task.Category?) {
        val filteredTasks = if (category == null) {
            TaskManager.getAllTasks()
        } else {
            TaskManager.getTasksByCategory(category)
        }
        updateTasks(filteredTasks)
    }

    fun filterByPriority(priority: Task.Priority?) {
        val filteredTasks = if (priority == null) {
            TaskManager.getAllTasks()
        } else {
            TaskManager.getTasksByPriority(priority)
        }
        updateTasks(filteredTasks)
    }

    fun filterByCompletion(showCompleted: Boolean?) {
        val filteredTasks = when (showCompleted) {
            true -> TaskManager.getCompletedTasks()
            false -> TaskManager.getPendingTasks()
            null -> TaskManager.getAllTasks()
        }
        updateTasks(filteredTasks)
    }

    fun sortByPriority() {
        tasks.sortBy {
            when(it.priority) {
                Task.Priority.HIGH -> 1
                Task.Priority.MEDIUM -> 2
                Task.Priority.LOW -> 3
            }
        }
        notifyDataSetChanged()
    }

    fun sortByDeadline() {
        tasks.sortBy { it.deadline }
        notifyDataSetChanged()
    }

    fun sortByTitle() {
        tasks.sortBy { it.title.lowercase() }
        notifyDataSetChanged()
    }
}