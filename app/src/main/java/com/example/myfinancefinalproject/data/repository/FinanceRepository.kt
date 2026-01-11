package com.example.myfinancefinalproject.data.repository

import com.example.myfinancefinalproject.HistoryClasses.Operation
import com.example.myfinancefinalproject.HistoryClasses.OperationType
import com.example.myfinancefinalproject.data.dao.BalanceDao
import com.example.myfinancefinalproject.data.dao.ExpenseDao
import com.example.myfinancefinalproject.data.dao.IncomeDao
import com.example.myfinancefinalproject.data.entity.Balance
import com.example.myfinancefinalproject.data.entity.Expense
import com.example.myfinancefinalproject.data.entity.Income
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

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

    fun observeBalance(userId: Int) = balanceDao.observeBalance(userId)

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

    fun observeBalanceEvents(
        userId: Long,
        from: Long,
        to: Long
    ) = balanceDao.observeBalanceEvents(userId, from, to)
    // ================= INCOME =================

    suspend fun insertIncome(income: Income) {
        incomeDao.insertIncome(income)
    }

    fun getIncome(userId: Int): Flow<List<Income>> {
        return incomeDao.getIncome(userId)
    }

    fun observeIncomeCategories(userId: Int): Flow<List<String>> =
        incomeDao.observeCategories(userId)

    fun observeIncomeSum()=incomeDao.observeTotalIncome()


    // ================= EXPENSE =================

    suspend fun insertExpense(expense: Expense) {
        expenseDao.insertExpense(expense)
    }
    fun observeExpenseCategories(userId: Int): Flow<List<String>> =
        expenseDao.observeCategories(userId)
    fun observeExpenseSum()=expenseDao.observeTotalExpense()
    // ================= COMBINE =================

    fun getExpenses(userId: Int): Flow<List<Expense>> {
        return expenseDao.getExpenses(userId)
    }
    suspend fun getExpensesByCategory(category: String): List<Expense> {
        return expenseDao.getExpensesByCategory(category)
    }
    fun observeHistory(): kotlinx.coroutines.flow.Flow<List<Operation>> {
        return kotlinx.coroutines.flow.combine(
            incomeDao.observeIncome(),
            expenseDao.observeExpense()
        ) { incomeList, expenseList ->

            val incomeOps = incomeList.map {
                Operation(
                    id = it.id,
                    amount = it.amount,
                    category =it.category,
                    type = OperationType.INCOME,
                    comment = it.description,
                    date = it.date
                )
            }

            val expenseOps = expenseList.map {
                Operation(
                    id = it.id,
                    amount = it.amount,
                    category =it.category,
                    type = OperationType.EXPENSE,
                    comment = it.description,
                    date = it.date
                )
            }

            (incomeOps + expenseOps).sortedByDescending { it.date }
        }
    }

}
