package com.example.myfinancefinalproject.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.myfinancefinalproject.data.dao.BalanceDao
import com.example.myfinancefinalproject.data.entity.Balance
import com.example.myfinancefinalproject.data.dao.ExpenseDao
import com.example.myfinancefinalproject.data.dao.IncomeDao
import com.example.myfinancefinalproject.data.dao.UserDao
import com.example.myfinancefinalproject.data.entity.Expense
import com.example.myfinancefinalproject.data.entity.Income
import com.example.myfinancefinalproject.data.entity.User
import com.example.myfinancefinalproject.data.repository.FinanceRepository

class FinanceRepository(private val balancedao: BalanceDao,
    private val expenseDao: ExpenseDao,
    private val incomeDao: IncomeDao,
    private val userdao: UserDao
){
    //ПОЛЬЗОВАТЕЛЬ
    suspend fun userExists(userId: Int): Boolean {
        return userdao.userExists(userId) > 0
    }
    suspend fun insertUser(user: User): Long {
        return userdao.insertUser(user)
    }
    suspend fun getPassword(nickname: String): String? {
        return userdao.getPassword(nickname)
    }
    suspend fun nicknameExists(nickname: String): Boolean {
        return userdao.nicknameExists(nickname) > 0
    }
    suspend fun getUserIdByNickname(nickname: String): Int? {
        return userdao.getUserIdByNickname(nickname)
    }
    suspend fun getnickname(userId: Int): String? {
        return userdao.getNickname(userId)
    }
    suspend fun updateUserAvatar(id: Int, path: String) =
        userdao.updateAvatar(id, path)
    suspend fun updatePassword(id: Int, newPassword: String) =
        userdao.updatePassword(id, newPassword)
    //БАЛАНС
    suspend fun createUserWithBalance(user: User) {
        val existingBalance = balancedao.getBalance(user.userId)
        if (existingBalance == null) {
            balancedao.insertBalance(Balance(userId = user.userId, total = 0.0))
        }
        userdao.insertUser(user)
    }

    suspend fun getBalance(userId: Int): Balance? {
        return balancedao.getBalanceById(userId)
    }
    suspend fun insertBalance(balance: Balance) {
        balancedao.insertBalance(balance)
    }
    fun observeBalance(userId: Int): LiveData<Double> {
        return balancedao.observeBalance(userId)
    }
    suspend fun updateBalance(id: Int, newAmount: Double) {
        ensureBalanceExists(id)
        balancedao.updateBalance(id, newAmount)
    }
    suspend fun ensureBalanceExists(userId: Int) {
        val existing = balancedao.getBalanceById(userId)
        if (existing == null) {
            balancedao.insertBalance(Balance(userId = userId, total = 0.0))
        }
    }


    //РАСХОДЫ
    suspend fun insertExpense(expense: Expense) =
        expenseDao.insertExpense(expense)
    fun getTotalExpense(userId: Int): LiveData<Double?> = expenseDao.getTotalExpense(userId)


    //ДОХОДЫ
    suspend fun insertIncome(income: Income) =
        incomeDao.insertIncome(income)
    fun getTotalIncome(userId: Int): LiveData<Double?> = incomeDao.getTotalIncome(userId)
    suspend fun ensureIncomeExists(userId: Int) {
        val existing = incomeDao.getIncomeById(userId)
        if (existing == null) {
            incomeDao.insertIncome(Income(id = userId,userId=userId, amount = 0.0, date = ""))
        }
    }
}

