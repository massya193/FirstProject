package com.example.myfinancefinalproject.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.myfinancefinalproject.data.entity.Balance
import com.example.myfinancefinalproject.data.entity.Income

@Dao
interface IncomeDao{
    @Insert
    suspend fun insertIncome(income: Income)


    @Query("SELECT SUM(amount) FROM income WHERE userId = :userId")
    fun getTotalIncome(userId: Int): LiveData<Double?>

    @Query("SELECT * FROM income WHERE id = :userId LIMIT 1")//Запрос прибыли,лимит 1-получение одного результата
    suspend fun getIncomeById(userId: Int): Income?
    @Update
    suspend fun updateIncome(income: Income)

    @Delete
    suspend fun deleteIncome(income: Income)

}
