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
import com.example.moneymoney_room.data.Item
import com.example.moneymoney_room.data.ItemsRepository
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
    val configurationRepository: ConfigurationRepository,
    val budgetItemsRepository: BudgetItemsRepository,
    val itemsRepository: ItemsRepository,
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

        updateConfigItem(configuration)
    }

    init {
        viewModelScope.launch {
            configUiState = configurationRepository.getConfigurationForYear(year)
                .filterNotNull()
                .first()
                .toConfigUiState()
        }
    }

    fun updateApproxSaldi(
        approxStartSaldo: Double,
        budgetItems: List<BudgetItem>,
        year: String
    ) {
        configUiState.approxStartSaldo = approxStartSaldo
        configUiState.approxEndSaldo = calculateApproxEndSaldo(approxStartSaldo, budgetItems, year)

        println("BudgetFormViewModel - updateApproxSaldi: $configUiState")

        updateConfigApproxSaldi(approxStartSaldo, approxEndSaldo, year)
    }

    fun calculateApproxEndSaldo(
        approxStartSaldo: Double,
        budgetItems: List<BudgetItem>,
        year: String,
    ): Double {

        val yearInt = year.toInt()

        val endDate = LocalDate.of(yearInt, 12, 31)
        val endOfDayTs = endDate.atTime(LocalTime.MAX).toEpochSecond(ZoneOffset.UTC)

        val startDate = LocalDate.of(yearInt, 1, 1)
        val startOfDayTs = startDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC)

        var totalAmount = approxStartSaldo
        var newItemList = mutableListOf<Item>()
//        var newItem: Item = Item(0, 0, "", "", 0, 0.0, 0.0, false)

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
                    val newItem = Item(0, date, "", item.name, 0, item.amount, 0.0, item.debit)
                    newItemList.add(newItem)


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

    fun reOpenBudgetStatus(year: Int, ts: Long, startSaldo: Double, endSaldo: Double) {
        viewModelScope.launch {

            configurationRepository.reOpenConfigurationForYear(year, ts, startSaldo, endSaldo)

            println("BudgetFormViewModel - reOpenBudgetStatus: $year")

        }
    }

    fun updateConfigItem(configuration: Configuration) {

        println("BudgetFormViewModel - updateConfigItem: $configuration")

        val ts = configuration.ts
        val status = configuration.status
        val budgetYear = configuration.budgetYear
        val password = configuration.password
        val email = configuration.email
        val startSaldo = configuration.startSaldo
        val endSaldo = configuration.endSaldo
        val approxStartSaldo = configuration.approxStartSaldo
        val approxEndSaldo = configuration.approxEndSaldo

        val updateJob = viewModelScope.launch {
            moneyMoneyDatabase.configurationDao().updateConfigurationForYear(
                ts, status, budgetYear, password, email, startSaldo, endSaldo, approxStartSaldo, approxEndSaldo
            )
        }

        updateJob.invokeOnCompletion { // Check when the job completes (either successfully or with an error)
            if (updateJob.isCompleted && updateJob.isCompleted) {
                // Update was successful
                println("Update successful!")
            } else {
                // Update failed
                println("Update failed!")
            }
        }
    }

    fun updateConfigApproxSaldi(approxStartSaldo: Double, approxEndSaldo: Double, year: String) {

        println("BudgetFormViewModel - updateConfigApproxSaldi: $configuration")

        viewModelScope.launch {
            moneyMoneyDatabase.configurationDao().updateApproxSaldi(approxStartSaldo, approxEndSaldo, year)
        }

    }

    suspend fun saveBudgetItem(budgetItem: BudgetItem) {
        budgetItemsRepository.insertBudgetItem(budgetItem)
    }

    suspend fun deleteItemsForYear(year: String) {
        itemsRepository.deleteAllItemsForYear(year)
    }

    suspend fun insertItemsForYear(budgetItems: List<BudgetItem>, year: String) {
        // calculate Items
        addItemsToRepository(budgetItems, year)

        configUiState.startSaldo = configUiState.approxStartSaldo
        configUiState.endSaldo = configUiState.approxEndSaldo
        configUiState.status = 1
        configUiState.ts = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)

        println("BudgetFormViewModel - configUiState: $configUiState")
        updateConfigItem(configUiState)
    }

    suspend fun addItemsToRepository(budgetItems: List<BudgetItem>, year: String) {

        val yearInt = year.toInt()

        val endDate = LocalDate.of(yearInt, 12, 31)
        val endOfDayTs = endDate.atTime(LocalTime.MAX).toEpochSecond(ZoneOffset.UTC)

        val startDate = LocalDate.of(yearInt, 1, 1)
        val startOfDayTs = startDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC)

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
                    itemsRepository.insertItem(newItem)

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
    }
}

data class BudgetItems(
    val list: List<BudgetItem> = mutableListOf(),
)

fun Configuration.toConfigUiState(): Configuration = Configuration(
//    id = id,
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