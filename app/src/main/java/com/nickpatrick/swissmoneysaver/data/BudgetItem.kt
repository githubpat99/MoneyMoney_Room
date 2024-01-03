package com.nickpatrick.swissmoneysaver.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity data class represents a single row in the database.
 */
@Entity(tableName = "budgetItems")
data class BudgetItem(
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
