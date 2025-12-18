package com.example.myfinancefinalproject.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "balance")
data class Balance(
    @PrimaryKey val userId: Int,// id пользователя
    val total: Double// текущая сумма баланса
)
