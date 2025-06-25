package com.dataapk.lifeorganizer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val priority: Priority = Priority.MEDIUM,
    val category: Category = Category.PERSONAL,
    val isCompleted: Boolean = false,
    val deadline: Date? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class Priority {
    HIGH, MEDIUM, LOW
}

enum class Category {
    PERSONAL, WORK, HEALTH, EDUCATION, SHOPPING, OTHER
}