package com.example.myfinancefinalproject.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.liveData
import com.example.myfinancefinalproject.data.entity.Balance
import com.example.myfinancefinalproject.data.entity.Expense
import com.example.myfinancefinalproject.data.entity.Income
import com.example.myfinancefinalproject.data.entity.User
import com.example.myfinancefinalproject.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn


import kotlinx.coroutines.launch


class FinanceViewModel(
    private val repository: FinanceRepository
) : ViewModel() {

    // ================= USER ID =================

    private val _userId = MutableStateFlow<Int?>(null)

    fun setUserId(id: Int) {
        _userId.value = id
    }

    // ================= BALANCE =================

    val balance: LiveData<Double> = liveData {
        _userId.filterNotNull().collect { id ->
            repository.ensureBalanceExists(id)
            emit(repository.getBalance(id)?.total ?: 0.0)
        }
    }

    fun updateBalance(delta: Double) {
        val id = _userId.value ?: return
        viewModelScope.launch {
            val current = repository.getBalance(id)?.total ?: 0.0
            val newAmount = current + delta
            repository.updateBalance(id, newAmount)
        }
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

    fun addIncome(amount: Double, date: String) {
        val id = _userId.value ?: return
        viewModelScope.launch {
            repository.insertIncome(
                Income(
                    userId = id,
                    amount = amount,
                    date = date
                )
            )
        }
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

    fun addExpense(amount: Double, category: String, date: String) {
        val id = _userId.value ?: return
        viewModelScope.launch {
            repository.insertExpense(
                Expense(
                    userId = id,
                    category = category,
                    amount = amount,
                    date = date
                )
            )
        }
    }
}

