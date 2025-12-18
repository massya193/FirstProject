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


import kotlinx.coroutines.launch


class FinanceViewModel(private val repository: FinanceRepository) : ViewModel() {

    private val _balance = MutableLiveData<Double>()
    val balance: LiveData<Double> get() = _balance
    private val _expenses = MutableLiveData<Double>()
    val expenses: LiveData<Double> get() = _expenses

    private val _income = MutableLiveData<Double>()
    val income: LiveData<Double> get() = _income

    //БАЛАНС
    // Загружаем баланс при старте
    fun loadBalance(userId: Int) {
        viewModelScope.launch {
            repository.ensureBalanceExists(userId)
            val balanceEntity = repository.getBalance(userId)?.total ?: 0.0
            _balance.postValue(balanceEntity)
        }
    }

    suspend fun getBalance(userId: Int): Balance? {
        return repository.getBalance(userId)
    }
    fun createUserWithBalance(user: User) {
        viewModelScope.launch {
            repository.createUserWithBalance(user)
        }
    }
    fun observeBalance(userId: Int): LiveData<Double> {
        return repository.observeBalance(userId)
    }

    // Добавляем или вычитаем деньги
    fun updateBalance(userId: Int, delta: Double) {
        viewModelScope.launch {
            val current = repository.getBalance(userId)?.total ?: 0.0
            var newAmount = current + delta
            repository.updateBalance(userId, newAmount)
            _balance.postValue(newAmount)
        }
    }
    fun insertBalance(balance: Balance) {
        viewModelScope.launch {
            repository.insertBalance(balance)
        }
    }


    //РАСХОДЫ
    fun addExpense(userId: Int, amount: Double, category: String, data: String) {
        viewModelScope.launch {
            val expense = Expense(
                userId = userId,
                category = category,
                amount = amount,
                date = data
            )
            repository.insertExpense(expense)
        }
    }

    fun observeExpense(userId: Int) = repository.getTotalExpense(userId)


    //ПОЛЬЗОВАТЕЛЬ
    fun insertUser(user: User, callback: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.insertUser(user)
            callback(id)
        }
    }
    fun getUserIdByNickname(nickname: String, callback: (Int?) -> Unit) {
        viewModelScope.launch {
            callback(repository.getUserIdByNickname(nickname))
        }
    }
    fun userExists(userId: Int, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val exists = repository.userExists(userId)
            callback(exists)
        }
    }
    fun nicknameExists(nickname: String,callback: (Boolean?) -> Unit) {
        viewModelScope.launch {
            val exists= repository.nicknameExists(nickname)
            callback(exists)
        }
    }
    fun getPassword(nickname: String, callback: (String?) -> Unit) {
        viewModelScope.launch {
            val password = repository.getPassword(nickname)
            callback(password)
        }
    }

    fun getNickname(userId: Int, callback: (String?) -> Unit) {
        viewModelScope.launch {
            callback(repository.getnickname(userId))
        }
    }

    fun updateUserAvatar(id: Int, path: String) {
        viewModelScope.launch {
            repository.updateUserAvatar(id, path)
        }
    }

    fun updatePassword(id: Int, newPassword: String) {
        viewModelScope.launch {
            repository.updatePassword(id, newPassword)
        }
    }

    //ДОХОДЫ
    fun addIncome(userId: Int, amount: Double, data: String) {
        viewModelScope.launch {
            val income = Income(
                userId = userId,
                amount = amount,
                date = data,
            )
            repository.insertIncome(income)
        }
    }

    fun observeIncome(userId: Int) = repository.getTotalIncome(userId)
}
