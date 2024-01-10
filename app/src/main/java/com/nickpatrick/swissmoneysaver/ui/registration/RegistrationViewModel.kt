package com.nickpatrick.swissmoneysaver.ui.registration

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nickpatrick.swissmoneysaver.MoneyMoneyApplication
import com.nickpatrick.swissmoneysaver.data.BudgetItem
import com.nickpatrick.swissmoneysaver.data.BudgetItemsRepository
import com.nickpatrick.swissmoneysaver.data.Configuration
import com.nickpatrick.swissmoneysaver.data.ItemsRepository
import com.nickpatrick.swissmoneysaver.data.MoneyMoneyDatabase
import com.nickpatrick.swissmoneysaver.util.Utilities
import com.nickpatrick.swissmoneysaver.util.Utilities.Companion.readJsonFromAssets
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.TimeZone

class RegistrationViewModel(
    private val budgetItemsRepository: BudgetItemsRepository,
    private val itemsRepository: ItemsRepository,
    application: MoneyMoneyApplication,
) : ViewModel() {

    val context = application.applicationContext
    private val moneyMoneyDatabase = MoneyMoneyDatabase.getDatabase(application)

    val configuration: Flow<Configuration?> =
        moneyMoneyDatabase.configurationDao().getConfiguration()

    val configItems = moneyMoneyDatabase.configurationDao().getConfigurations()

    // Initialize registrationUiState with values from configuration
    var registrationUiState: MutableState<Configuration> = mutableStateOf(
        Configuration()
    )
    val utcTimeZone = TimeZone.getTimeZone("UTC")
    private val utcOffsetInSeconds = utcTimeZone.rawOffset / 1000 // Convert milliseconds to seconds

    private val _messageLiveData = MutableLiveData<String>()
    val messageLiveData: LiveData<String>
        get() = _messageLiveData

    init {
        viewModelScope.launch {
            val configurationValue = configuration.first() // Wait for the first value
            registrationUiState.value = Configuration(
                startSaldo = configurationValue?.startSaldo ?: 0.0,
                budgetYear = configurationValue?.budgetYear ?: 0,
                password = configurationValue?.password ?: "",
                email = configurationValue?.email ?: ""
            )
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    fun updateUiState(configuration: Configuration) {
        this.registrationUiState.value = configuration
    }


    fun updateConfiguration(configuration: Configuration) {

        println("RegistrationViewModel - updateConfiguration - configuration: $configuration")

        val nowTs = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        val moneyMoneyDatabase = MoneyMoneyDatabase.getDatabase(context)

        // Init to now and open for Insert and Update
        configuration.ts = nowTs
        configuration.status = 0

        val ts = configuration.ts
        val status = configuration.status
        val budgetYear = configuration.budgetYear
        val password = configuration.password
        val email = configuration.email
        val startSaldo = configuration.startSaldo
        val endSaldo = configuration.endSaldo
        val approxStartSaldo = configuration.approxStartSaldo
        val approxEndSaldo = configuration.approxEndSaldo



        viewModelScope.launch {

            println("RegistrationViewModel - updateConfiguration - viewModelScope")

            val config = moneyMoneyDatabase.configurationDao()
                .getConfigurationForYear(configuration.budgetYear.toString())
                .firstOrNull()

            if (config != null) {
                // update
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
            } else {
                // insert
                moneyMoneyDatabase.configurationDao().insert(configuration)
            }
        }
    }


    fun copyBudgetFromTo(budgetYear: Int, selectedYear: Int) {

        var newConfig = Configuration()

        // update selected Budget Year
        // delete BudgetItems from selected Year

        viewModelScope.launch {
            var budgetClosed = false

            val configItemsList = configItems.take(1).toList()

            for (configList in configItemsList) {
                for (config in configList) {
                    if (config != null) {
                        if (config != null && config.budgetYear == selectedYear && config.status == 1) {
                            budgetClosed = true
                            break
                        }
                        if (config != null && config.budgetYear == budgetYear) {
                            newConfig = config.copy(
                                id = 0,
                                budgetYear = selectedYear,
                                startSaldo = config.startSaldo,
                                endSaldo = config.endSaldo,
                                approxStartSaldo = config.approxStartSaldo,
                                approxEndSaldo = config.approxEndSaldo
                            )
                        }
                    }
                }
            }

            if (budgetClosed) {
                // this budget must be re-Opened first
                _messageLiveData.value = "Budget ${selectedYear} Status geschlossen"
            } else {
                budgetItemsRepository.deleteBudgetItemsForYear(
                    selectedYear.toString(),
                    utcOffsetInSeconds.toLong()
                )

                val sourceBudgetItems = mutableListOf<BudgetItem>()

                val flowListBudgetItem = budgetItemsRepository
                    .getAllBudgetItemsStreamForYearTZ(
                        budgetYear.toString(),
                        utcOffsetInSeconds.toLong()
                    )

                flowListBudgetItem.take(1).collect {
                    sourceBudgetItems.addAll(it)
                    sourceBudgetItems.map {

                        // Convert existing timestamp to Instant
                        val instant: Instant = Instant.ofEpochSecond(it.timestamp)

                        // Convert Instant to LocalDateTime
                        val existingDateTime: LocalDateTime =
                            LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

                        // Calculate the years to add
                        val yearDiff = budgetYear - selectedYear
                        if (yearDiff < 0) yearDiff * -1

                        // Add one year to the existing timestamp
                        val updatedDateTime: LocalDateTime =
                            existingDateTime.plus(-yearDiff.toLong(), ChronoUnit.YEARS)

                        // Convert updated LocalDateTime back to Instant
                        val updatedInstant: Instant =
                            updatedDateTime.atZone(ZoneId.systemDefault()).toInstant()

                        // Get the updated timestamp after adding one year
                        val updatedTimestamp: Long = updatedInstant.epochSecond

                        BudgetItem(
                            id = 0,
                            timestamp = updatedTimestamp,
                            name = it.name,
                            description = it.description,
                            type = it.type,
                            amount = it.amount,
                            balance = it.balance,
                            debit = it.debit
                        )
                    }.forEach { budgetItem ->
                        budgetItemsRepository.insertBudgetItem(budgetItem)

                    }
                }

                if (!budgetClosed) {
                    // Only update the returnMsg if the budget is not closed

                    var msgTxt = ""

                    println("RegistrationViewModel - Locale.current.language: ${Locale.current.language}")

                    when (Locale.current.language) {
                        "de" -> msgTxt = "Budget $budgetYear erfolgreich auf $selectedYear übertragen"
                        "en" -> msgTxt = "Budget $budgetYear successfully transferred to $selectedYear"
                        "fr" -> msgTxt = "Budget $budgetYear reporté avec succès sur $selectedYear"
                        "it" -> msgTxt = "Bilancio $budgetYear trasferito con successo al $selectedYear"
                        else -> msgTxt = "Budget $budgetYear -> $selectedYear ..."
                    }

                    _messageLiveData.value = msgTxt

                }

                updateConfiguration(newConfig)
            }
        }
    }

    fun generateBudgetFromToJson(budgetYear: Int, budgetName: String) {

        deleteBudgetOfYear(budgetYear)
        val sourceBudgetItems = mutableListOf<BudgetItem>()

        val flowListBudgetItem = budgetItemsRepository
            .getAllBudgetItemsStreamForYearTZ(
                budgetYear.toString(),
                utcOffsetInSeconds.toLong()
            )
        viewModelScope.launch {
            flowListBudgetItem.take(1).collect {
                sourceBudgetItems.addAll(it)
            }

            val fileName = budgetName + "_" + Locale.current.language + ".json"
            val jsonData = readJsonFromAssets(context,  fileName)

            val gson = Gson()
            val itemsList: List<BudgetItem> = gson.fromJson(jsonData, object : TypeToken<List<BudgetItem>>() {}.type)

            itemsList.forEach {

                val budgetItem = it.copy( timestamp = Utilities.addYearToTimestamp(it.timestamp, budgetYear))
                budgetItemsRepository.insertBudgetItem(budgetItem)
            }

            val approxEndSaldo = Utilities.calculateApproxEndSaldo(0.0, itemsList, budgetYear.toString())

            var config = Configuration(0,0,0,budgetYear,"","",0.0, 0.0, 0.0, approxEndSaldo)

            // updates Config if available, otherwise insert
            updateConfiguration(config)
        }
    }

    fun deleteBudgetOfYear(budgetYear: Int) {
        viewModelScope.launch {
            budgetItemsRepository.deleteBudgetItemsForYear(budgetYear.toString(), utcOffsetInSeconds.toLong())

            // Must also delete all existing Items for this year
            itemsRepository.deleteAllItemsForYear(budgetYear.toString())

            var config = Configuration(0,0,0,budgetYear,"","",0.0, 0.0, 0.0, 0.0)

            // updates Config if available, otherwise insert
            updateConfiguration(config)
        }
    }

    fun initializeAllData() {
        viewModelScope.launch {
            itemsRepository.deleteAllItems()
            budgetItemsRepository.deleteAllBudgetItems()
            moneyMoneyDatabase.configurationDao().deleteAllConfigurations()
        }

    }
}
