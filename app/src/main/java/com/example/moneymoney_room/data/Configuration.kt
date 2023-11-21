package com.example.moneymoney_room.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "configuration")
data class Configuration(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 1,
    val ts: Long = 1672527600,
    val status: Int = 0,
    val budgetYear: Int = 2023,
    val password: String = "",
    val email: String = "",
    val startSaldo: Double = 0.0,
    val endSaldo: Double = 0.0,
    var approxStartSaldo: Double = 0.0,
    var approxEndSaldo: Double = 0.0
)
