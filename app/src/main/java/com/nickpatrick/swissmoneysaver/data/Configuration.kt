package com.nickpatrick.swissmoneysaver.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "configuration")
data class Configuration(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var ts: Long = 1672527600,
    var status: Int = 0,
    val budgetYear: Int = 2023,
    val password: String = "",
    val email: String = "",
    var startSaldo: Double = 0.0,
    var endSaldo: Double = 0.0,
    var approxStartSaldo: Double = 0.0,
    var approxEndSaldo: Double = 0.0
)
