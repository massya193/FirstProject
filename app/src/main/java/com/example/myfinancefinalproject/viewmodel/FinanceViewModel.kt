package com.example.myfinancefinalproject.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.liveData
import com.example.myfinancefinalproject.data.entity.Balance
import com.example.myfinancefinalproject.data.entity.Expense
import com.example.myfinancefinalproject.data.entity.Income
import com.example.myfinancefinalproject.data.entity.User
import com.example.myfinancefinalproject.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.map
import com.example.myfinancefinalproject.data.dto.BalanceEvent
import com.example.myfinancefinalproject.data.dto.ExpenseDaySum
import com.example.myfinancefinalproject.data.dto.IncomeDaySum
import com.github.mikephil.charting.data.Entry


import kotlinx.coroutines.launch


class FinanceViewModel(
    private val repository: FinanceRepository
) : ViewModel() {
    private val vmTag = "FinanceVM#${System.identityHashCode(this)}"
    // ================= USER ID =================

    private val _userId = MutableStateFlow<Int?>(null)
    val userId = _userId.asStateFlow()

    fun setUserId(id: Int) {
        Log.d("CAT", "$vmTag setUserId=$id")
        _userId.value = id
    }

    // ================= BALANCE =================

    val balance = _userId.filterNotNull().flatMapLatest { id ->
            repository.observeBalance(id).map { it ?: 0.0 }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)


    fun updateBalance(delta: Double) {
        val id = _userId.value ?: return
        viewModelScope.launch {
            val current = repository.getBalance(id)?.total ?: 0.0
            val newAmount = current + delta

            repository.updateBalance(id, newAmount)
        }
    }
    fun observeBalancePoints(fromDate: Long, toDate: Long): LiveData<Pair<List<Entry>, List<Long>>> {
        val uid = (_userId.value ?: 0).toLong()
        return repository.observeBalanceEvents(uid, fromDate, toDate)
            .map { events ->
                var balance = 0.0
                val days = ArrayList<Long>(events.size)
                val entries = ArrayList<Entry>(events.size)

                events.forEachIndexed { index, e ->
                    balance += e.delta
                    days += e.date
                    entries += Entry(index.toFloat(), balance.toFloat()).apply { data=e }
                }
                entries to days
            }
            .asLiveData()
    }
    fun observeBalanceEvents(from: Long, to: Long): LiveData<List<BalanceEvent>> {
        val uid = _userId.value?.toLong() ?: return MutableLiveData(emptyList())
        return repository.observeEventsOfBalance(uid, from, to).asLiveData()
    }
    // ================= INCOME =================

    val income = _userId
        .filterNotNull()
        .flatMapLatest { id ->
            repository.getIncome(id)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun addIncome(amount: Double, category: String, date: Long, description: String?) {
        val id = _userId.value ?: return
        viewModelScope.launch {
            repository.insertIncome(
                Income(
                    userId = id,
                    amount = amount,
                    date = date,
                    category = category,
                    description = description
                )
            )
        }
    }

    val incomeCategories: StateFlow<List<String>> =
        userId.filterNotNull()
            .flatMapLatest { id -> repository.observeIncomeCategories(id) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val incomeCount=repository.observeIncomeSum().asLiveData()

    fun observeIncomeChart(from: Long, to: Long): LiveData<List<IncomeDaySum>> {
        val uid = _userId.value?.toLong() ?: return MutableLiveData(emptyList())
        return repository.observeIncomeByDay(uid, from, to).asLiveData()
    }

    fun observeIncomeTransactions(from: Long, to: Long): LiveData<List<Income>> {
        val uid = _userId.value?.toLong() ?: return MutableLiveData(emptyList())
        return repository.observeIncomeTransactions(uid, from, to).asLiveData()
    }
    // ================= EXPENSES =================

    val expenses = _userId
        .filterNotNull()
        .flatMapLatest { id ->
            repository.getExpenses(id)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun addExpense(amount: Double, category: String, date: Long,description: String?) {
        val id = _userId.value ?: return
        viewModelScope.launch {
            repository.insertExpense(
                Expense(
                    userId = id,
                    category = category,
                    amount = amount,
                    date = date,
                    description = description
                )
            )
        }
    }

    val expenseCategories: StateFlow<List<String>> =
        userId.filterNotNull()
            .flatMapLatest { id -> repository.observeExpenseCategories(id) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenseCount=repository.observeExpenseSum().asLiveData()

    fun observeExpenseChart(from: Long, to: Long): LiveData<List<ExpenseDaySum>> {
        val uid = _userId.value?.toLong() ?: return MutableLiveData(emptyList())
        return repository.observeExpenseByDay(uid, from, to).asLiveData()
    }

    fun observeExpenseTransactions(from: Long, to: Long): LiveData<List<Expense>> {
        val uid = _userId.value?.toLong() ?: return MutableLiveData(emptyList())
        return repository.observeExpenseTransactions(uid, from, to).asLiveData()
    }

    // ================= COMBINE =================
    val history = repository.observeHistory().asLiveData()

}

