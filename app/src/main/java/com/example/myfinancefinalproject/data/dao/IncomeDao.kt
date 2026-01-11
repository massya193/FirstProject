package com.example.myfinancefinalproject.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.myfinancefinalproject.data.entity.Balance
import com.example.myfinancefinalproject.data.entity.Income
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao{
    @Insert
    suspend fun insertIncome(income: Income)

    @Query("SELECT * FROM income ORDER BY date DESC")
    fun observeIncome(): kotlinx.coroutines.flow.Flow<List<Income>>

    @Query("SELECT * FROM income WHERE userId = :userId")
    fun getIncome(userId: Int): Flow<List<Income>>
    @Query("SELECT * FROM income WHERE id = :userId LIMIT 1")//Запрос прибыли,лимит 1-получение одного результата
    suspend fun getIncomeById(userId: Int): Income?
    @Query("SELECT IFNULL(SUM(amount), 0) FROM income")
    fun observeTotalIncome(): kotlinx.coroutines.flow.Flow<Double>
    @Query( "SELECT DISTINCT category FROM income WHERE userId = :userId AND category IS NOT NULL AND category != '' ORDER BY category ")
    fun observeCategories(userId: Int): kotlinx.coroutines.flow.Flow<List<String>>
    @Update
    suspend fun updateIncome(income: Income)

    @Delete
    suspend fun deleteIncome(income: Income)

}
