package com.example.moneymoney_room.ui.MonthlyDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymoney_room.MoneyMoneyApplication
import com.example.moneymoney_room.data.Configuration
import com.example.moneymoney_room.data.Item
import com.example.moneymoney_room.data.ItemsRepository
import com.example.moneymoney_room.data.MoneyMoneyDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel to retrieve all items in the Room database.
 */

class MonthlyDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    itemsRepository: ItemsRepository,
    application: MoneyMoneyApplication
) :
    ViewModel() {

    val itemsRepository = itemsRepository
    private val moneyMoneyDatabase = MoneyMoneyDatabase.getDatabase(application)
    val configuration: Flow<Configuration?> =
        moneyMoneyDatabase.configurationDao().getConfiguration()

    private val monthId: Int = checkNotNull(savedStateHandle[MonthlyDetailDestination.itemIdArg])

    init {
        println("MonthlyDetailsViewModel - monthId = $monthId")
    }

    // todo PIN:
    // save only Items of Specific Month (monthId) in monthlyUiState
    val monthlyUiState: StateFlow<MonthlyDetailsUiState> =
        itemsRepository.getAllItemsStream().map { MonthlyDetailsUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = MonthlyDetailsUiState()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L

    }
}

data class MonthlyDetailsUiState(
    val list: List<Item> = listOf(),
)