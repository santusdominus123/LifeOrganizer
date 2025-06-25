package com.dataapk.lifeorganizer.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dataapk.lifeorganizer.R
import com.dataapk.lifeorganizer.data.model.Task
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private val onTaskClick: (Task) -> Unit,
    private val onTaskStatusChanged: (Task, Boolean) -> Unit,
    private val onTaskDelete: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTaskTitle)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvTaskDescription)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private val tvPriority: TextView = itemView.findViewById(R.id.tvPriority)
        private val tvDeadline: TextView = itemView.findViewById(R.id.tvDeadline)
        private val cbCompleted: CheckBox = itemView.findViewById(R.id.cbTaskStatus)
        private val btnDelete: View = itemView.findViewById(R.id.ivDelete )

        fun bind(task: Task) {
            tvTitle.text = task.title
            tvDescription.text = task.description ?: "No description"
            tvCategory.text = task.category.name.lowercase().replaceFirstChar { it.uppercase() }
            tvPriority.text = task.priority.name

            // Set priority color
            val priorityColor = when (task.priority) {
                com.dataapk.lifeorganizer.data.model.Priority.HIGH ->
                    itemView.context.getColor(R.color.priority_high)
                com.dataapk.lifeorganizer.data.model.Priority.MEDIUM ->
                    itemView.context.getColor(R.color.priority_medium)
                com.dataapk.lifeorganizer.data.model.Priority.LOW ->
                    itemView.context.getColor(R.color.priority_low)
            }
            tvPriority.setTextColor(priorityColor)

            // Format deadline
            task.deadline?.let { deadline ->
                val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                tvDeadline.text = "Due: ${formatter.format(deadline)}"
                tvDeadline.visibility = View.VISIBLE
            } ?: run {
                tvDeadline.visibility = View.GONE
            }

            cbCompleted.isChecked = task.isCompleted

            // Set click listeners
            itemView.setOnClickListener { onTaskClick(task) }

            cbCompleted.setOnCheckedChangeListener { _, isChecked ->
                onTaskStatusChanged(task, isChecked)
            }

            btnDelete.setOnClickListener { onTaskDelete(task) }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}