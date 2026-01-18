package com.example.myfinancefinalproject.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.myfinancefinalproject.data.entity.User

@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM user WHERE userId = :id")
    suspend fun getUserById(id: Int): User?

    @Query("SELECT COUNT(*) FROM user WHERE userId = :userId")
    suspend fun userExists(userId: Int): Int

    @Query("SELECT nickname FROM user WHERE userId = :userId LIMIT 1")
    suspend fun getUserNickname(userId: Int): String?
    @Query("SELECT userId FROM user WHERE nickname = :nickname LIMIT 1")
    suspend fun getUserIdByNickname(nickname: String): Int?

    @Query("SELECT password FROM user WHERE nickname = :nickname LIMIT 1")
    suspend fun getPassword(nickname: String): String?

    @Query("UPDATE user SET avatarPath = :path WHERE userId = :id")
    suspend fun updateUserAvatar(id: Int, path: String)

    @Query("UPDATE user SET password = :password WHERE userId = :id")
    suspend fun updatePassword(id: Int, password: String)
}
