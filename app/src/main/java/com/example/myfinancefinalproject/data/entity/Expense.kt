package com.example.myfinancefinalproject.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.ColumnInfo

@Entity(tableName = "expenses")//говорит room что этот класс надо превратить в базу данны
data class Expense(
    @PrimaryKey(autoGenerate = true) //даем классу уникальный номер,и меняем его по ходу записи
    val id:Int=0,
    val userId: Int,
    val category: String,
    val amount: Double,
    val date: String,
)