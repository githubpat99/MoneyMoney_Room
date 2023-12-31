package com.nickpatrick.swissmoneysaver.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface BudgetItemsRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllBudgetItemsStream(): Flow<List<BudgetItem>>
    /**
     * Retrieve all the items from the the given year.
     */
    fun getAllBudgetItemsStreamForYearTZ(year: String, timeZoneOffsetInSeconds: Long): Flow<List<BudgetItem>>

    fun getAllBudgetItemsStreamForYear(year: String): Flow<List<BudgetItem>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getBudgetItemStream(id: Int): Flow<BudgetItem?>

    /**
     * Insert item in the data source
     */
    suspend fun insertBudgetItem(budgetItem: BudgetItem)

    /**
     * Delete item from the data source
     */
    suspend fun deleteBudgetItem(budgetItem: BudgetItem)

    /**
     * Delete item from the data source
     */
    suspend fun deleteAllBudgetItems()

    /**
     * Update item in the data source
     */
    suspend fun updateBudgetItem(budgetItem: BudgetItem)

    /**
     * Delete all the items from the the given year.
     */
    suspend fun deleteBudgetItemsForYear(year: String, timeZoneOffsetInSeconds: Long)

}