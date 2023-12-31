package com.nickpatrick.swissmoneysaver.data

import kotlinx.coroutines.flow.Flow

class OfflineItemsRepository(private val itemDao: ItemDao) : ItemsRepository {
    override fun getAllItemsStream(): Flow<List<Item>> = itemDao.getAllItems()

    override fun getAllItemsStreamForYear(year: String): Flow<List<Item>> = itemDao.getAllItemsForYear(year)

    override fun getItemStream(id: Int): Flow<Item?> = itemDao.getItem(id)

    override suspend fun insertItem(item: Item) = itemDao.insert(item)

    override suspend fun deleteItem(item: Item) = itemDao.delete(item)

    override suspend fun updateItem(item: Item) = itemDao.update(item)

    override suspend fun deleteAllItems() = itemDao.deleteAllItems()

    override suspend fun deleteAllItemsForYear(year: String) = itemDao.deleteAllItemsForYear(year)
}