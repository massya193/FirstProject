package com.example.myfinancefinalproject.data.database

import android.content.Context
import androidx.room.Room

object DatabaseProvider { // одна база на приложение(чтобы не создавалось больше)
    private var database: MainDataBase? = null //приват переменная с ссылкой на базу(null==изначально ее нет)

    fun getDatabase(context: Context): MainDataBase { //функция через которую мы будем получать базу с любого места
        if (database == null) {
                database=Room.databaseBuilder(
                context.applicationContext,
                MainDataBase::class.java,
                    "finance_database"
            )
                .fallbackToDestructiveMigration() // если поменял версию без имиграции(перенос старых данных на новую)пересоздать базу
                .build()
        }
        return database!!
    }
    fun closeDatabase(){
        database=null
    }
}