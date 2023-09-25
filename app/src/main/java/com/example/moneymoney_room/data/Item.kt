package com.example.moneymoney_room.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity data class represents a single row in the database.
 */
@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: Long,
    val name: String,
    val description: String,
    val type: Int,
    val amount: Double,
    val balance: Double,
    val debit: Boolean
)
