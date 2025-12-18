package com.example.myfinancefinalproject.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.myfinancefinalproject.data.entity.User

@Dao
interface UserDao{

    @Insert
    suspend fun insertUser(user: User): Long
    @Query("SELECT COUNT(*) FROM user WHERE userId = :userId")
    suspend fun userExists(userId: Int): Int

    @Query("SELECT password FROM user WHERE nickname = :nickname LIMIT 1")
    suspend fun getPassword(nickname: String): String?
    @Query("SELECT nickname FROM user WHERE userId = :userId LIMIT 1")
    suspend fun getNickname(userId: Int): String?

    @Query("SELECT COUNT(*) FROM user WHERE nickname = :nickname")
    suspend fun nicknameExists(nickname: String): Int

    @Query("UPDATE user SET avatarPath = :path WHERE userId = :userId")
    suspend fun updateAvatar(userId: Int, path: String)

    @Query("UPDATE user SET password = :newPassword WHERE userId = :userId")
    suspend fun updatePassword(userId: Int, newPassword: String)
    @Query("SELECT * FROM user")
    suspend fun getAllUsers(): List<User>
    @Query("SELECT userId FROM user WHERE nickname = :nickname LIMIT 1")
    suspend fun getUserIdByNickname(nickname: String): Int?


}