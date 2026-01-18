package com.example.myfinancefinalproject.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.myfinancefinalproject.data.dto.ExpenseDaySum
import com.example.myfinancefinalproject.data.entity.Balance
import com.example.myfinancefinalproject.data.entity.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert //вставить запись в таблицу expense
    suspend fun insertExpense(expense: Expense) // suspend-функция вызывается параллельно программе

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun observeExpense(): kotlinx.coroutines.flow.Flow<List<Expense>>
    @Query("SELECT * FROM expenses WHERE userId = :userId")
    fun getExpenses(userId: Int): Flow<List<Expense>>
    @Query("SELECT * FROM expenses WHERE category= :category")// выбрать категорию
    suspend fun getExpensesByCategory(category:String):List<Expense>
    @Query("SELECT IFNULL(SUM(amount), 0) FROM expenses")
    fun observeTotalExpense(): kotlinx.coroutines.flow.Flow<Double>
    @Query( "SELECT DISTINCT category FROM expenses WHERE userId = :userId AND category IS NOT NULL AND category != '' ORDER BY category ")
    fun observeCategories(userId: Int): kotlinx.coroutines.flow.Flow<List<String>>

    @Query("SELECT (date / 86400000) * 86400000 AS dayStart,SUM(amount) AS total FROM expenses WHERE userId = :userId AND date BETWEEN :from AND :to GROUP BY dayStart ORDER BY dayStart ASC")
    fun observeExpenseByDay(userId: Long, from: Long, to: Long): kotlinx.coroutines.flow.Flow<List<ExpenseDaySum>>

    @Query("SELECT * FROM expenses WHERE userId = :userId AND date BETWEEN :from AND :to ORDER BY date DESC ")
    fun observeExpenseTransactions(userId: Long, from: Long, to: Long): kotlinx.coroutines.flow.Flow<List<Expense>>

    @Update//обновить запись по id(внутри expence)
    suspend fun updateExpense(expense: Expense)

    @Delete//удалить запись по id(внутри expence)
    suspend fun deleteExpense(expense: Expense)

}