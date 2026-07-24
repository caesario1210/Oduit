package com.oduit.app.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = Account::class,
            parentColumns = ["id"],
            childColumns = ["accountId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [
        Index("accountId"),
        Index("categoryId"),
        Index("date"),
    ],
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val accountId: Long = 1,
    val categoryId: Long? = null,
    val amount: Double,
    val type: String, // "income" or "expense" — direction
    @ColumnInfo(name = "tipe_transaksi")
    val transactionType: String = "cashflow", // "cashflow" or "transfer"
    val date: Long, // epoch millis
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)
