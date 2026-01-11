package com.example.myfinancefinalproject.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.myfinancefinalproject.data.dto.BalanceEvent
import com.example.myfinancefinalproject.data.entity.Balance
import kotlinx.coroutines.flow.Flow

@Dao
    interface BalanceDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBalance(balance: Balance)
    @Query("SELECT * FROM balance WHERE userId = :userId LIMIT 1")
    suspend fun getBalance(userId: Int): Balance?
    @Query("SELECT * FROM balance WHERE userid = :userId LIMIT 1")//Запрос баланса,лимит 1-получение одного результата
    suspend fun getBalanceById(userId: Int): Balance?

    @Query("UPDATE balance SET total= :newAmount WHERE userId= :userId")//обновить баланс
    suspend fun updateBalance(userId:Int,newAmount:Double)

    @Query("SELECT total FROM balance WHERE userId = :userId")
    fun observeBalance(userId: Int): Flow<Double?>

    @Query("SELECT date AS date, amount AS delta FROM income WHERE userId = :userId AND date BETWEEN :from AND :to UNION ALL SELECT date AS date, -amount AS delta FROM expenses WHERE userId = :userId AND date BETWEEN :from AND :to ORDER BY date ASC ")
    fun observeBalanceEvents(userId: Long, from: Long, to: Long): Flow<List<BalanceEvent>>

}