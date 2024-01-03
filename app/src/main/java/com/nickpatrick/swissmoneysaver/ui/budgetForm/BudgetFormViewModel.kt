package com.nickpatrick.swissmoneysaver.ui.budgetForm

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nickpatrick.swissmoneysaver.MoneyMoneyApplication
import com.nickpatrick.swissmoneysaver.data.BudgetItem
import com.nickpatrick.swissmoneysaver.data.BudgetItemsRepository
import com.nickpatrick.swissmoneysaver.data.Configuration
import com.nickpatrick.swissmoneysaver.data.ConfigurationRepository
import com.nickpatrick.swissmoneysaver.data.Item
import com.nickpatrick.swissmoneysaver.data.ItemsRepository
import com.nickpatrick.swissmoneysaver.data.MoneyMoneyDatabase
import com.nickpatrick.swissmoneysaver.util.Utilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        year: String,
    ) {
        configUiState.approxStartSaldo = approxStartSaldo
        configUiState.approxEndSaldo = Utilities.calculateApproxEndSaldo(approxStartSaldo, budgetItems, year)

        println("BudgetFormViewModel - updateApproxSaldi: $configUiState")

        updateConfigApproxSaldi(approxStartSaldo, configUiState.approxEndSaldo, year)
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
                ts,
                status,
                budgetYear,
                password,
                email,
                startSaldo,
                endSaldo,
                approxStartSaldo,
                approxEndSaldo
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
            moneyMoneyDatabase.configurationDao()
                .updateApproxSaldi(approxStartSaldo, approxEndSaldo, year)
        }

    }

    suspend fun saveBudgetItem(budgetItem: BudgetItem) {
        budgetItemsRepository.insertBudgetItem(budgetItem)
    }

    suspend fun deleteItemsForYear(year: String) {

        println("BudgetFormViewModel - deleteItemsForYear - configUiState: $configUiState")

        itemsRepository.deleteAllItemsForYear(year)
    }

    fun handleYesClick(year: String, budgetStatus: Int, budgetItems: List<BudgetItem>) {
        viewModelScope.launch {
            if (budgetStatus == 0) {
                val items = addItemsToItemList(budgetItems, year)
                deleteItemsForYear(year)
                insertItemsForYear(items)
                // Finally, navigate or update LiveData/state
            } else {
                deleteItemsForYear(year)
                reOpenBudgetStatus(
                    year.toInt(),
                    LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                    0.0, 0.0
                )
            }
        }
    }

    suspend fun insertItemsForYear(items: List<Item>) {

        println("BudgetFormViewModel - insertItemsForYear - before - addItemsToRepository: $items")

        withContext(Dispatchers.IO) {
            for (item in items) {
                try {
                    itemsRepository.insertItem(item)
                    Log.d("BudgetFormViewModel", "Inserted item: ${item.description}")
                } catch (e: Exception) {
                    Log.e("BudgetFormViewModel", "Error inserting item: ${item.description}", e)
                    // Handle the exception (log, notify user, etc.)
                }
            }
        }

        println("BudgetFormViewModel - insertItemsForYear - addItemsToRepository: $items")

        configUiState.startSaldo = configUiState.approxStartSaldo
        configUiState.endSaldo = configUiState.approxEndSaldo
        configUiState.status = 1
        configUiState.ts = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)

        println("BudgetFormViewModel - insertItemsForYear - configUiState: $configUiState")

        updateConfigItem(configUiState)
    }

    fun addItemsToItemList(budgetItems: List<BudgetItem>, year: String): List<Item> {

        val yearInt = year.toInt()

        val endDate = LocalDate.of(yearInt, 12, 31)
        val endOfDayTs = endDate.atTime(LocalTime.MAX).toEpochSecond(ZoneOffset.UTC)

        val startDate = LocalDate.of(yearInt, 1, 1)
        val startOfDayTs = startDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC)

        val items = mutableListOf<Item>()

        println("BudgetFormViewModel - addItemsToRepository - budgetItems: $budgetItems")

        val nextDate: (Long, Int) -> Long = { date, months ->
            LocalDateTime.ofEpochSecond(date, 0, ZoneOffset.UTC)
                .plusMonths(months.toLong())
                .toEpochSecond(ZoneOffset.UTC)
        }

        for (item in budgetItems) {

            println("BudgetFormViewModel - addItemsToRepository - item: ${item.name}")

            var date = item.timestamp
            val nextDateCalculator: (Long) -> Long = {
                when (item.type) {
                    12 -> nextDate(it, 1)
                    6 -> nextDate(it, 2)
                    4 -> nextDate(it, 3)
                    3 -> nextDate(it, 4)
                    2 -> nextDate(it, 6)
                    else -> nextDate(it, 12)
                }
            }

            while (date <= endOfDayTs) {
                if (date >= startOfDayTs) {
                    var amount = item.amount
                    if (item.debit == false) {
                        amount = item.amount * -1
                    }

                    val newItem = Item(0, date * 1000, "", item.name, 0, amount, 0.0, item.debit)

                    println("BudgetFormViewModel - addItemsToRepository - before insert item: ${item.name}")

//                    itemsRepository.insertItem(newItem)

                    items.add(newItem)
                    println("BudgetFormViewModel - addItemsToRepository - after insert item: ${item.name}")

                }
                date = nextDateCalculator(date)
            }
        }

        return items
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