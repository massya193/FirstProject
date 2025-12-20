package com.example.myfinancefinalproject.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myfinancefinalproject.data.entity.User
import com.example.myfinancefinalproject.data.repository.FinanceRepository
import com.example.myfinancefinalproject.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepository,
    private val financeRepository: FinanceRepository
) : ViewModel() {

    // ================= CURRENT USER =================

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    // ================= LOGIN =================

    fun login(nickname: String, password: String, onResult: (Int?) -> Unit) {
        viewModelScope.launch {
            val savedPassword = userRepository.getPassword(nickname)

            if (savedPassword != null && savedPassword == password) {
                val userId = userRepository.getUserIdByNickname(nickname)
                if (userId != null) {
                    _user.postValue(userRepository.getUserById(userId))
                    onResult(userId)   // ✅ отдаём ID напрямую
                } else {
                    onResult(null)
                }
            } else {
                onResult(null)
            }
        }
    }

    // ================= REGISTER =================

    fun register(user: User, onResult: (Long) -> Unit) {
        viewModelScope.launch {
            val id = userRepository.insertUser(user)
            financeRepository.createBalanceForUser(id.toInt())
            onResult(id)
        }
    }

    // ================= USER DATA =================

    fun loadUserById(userId: Int) {
        viewModelScope.launch {
            _user.postValue(userRepository.getUserById(userId))
        }
    }

    fun updateAvatar(path: String) {
        _user.value?.let { current ->
            viewModelScope.launch {
                userRepository.updateUserAvatar(current.userId, path)
                _user.postValue(current.copy(avatarPath = path))
            }
        }
    }

    fun updatePassword(newPassword: String) {
        _user.value?.let { current ->
            viewModelScope.launch {
                userRepository.updatePassword(current.userId, newPassword)
            }
        }
    }

    fun userExists(userId: Int, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            callback(userRepository.userExists(userId))
        }
    }

}
