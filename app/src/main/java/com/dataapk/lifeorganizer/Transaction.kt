// Transaction.kt
package com.dataapk.lifeorganizer.model

import java.util.Date

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val type: TransactionType,
    val category: TransactionCategory,
    val description: String = "",
    val date: Date = Date(),
    val createdAt: Date = Date()
)

enum class TransactionType(val displayName: String) {
    INCOME("Pemasukan"),
    EXPENSE("Pengeluaran")
}

enum class TransactionCategory(val displayName: String, val color: String, val icon: String, val type: TransactionType) {
    // Income categories
    SALARY("Gaji", "#28A745", "ic_salary", TransactionType.INCOME),
    FREELANCE("Freelance", "#17A2B8", "ic_freelance", TransactionType.INCOME),
    BUSINESS("Bisnis", "#6F42C1", "ic_business", TransactionType.INCOME),
    INVESTMENT("Investasi", "#20C997", "ic_investment", TransactionType.INCOME),
    OTHER_INCOME("Lainnya", "#6C757D", "ic_other", TransactionType.INCOME),

    // Expense categories
    FOOD("Makanan", "#FD7E14", "ic_food", TransactionType.EXPENSE),
    TRANSPORT("Transport", "#007BFF", "ic_transport", TransactionType.EXPENSE),
    ENTERTAINMENT("Hiburan", "#E83E8C", "ic_entertainment", TransactionType.EXPENSE),
    SHOPPING("Belanja", "#DC3545", "ic_shopping", TransactionType.EXPENSE),
    BILLS("Tagihan", "#FFC107", "ic_bills", TransactionType.EXPENSE),
    OTHER_EXPENSE("Lainnya", "#6C757D", "ic_other", TransactionType.EXPENSE)
}