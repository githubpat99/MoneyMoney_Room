package com.example.moneymoney_room.ui.budgetForm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymoney_room.MoneyMoneyApplication
import com.example.moneymoney_room.data.BudgetItem
import com.example.moneymoney_room.data.BudgetItemsRepository
import com.example.moneymoney_room.data.Configuration
import com.example.moneymoney_room.data.ConfigurationRepository
import com.example.moneymoney_room.data.MoneyMoneyDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

class BudgetFormViewModel(
    configurationRepository: ConfigurationRepository,
    private val budgetItemsRepository: BudgetItemsRepository,
    application: MoneyMoneyApplication,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var value = checkNotNull(savedStateHandle[BudgetFormDestination.itemIdArg])

    private val moneyMoneyDatabase = MoneyMoneyDatabase.getDatabase(application)

    val configuration: Flow<Configuration?> =
        moneyMoneyDatabase.configurationDao().getConfiguration()

    var approxStartSaldo by mutableStateOf(0.00)
        private set

    var approxEndSaldo by mutableStateOf(0.00)
        private set

    var budgetItems: StateFlow<BudgetItems> =
        moneyMoneyDatabase.budgetItemDao().getAllItems().map { BudgetItems(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = BudgetItems()
            )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    init {

        viewModelScope.launch {
            configurationRepository.getConfiguration().collect { configuration ->
                configuration?.let {
                    approxStartSaldo = it.approxStartSaldo
                    approxEndSaldo = it.approxEndSaldo

                }
            }
        }
    }

    fun updateApproxSaldi(approxStartSaldo: String, budgetItems: BudgetItems) {
        val approxStartSaldo = approxStartSaldo.toDouble()
        approxEndSaldo = calculateApproxEndSaldo(approxStartSaldo, budgetItems)
        updateConfiguration(approxStartSaldo, approxEndSaldo)
    }

    fun calculateApproxEndSaldo(approxStartSaldo: Double, budgetItems: BudgetItems): Double {

        val endDate = LocalDate.of(2023, 12, 31)
        val endOfDayTs = endDate.atTime(LocalTime.MAX).toEpochSecond(ZoneOffset.UTC)

        val startDate = LocalDate.of(2023, 1, 1)
        val startOfDayTs = startDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC)

        var totalAmount = approxStartSaldo

        val budgetItems = budgetItems.list

        for (item in budgetItems) {

            var itemAmount = 0.0
            var date = item.timestamp
            while (date <= endOfDayTs) {
                if (date >= startOfDayTs) {
                    if (item.debit) {
                        itemAmount += item.amount
                    } else {
                        itemAmount -= item.amount
                    }
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
            totalAmount += itemAmount

            println("BudgetFormViewModel - calculate - totalAmount: $totalAmount / itemAmount: $itemAmount")

        }

        val df = DecimalFormat("#.##")
        approxEndSaldo = df.format(totalAmount).toDouble()
        return approxEndSaldo
    }

    fun toggleBudgetStatus() {
        viewModelScope.launch {

            if (moneyMoneyDatabase != null) {
                moneyMoneyDatabase.configurationDao()
                    .toggleBudgetStatus()
            }
        }
    }
    private fun updateConfiguration(
        approxStartSaldo: Double,
        approxEndSaldo: Double,
    ) {

        viewModelScope.launch {

            if (moneyMoneyDatabase != null) {
                moneyMoneyDatabase.configurationDao()
                    .updateApproxSaldi(approxStartSaldo, approxEndSaldo)
            }
        }
    }

    suspend fun saveBudgetItem(budgetItem: BudgetItem) {
        budgetItemsRepository.insertBudgetItem(budgetItem)

    }

    suspend fun insertBudgetItem(budgetItem: BudgetItem) {

        moneyMoneyDatabase.budgetItemDao().insert(budgetItem)
    }

}

data class BudgetItems(
    val list: List<BudgetItem> = mutableListOf(),
)

