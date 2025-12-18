package com.example.myfinancefinalproject.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.myfinancefinalproject.data.entity.Balance
import com.example.myfinancefinalproject.data.entity.Expense
@Dao
interface ExpenseDao {
    @Insert //вставить запись в таблицу expense
    suspend fun insertExpense(expense: Expense) // suspend-функция вызывается параллельно программе

    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId")
    fun getTotalExpense(userId: Int): LiveData<Double?>

    @Query("SELECT * FROM expenses WHERE category= :category")// выбрать категорию
    suspend fun getExpensesByCategory(category:String):List<Expense>

    @Update//обновить запись по id(внутри expence)
    suspend fun updateExpense(expense: Expense)

    @Delete//удалить запись по id(внутри expence)
    suspend fun deleteExpense(expense: Expense)

}