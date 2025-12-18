package com.example.myfinancefinalproject

import android.content.Context

object UserPreferences {
    private const val PREFS_NAME="user_prefs"
    private const val KEY_NICKNAME="nickname"

    fun saveCurrentUser(context: Context, nickname: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_NICKNAME, nickname).apply()
    }
    fun getCurrentUser(context: Context): String? {
        val prefs=context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE)
        return prefs.getString(KEY_NICKNAME,null)
    }
    fun clearCurrentUser(context: Context){
        val prefs=context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_NICKNAME).apply()
    }
}
