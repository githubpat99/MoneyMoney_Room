package com.example.moneymoney_room.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "configurations")
data class Configuration(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1,
    val userName: String,
    val password: String,
    val email: String,
    val startSaldo: Double,
)
