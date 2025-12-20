package com.example.myfinancefinalproject.data.repository

import androidx.lifecycle.LiveData
import com.example.myfinancefinalproject.data.dao.BalanceDao
import com.example.myfinancefinalproject.data.dao.ExpenseDao
import com.example.myfinancefinalproject.data.dao.IncomeDao
import com.example.myfinancefinalproject.data.entity.Balance
import com.example.myfinancefinalproject.data.entity.Expense
import com.example.myfinancefinalproject.data.entity.Income
import kotlinx.coroutines.flow.Flow

class FinanceRepository(
    private val balanceDao: BalanceDao,
    private val expenseDao: ExpenseDao,
    private val incomeDao: IncomeDao
) {

    // ================= BALANCE =================

    suspend fun ensureBalanceExists(userId: Int) {
        val existing = balanceDao.getBalanceById(userId)
        if (existing == null) {
            balanceDao.insertBalance(
                Balance(userId = userId, total = 0.0)
            )
        }
    }

    suspend fun getBalance(userId: Int): Balance? {
        return balanceDao.getBalanceById(userId)
    }

    suspend fun updateBalance(userId: Int, newAmount: Double) {
        ensureBalanceExists(userId)
        balanceDao.updateBalance(userId, newAmount)
    }

    fun observeBalance(userId: Int): LiveData<Double> {
        return balanceDao.observeBalance(userId)
    }

    suspend fun createBalanceForUser(userId: Int) {
        val existing = balanceDao.getBalanceById(userId)
        if (existing == null) {
            balanceDao.insertBalance(
                Balance(
                    userId = userId,
                    total = 0.0
                )
            )
        }
    }

    // ================= INCOME =================

    suspend fun insertIncome(income: Income) {
        incomeDao.insertIncome(income)
    }

    fun getIncome(userId: Int): Flow<List<Income>> {
        return incomeDao.getIncome(userId)
    }

    // ================= EXPENSE =================

    suspend fun insertExpense(expense: Expense) {
        expenseDao.insertExpense(expense)
    }

    fun getExpenses(userId: Int): Flow<List<Expense>> {
        return expenseDao.getExpenses(userId)
    }
}
