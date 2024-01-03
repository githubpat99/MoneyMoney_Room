package com.nickpatrick.swissmoneysaver.data

import android.content.Context

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val itemsRepository: ItemsRepository
    val budgetItemsRepository: BudgetItemsRepository
    val configurationRepository: ConfigurationRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineItemsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [ItemsRepository]
     */
    override val itemsRepository: ItemsRepository by lazy {
        OfflineItemsRepository(MoneyMoneyDatabase.getDatabase(context).itemDao())
    }

    override val budgetItemsRepository: BudgetItemsRepository by lazy {
        OfflineBudgetItemsRepository(MoneyMoneyDatabase.getDatabase(context).budgetItemDao())
    }

    override val configurationRepository: ConfigurationRepository by lazy {
        ConfigurationRepository(MoneyMoneyDatabase.getDatabase(context).configurationDao())
    }
}