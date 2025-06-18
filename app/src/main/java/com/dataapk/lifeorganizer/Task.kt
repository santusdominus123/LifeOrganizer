package com.dataapk.lifeorganizer

import java.io.Serializable
import java.util.*

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val priority: Priority,
    val category: Category,
    val deadline: String,
    var isCompleted: Boolean = false
) : Serializable {
    enum class Priority { HIGH, MEDIUM, LOW }
    enum class Category { WORK, STUDY, PERSONAL, HEALTH }
}