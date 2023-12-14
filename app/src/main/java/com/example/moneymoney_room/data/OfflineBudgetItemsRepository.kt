package com.example.moneymoney_room.data

import kotlinx.coroutines.flow.Flow

class OfflineBudgetItemsRepository(private val budgetItemDao: BudgetItemDao) : BudgetItemsRepository {
    override fun getAllBudgetItemsStream(): Flow<List<BudgetItem>> = budgetItemDao.getAllItems()
    override fun getAllBudgetItemsStreamForYearTZ(year: String, timeZoneOffsetInSeconds: Long): Flow<List<BudgetItem>> =
        budgetItemDao.getBudgetItemsForYearTZ(year, timeZoneOffsetInSeconds)

    override fun getAllBudgetItemsStreamForYear(year: String): Flow<List<BudgetItem>> =
        budgetItemDao.getBudgetItemsForYear(year)

    override fun getBudgetItemStream(id: Int): Flow<BudgetItem?> = budgetItemDao.getItem(id)

    override suspend fun insertBudgetItem(budgetItem: BudgetItem) = budgetItemDao.insert(budgetItem)

    override suspend fun deleteBudgetItem(budgetItem: BudgetItem) = budgetItemDao.delete(budgetItem)

    override suspend fun updateBudgetItem(budgetItem: BudgetItem) = budgetItemDao.update(budgetItem)

    override suspend fun deleteAllBudgetItems() = budgetItemDao.deleteAllBudgetItems()

    override suspend fun deleteBudgetItemsForYear(year: String, timeZoneOffsetInSeconds: Long) =
        budgetItemDao.deleteBudgetItemsForYear(year, timeZoneOffsetInSeconds)

}