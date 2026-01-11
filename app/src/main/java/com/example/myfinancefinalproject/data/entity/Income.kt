package com.example.myfinancefinalproject.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(tableName = "income")
data class Income(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val description: String?,
    val category: String,
    val amount: Double,
    val date: Long
)
