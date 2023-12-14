package com.example.moneymoney_room.ui.MonthlyDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymoney_room.MoneyMoneyApplication
import com.example.moneymoney_room.data.Configuration
import com.example.moneymoney_room.data.ConfigurationRepository
import com.example.moneymoney_room.data.Item
import com.example.moneymoney_room.data.ItemsRepository
import com.example.moneymoney_room.data.MoneyMoneyDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

/**
 * ViewModel to retrieve all items in the Room database.
 */

class MonthlyDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    itemsRepository: ItemsRepository,
    val configurationRepository: ConfigurationRepository,
    application: MoneyMoneyApplication
) :
    ViewModel() {

    val itemsRepository = itemsRepository
    private val moneyMoneyDatabase = MoneyMoneyDatabase.getDatabase(application)

    var value = checkNotNull(savedStateHandle[MonthlyDetailDestination.Year])
    val year = value
    val yearString = "$year"
    val configuration: Flow<Configuration?> =
        moneyMoneyDatabase.configurationDao().getConfigurationForYear(yearString)
    var value2 = checkNotNull(savedStateHandle[MonthlyDetailDestination.Month])
    val month = value2
    var value3 = checkNotNull(savedStateHandle[MonthlyDetailDestination.EndSaldo])
    val endSaldo: Double = value3.toString().toDouble()
    var value4 = checkNotNull(savedStateHandle[MonthlyDetailDestination.MonthlyTotal])
    val monthlyTotal: Double = value4.toString().toDouble()

    val yearAndMonth = "$year$month".toInt()


    val timestamps = getFirstAndLastTimestamp(yearAndMonth)
    val firstTimestamp = timestamps.first
    val lastTimestamp = timestamps.second

    val monthlyDetailsUiState: StateFlow<MonthlyDetailsUiState> =
        itemsRepository.getAllItemsStream()
            .map { items ->
                val filteredItems = items.filter { item ->
                    val timestamp = item.timestamp
                    timestamp in firstTimestamp..lastTimestamp
                }
                MonthlyDetailsUiState(filteredItems)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = MonthlyDetailsUiState()
            )

    init {

        viewModelScope.launch(Dispatchers.IO) {
            configurationRepository.updateConfigurationEndSaldoForYear(yearString, endSaldo)

        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    suspend fun saveItem(item: Item) {
        itemsRepository.insertItem(item)
    }
}

data class MonthlyDetailsUiState(
    val list: List<Item> = listOf(),
)

fun getFirstAndLastTimestamp(yearAndMonth: Int): Pair<Long, Long> {
    val year = yearAndMonth / 100 // Extract year (first 4 digits)
    val month = yearAndMonth % 100 // Extract month (last 2 digits)

    val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    calendar.clear() // Clear any existing values

    // Set the calendar to the first day of the specified year and month
    calendar.set(Calendar.YEAR, year)
    calendar.set(Calendar.MONTH, month - 1) // Calendar months are 0-based, so -1
    calendar.set(Calendar.DAY_OF_MONTH, 1)

    val firstTimestamp = calendar.timeInMillis // Timestamp for the first day

    // Set the calendar to the last day of the specified year and month
    calendar.add(Calendar.MONTH, 1) // Move to the next month
    calendar.add(Calendar.DATE, -1) // Move to the last day of the previous month

    val lastTimestamp = calendar.timeInMillis // Timestamp for the last day

    return Pair(firstTimestamp, lastTimestamp)
}