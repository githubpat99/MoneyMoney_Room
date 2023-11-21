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

    @Query("SELECT * from configuration WHERE budgetYear = :budgetYear LIMIT 1")
    fun getConfigurationForYear(budgetYear: String): Flow<Configuration?>

    @Query("SELECT * from configuration")
    fun getConfigurations(): Flow<List<Configuration?>>

    @Query("UPDATE configuration SET approxStartSaldo = :approxStartSaldo, approxEndSaldo = :approxEndSaldo WHERE budgetYear = :budgetYear")
    suspend fun updateApproxSaldi(approxStartSaldo: Double, approxEndSaldo: Double, budgetYear: String)

    @Query("UPDATE configuration " +
            "SET status = CASE " +
            "WHEN status = 0 THEN 1 " +
            "WHEN status = 1 THEN 0 " +
            "ELSE status = 0 END" +
            ", ts = :ts " +
            "WHERE budgetYear = :year")
    suspend fun toggleBudgetStatus(year: Int, ts: Long)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(configuration: Configuration)
}

