package com.example.moneymoney_room.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Database class with a singleton Instance object.
 */
@Database(entities = [Item::class, Configuration::class, BudgetItem::class], version = 6, exportSchema = false)
abstract class MoneyMoneyDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao
    abstract fun configurationDao(): ConfigurationDao
    abstract fun budgetItemDao(): BudgetItemDao

    companion object {
        @Volatile
        private var instance: MoneyMoneyDatabase? = null

        fun getDatabase(context: Context): MoneyMoneyDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    MoneyMoneyDatabase::class.java,
                    "money_money_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                instance = newInstance
                newInstance
            }
        }
    }
}