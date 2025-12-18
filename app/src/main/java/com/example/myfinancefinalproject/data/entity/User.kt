package com.example.myfinancefinalproject.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,
    val password: String,
    val nickname: String,
    val avatarPath: String?
)
