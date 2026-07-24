package com.oduit.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: String, // "income" or "expense"
    val icon: String = "category",
    val color: String = "#009688",
    val isDefault: Boolean = false,
)
