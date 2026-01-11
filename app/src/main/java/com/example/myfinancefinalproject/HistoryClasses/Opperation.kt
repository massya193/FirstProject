package com.example.myfinancefinalproject.HistoryClasses

import kotlin.text.CharCategory

data class Operation(
    val id: Int,
    val amount: Double,
    val category: String,
    val type: OperationType,
    val comment: String?,
    val date: Long
)
enum class OperationType {
    INCOME, EXPENSE
}
