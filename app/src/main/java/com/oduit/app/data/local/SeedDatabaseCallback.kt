package com.oduit.app.data.local

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.oduit.app.OduitApp
import com.oduit.app.data.local.entity.Account
import com.oduit.app.data.local.entity.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SeedDatabaseCallback : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.getInstance(OduitApp.instance)
            seedCategories(database)
            seedDefaultAccount(database)
        }
    }

    private suspend fun seedCategories(database: AppDatabase) {
        val dao = database.categoryDao()
        dao.insertAll(
            listOf(
                Category(name = "Gaji", type = "income", icon = "work", color = "#4CAF50", isDefault = true),
                Category(name = "Freelance", type = "income", icon = "computer", color = "#2196F3", isDefault = true),
                Category(name = "Investasi", type = "income", icon = "trending_up", color = "#9C27B0", isDefault = true),
                Category(name = "Bonus", type = "income", icon = "celebration", color = "#FF9800", isDefault = true),
                Category(name = "Lain-lain (Masuk)", type = "income", icon = "more_horiz", color = "#607D8B", isDefault = true),
                Category(name = "Transfer", type = "income", icon = "swap_horiz", color = "#009688", isDefault = true),

                Category(name = "Makan & Minum", type = "expense", icon = "restaurant", color = "#F44336", isDefault = true),
                Category(name = "Transportasi", type = "expense", icon = "directions_car", color = "#FF5722", isDefault = true),
                Category(name = "Belanja", type = "expense", icon = "shopping_cart", color = "#E91E63", isDefault = true),
                Category(name = "Tagihan", type = "expense", icon = "receipt", color = "#3F51B5", isDefault = true),
                Category(name = "Hiburan", type = "expense", icon = "sports_esports", color = "#9C27B0", isDefault = true),
                Category(name = "Kesehatan", type = "expense", icon = "local_hospital", color = "#00BCD4", isDefault = true),
                Category(name = "Pendidikan", type = "expense", icon = "school", color = "#795548", isDefault = true),
                Category(name = "Transfer", type = "expense", icon = "swap_horiz", color = "#009688", isDefault = true),
                Category(name = "Lain-lain (Keluar)", type = "expense", icon = "more_horiz", color = "#607D8B", isDefault = true),
            ),
        )
    }

    private suspend fun seedDefaultAccount(database: AppDatabase) {
        val dao = database.accountDao()
        dao.insert(
            Account(name = "Tunai", initialBalance = 0.0, icon = "wallet"),
        )
    }
}
