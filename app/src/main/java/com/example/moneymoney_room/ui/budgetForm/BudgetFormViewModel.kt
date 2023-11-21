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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

class BudgetFormViewModel(
    configurationRepository: ConfigurationRepository,
    val budgetItemsRepository: BudgetItemsRepository,
    application: MoneyMoneyApplication,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var configUiState by mutableStateOf(Configuration())
        private set

    var year = checkNotNull(savedStateHandle[BudgetFormDestination.year]) as String
    var tab = checkNotNull(savedStateHandle[BudgetFormDestination.tab]) as String

    private val moneyMoneyDatabase = MoneyMoneyDatabase.getDatabase(application)

    val configuration: Flow<Configuration?> =
        moneyMoneyDatabase.configurationDao().getConfigurationForYear(year)

    var approxStartSaldo by mutableStateOf(0.00)
        private set

    var approxEndSaldo by mutableStateOf(0.00)
        private set

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun updateConfigUiState(configuration: Configuration) {

        configUiState = configuration
        configUiState.approxStartSaldo = configuration.approxStartSaldo
        configUiState.approxEndSaldo = configuration.approxEndSaldo

        updateConfiguration(configUiState.approxStartSaldo, configUiState.approxEndSaldo, configUiState.budgetYear.toString())
    }

    init {
        viewModelScope.launch {
            configUiState = configurationRepository.getConfigurationForYear(year)
                .filterNotNull()
                .first()
                .toConfigUiState()
        }
    }

    fun updateApproxSaldi(approxStartSaldo: Double, budgetItems: List<BudgetItem>, year: String) {

        approxEndSaldo = calculateApproxEndSaldo(approxStartSaldo, budgetItems, year)
        updateConfiguration(approxStartSaldo, approxEndSaldo, year)
    }

    fun calculateApproxEndSaldo(approxStartSaldo: Double, budgetItems: List<BudgetItem>, year: String): Double {

        val yearInt = year.toInt()

        val endDate = LocalDate.of(yearInt, 12, 31)
        val endOfDayTs = endDate.atTime(LocalTime.MAX).toEpochSecond(ZoneOffset.UTC)

        val startDate = LocalDate.of(yearInt, 1, 1)
        val startOfDayTs = startDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC)

        var totalAmount = approxStartSaldo

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
        }

        val df = DecimalFormat("#.##")
        approxEndSaldo = df.format(totalAmount).toDouble()
        return approxEndSaldo
    }

    fun toggleBudgetStatus(year: Int, ts: Long) {
        viewModelScope.launch {

            if (moneyMoneyDatabase != null) {
                moneyMoneyDatabase.configurationDao()
                    .toggleBudgetStatus(year, ts)
            }
        }
    }
    private fun updateConfiguration(
        approxStartSaldo: Double,
        approxEndSaldo: Double,
        year: String
    ) {

        this.approxStartSaldo = approxStartSaldo
        this.approxEndSaldo = approxEndSaldo

        viewModelScope.launch {

            if (moneyMoneyDatabase != null) {
                moneyMoneyDatabase.configurationDao()
                    .updateApproxSaldi(approxStartSaldo, approxEndSaldo, year)
            }
        }
    }

    suspend fun saveBudgetItem(budgetItem: BudgetItem) {
        budgetItemsRepository.insertBudgetItem(budgetItem)
    }
}

data class BudgetItems(
    val list: List<BudgetItem> = mutableListOf(),
)

fun Configuration.toConfigUiState(): Configuration = Configuration(
    id = id,
    ts = ts,
    status = status,
    budgetYear = budgetYear,
    password = password,
    email = email,
    startSaldo = startSaldo,
    endSaldo = endSaldo,
    approxStartSaldo = approxStartSaldo,
    approxEndSaldo = approxEndSaldo
)