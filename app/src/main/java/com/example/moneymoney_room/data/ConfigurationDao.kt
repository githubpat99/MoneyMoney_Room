package com.example.moneymoney_room.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigurationDao {

    @Query("SELECT * from configuration WHERE id = 1 LIMIT 1")
    fun getConfiguration(): Flow<Configuration?>

    @Query("UPDATE configuration SET approxStartSaldo = :approxStartSaldo, approxEndSaldo = :approxEndSaldo WHERE id = 1")
    suspend fun updateApproxSaldi(approxStartSaldo: Double, approxEndSaldo: Double)

    @Query("UPDATE configuration " +
            "SET status = CASE " +
            "WHEN status = 0 THEN 1 " +
            "WHEN status = 1 THEN 0 " +
            "ELSE status = 0 " +
            "END WHERE id = 1")
    suspend fun toggleBudgetStatus()


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(configuration: Configuration)
}

