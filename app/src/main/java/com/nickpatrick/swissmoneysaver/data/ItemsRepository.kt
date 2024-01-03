package com.nickpatrick.swissmoneysaver.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface ItemsRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllItemsStream(): Flow<List<Item>>
    /**
     * Retrieve all the items from the the given data source for specific year.
     */
    fun getAllItemsStreamForYear(year: String): Flow<List<Item>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getItemStream(id: Int): Flow<Item?>

    /**
     * Insert item in the data source
     */
    suspend fun insertItem(item: Item)

    /**
     * Delete item from the data source
     */
    suspend fun deleteItem(item: Item)

    /**
     * Delete allItems from the data source
     */
    suspend fun deleteAllItems()

    /**
     * Delete allItems from the data source
     */
    suspend fun deleteAllItemsForYear(year: String)

    /**
     * Update item in the data source
     */
    suspend fun updateItem(item: Item)

}