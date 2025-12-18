package com.example.myfinancefinalproject.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myfinancefinalproject.data.dao.BalanceDao
import com.example.myfinancefinalproject.data.dao.ExpenseDao
import com.example.myfinancefinalproject.data.dao.IncomeDao
import com.example.myfinancefinalproject.data.dao.UserDao
import com.example.myfinancefinalproject.data.entity.Expense
import com.example.myfinancefinalproject.data.entity.Balance
import com.example.myfinancefinalproject.data.entity.Income
import com.example.myfinancefinalproject.data.entity.User

@Database(
    entities = [Expense::class, Balance::class,Income::class, User::class], //с какой таблицей будем работать
    version = 13, // версия базы(если меняю что то в таблице нужно менять версию)
    exportSchema = false // для создания json файла физически для просмотра
)
abstract class MainDataBase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao //указание на какое dao ссылаться
    abstract fun balanceDao(): BalanceDao //указание на какое dao ссылаться
    abstract fun incomeDao(): IncomeDao //указание на какое dao ссылаться
    abstract fun userDao(): UserDao //указание на какое dao ссылаться
}
