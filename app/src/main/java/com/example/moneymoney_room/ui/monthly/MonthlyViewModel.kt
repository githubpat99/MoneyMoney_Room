package com.example.moneymoney_room.ui.monthly

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymoney_room.MoneyMoneyApplication
import com.example.moneymoney_room.data.Configuration
import com.example.moneymoney_room.data.Item
import com.example.moneymoney_room.data.ItemsRepository
import com.example.moneymoney_room.data.MoneyMoneyDatabase
import com.example.moneymoney_room.ui.budgetForm.BudgetFormDestination
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel to retrieve all items in the Room database.
 */

class MonthlyViewModel(
    val itemsRepository: ItemsRepository,
    application: MoneyMoneyApplication,
    savedStateHandle: SavedStateHandle
) :ViewModel() {

    private val moneyMoneyDatabase = MoneyMoneyDatabase.getDatabase(application)
    var year = checkNotNull(savedStateHandle[BudgetFormDestination.year]) as String

    val configuration: Flow<Configuration?> =
        moneyMoneyDatabase.configurationDao().getConfigurationForYear(year)

    val context: Context = application.applicationContext

    val monthlyUiState: StateFlow<MonthlyUiState> =
        itemsRepository.getAllItemsStreamForYear(year).map { MonthlyUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = MonthlyUiState()
            )


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class MonthlyUiState(
    val list: List<Item> = listOf(),
)

