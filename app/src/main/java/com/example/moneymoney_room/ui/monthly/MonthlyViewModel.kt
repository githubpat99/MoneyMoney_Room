package com.example.moneymoney_room.ui.monthly

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymoney_room.MoneyMoneyApplication
import com.example.moneymoney_room.data.BudgetItem
import com.example.moneymoney_room.data.BudgetItemsRepository
import com.example.moneymoney_room.data.Configuration
import com.example.moneymoney_room.data.ConfigurationRepository
import com.example.moneymoney_room.data.Item
import com.example.moneymoney_room.data.ItemsRepository
import com.example.moneymoney_room.data.MoneyMoneyDatabase
import com.example.moneymoney_room.ui.budgetForm.BudgetFormDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

/**
 * ViewModel to retrieve all items in the Room database.
 */

class MonthlyViewModel(
    val itemsRepository: ItemsRepository,
    private val configurationRepository: ConfigurationRepository,
    val budgetItemsRepository: BudgetItemsRepository,
    application: MoneyMoneyApplication,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val moneyMoneyDatabase = MoneyMoneyDatabase.getDatabase(application)
    var year = checkNotNull(savedStateHandle[BudgetFormDestination.year]) as String
    val appTimezone = application.appTimeZone
    val tzMillis = appTimezone.rawOffset
    val timezoneLongSeconds: Long = tzMillis / 1000L

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

    val budgetUiState: StateFlow<BudgetUiState> =
        budgetItemsRepository.getAllBudgetItemsStreamForYear(year, timezoneLongSeconds).map { BudgetUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = BudgetUiState()
            )


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun updateConfigEndSaldoForYear(endSaldo: Double ) {

        viewModelScope.launch(Dispatchers.IO) {
            configurationRepository.updateConfigurationEndSaldoForYear(year, endSaldo)
        }

    }

    fun reCalculateBudgetForMonthlyView(budgetItems: List<BudgetItem>, year: String) : MutableList<Item> {

        val yearInt = year.toInt()

        val endDate = LocalDate.of(yearInt, 12, 31)
        val endOfDayTs = endDate.atTime(LocalTime.MAX).toEpochSecond(ZoneOffset.UTC)

        val startDate = LocalDate.of(yearInt, 1, 1)
        val startOfDayTs = startDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC)

        val budgetItemList = mutableListOf<Item>()

        for (item in budgetItems) {

            var itemAmount = 0.0
            var date = item.timestamp
            while (date <= endOfDayTs) {
                if (date >= startOfDayTs) {
                    var amount = item.amount
                    if (item.debit == false) {
                        amount = item.amount * -1
                    }

                    val newItem = Item(0, date * 1000, "", item.name, 0, amount, 0.0, item.debit)
                    budgetItemList.add(newItem)

                }
                date = when (item.type) {
                    12 -> LocalDateTime.ofEpochSecond(date, 0, ZoneOffset.UTC).plusMonths(1)
                        .toEpochSecond(ZoneOffset.UTC)

                    6 -> LocalDateTime.ofEpochSecond(date, 0, ZoneOffset.UTC).plusMonths(2)
                        .toEpochSecond(ZoneOffset.UTC)

                    4 -> LocalDateTime.ofEpochSecond(date, 0, ZoneOffset.UTC).plusMonths(3)
                        .toEpochSecond(ZoneOffset.UTC)

                    3 -> LocalDateTime.ofEpochSecond(date, 0, ZoneOffset.UTC).plusMonths(4)
                        .toEpochSecond(ZoneOffset.UTC)

                    2 -> LocalDateTime.ofEpochSecond(date, 0, ZoneOffset.UTC).plusMonths(6)
                        .toEpochSecond(ZoneOffset.UTC)

                    else -> LocalDateTime.ofEpochSecond(date, 0, ZoneOffset.UTC).plusMonths(12)
                        .toEpochSecond(ZoneOffset.UTC)
                }
            }
        }
        return budgetItemList
    }
}

data class MonthlyUiState(
    val list: List<Item> = listOf(),
)

data class BudgetUiState(
    val list: List<BudgetItem> = mutableListOf()
)

