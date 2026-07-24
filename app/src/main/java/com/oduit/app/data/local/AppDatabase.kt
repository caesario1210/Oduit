package com.oduit.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.oduit.app.data.local.dao.AccountDao
import com.oduit.app.data.local.dao.BudgetDao
import com.oduit.app.data.local.dao.CategoryDao
import com.oduit.app.data.local.dao.SavingsGoalDao
import com.oduit.app.data.local.dao.TransactionDao
import com.oduit.app.data.local.entity.Account
import com.oduit.app.data.local.entity.Budget
import com.oduit.app.data.local.entity.Category
import com.oduit.app.data.local.entity.SavingsContribution
import com.oduit.app.data.local.entity.SavingsGoal
import com.oduit.app.data.local.entity.Transaction

@Database(
    entities = [
        Account::class,
        Category::class,
        Transaction::class,
        Budget::class,
        SavingsGoal::class,
        SavingsContribution::class,
    ],
    version = 2,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao
    abstract fun savingsGoalDao(): SavingsGoalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "oduit_database",
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(SeedDatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
