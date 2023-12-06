package com.example.moneymoney_room.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Database access object to access the Inventory database
 */
@Dao
interface BudgetItemDao {

    @Query("SELECT * from budgetItems ORDER BY timestamp ASC")
    fun getAllItems(): Flow<List<BudgetItem>>

    @Query("SELECT * FROM budgetItems " +
            "WHERE strftime('%Y', datetime(timestamp, 'unixepoch')) = :year " +
            "ORDER BY timestamp ASC")
    fun getItemsForYear(year: String): Flow<List<BudgetItem>>

    @Query("SELECT * FROM budgetItems " +
            "WHERE strftime('%Y', datetime(timestamp + :timeZoneOffsetInSeconds, 'unixepoch')) = :year " +
            "ORDER BY timestamp ASC, id DESC")
    fun getBudgetItemsForYear(year: String, timeZoneOffsetInSeconds: Long): Flow<List<BudgetItem>>



    @Query("SELECT * from budgetItems WHERE id = :id")
    fun getItem(id: Int): Flow<BudgetItem>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budgetItem: BudgetItem)

    @Update
    suspend fun update(budgetItem: BudgetItem)

    @Delete
    suspend fun delete(budgetItem: BudgetItem)

    // Bulk Insert for a specific Account
    @Query("DELETE from budgetItems")
    suspend fun deleteAllBudgetItems()
}