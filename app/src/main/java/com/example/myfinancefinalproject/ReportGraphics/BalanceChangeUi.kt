package com.example.myfinancefinalproject.ReportGraphics

data class BalanceChangeUi(
    val date: Long,
    val delta: Double,
    val balanceAfter: Double
)