package com.example.myfinancefinalproject.data.repository

import com.example.myfinancefinalproject.data.dao.UserDao
import com.example.myfinancefinalproject.data.entity.User

class UserRepository(
    private val userDao: UserDao
) {

    suspend fun insertUser(user: User): Long {
        return userDao.insertUser(user)
    }
    suspend fun userExists(userId: Int): Boolean {
        return userDao.userExists(userId) > 0
    }
    suspend fun getUserById(id: Int): User? {
        return userDao.getUserById(id)
    }
    suspend fun getUserNickname(userId: Int): String? = userDao.getUserNickname(userId)
    suspend fun getUserIdByNickname(nickname: String): Int? {
        return userDao.getUserIdByNickname(nickname)
    }

    suspend fun getPassword(nickname: String): String? {
        return userDao.getPassword(nickname)
    }

    suspend fun updateUserAvatar(id: Int, path: String) {
        userDao.updateUserAvatar(id, path)
    }

    suspend fun updatePassword(id: Int, newPassword: String) {
        userDao.updatePassword(id, newPassword)
    }
}
